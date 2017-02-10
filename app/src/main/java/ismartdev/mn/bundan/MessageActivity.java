package ismartdev.mn.bundan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.HashMap;
import java.util.Map;

import ismartdev.mn.bundan.models.MatchPost;
import ismartdev.mn.bundan.models.MessagePost;
import ismartdev.mn.bundan.models.Messages;
import ismartdev.mn.bundan.models.User;
import ismartdev.mn.bundan.util.ApiClient;
import ismartdev.mn.bundan.util.ApiInterface;
import ismartdev.mn.bundan.util.CircleImageView;
import ismartdev.mn.bundan.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MessageActivity";
    private String match_id;
    private String match_name;
    private String match_user_img;
    private String match_uid;
    private LinearLayout lv;
    private ScrollView scrollView;
    private ActionBar actionBar;
    Bundle b;
    private TextView send;
    private EditText message;
    private SharedPreferences sharedPreferences;
    private ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_messagedetail_list);
        sharedPreferences = getSharedPreferences(Constants.sp_app, MODE_PRIVATE);
        scrollView = (ScrollView) findViewById(R.id.message_scroll);

        JodaTimeAndroid.init(this);
        send = (TextView) findViewById(R.id.chat_send);
        message = (EditText) findViewById(R.id.chat_edit);
        b = getIntent().getExtras();
        match_id = b.getString("matchID");
        sharedPreferences.edit().putString("matchID", match_id).commit();
        match_uid = b.getString("uid");

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(match_name);
        lv = (LinearLayout) findViewById(R.id.list);
        send.setOnClickListener(this);
        ref.child(Constants.user + "/" + match_uid + "/" + Constants.user_info).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    match_name = user.name;
                    match_user_img = user.picture.get(0);
                    ref.child(Constants.user_matches + "/" + match_id + "/messages").addChildEventListener(childEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendPushNotificationMessage(String uid, final String matchID, String name, String message) {
        apiService =
                ApiClient.getClient(Constants.url).create(ApiInterface.class);
        Call<MessagePost> call = apiService.messagePush(new MessagePost(uid, matchID, message, name));
        call.enqueue(new Callback<MessagePost>() {
            @Override
            public void onResponse(Call<MessagePost> call, Response<MessagePost> response) {
                if (response.body().getCode() == 200) {
                    Log.e("Success chat ", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<MessagePost> call, Throwable t) {
                Log.e("onFailure matchPush ", t.toString());
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ref.removeEventListener(childEventListener);
        sharedPreferences.edit().putString("matchID", "").commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == send) {
            if (!TextUtils.isEmpty(message.getText())) {
                sendMsg(message.getText().toString());
            }
        }
    }


    private void sendMsg(final String messageStr) {
        Messages messages = new Messages();
        messages.senderId = getUid();
        messages.sendDate = ServerValue.TIMESTAMP;
        messages.message = messageStr;
        String newMessagekey = ref.child(Constants.user_matches + "/" + match_id + "/messages").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(Constants.user_matches + "/" + match_id + "/messages" + "/" + newMessagekey, messages.toMap());
        childUpdates.put(Constants.user_matches + "/" + match_id + "/last_message", messages.toMap());
        ref.updateChildren(childUpdates);

        message.setText("");

        sendPushNotificationMessage(match_uid, match_id, match_name, messageStr);

    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
            Messages message = dataSnapshot.getValue(Messages.class);
            if (message != null) {
                createMessageView(message);
            }
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "postComments:onCancelled", databaseError.toException());
        }
    };


    private void createMessageView(Messages message) {
        View view = null;
        if (message.senderId.equals(getUid())) {
            view = getLayoutInflater().inflate(R.layout.user_messenger_item, null);
        } else {
            view = getLayoutInflater().inflate(R.layout.friend_messenger_item, null);
            final CircleImageView imageView = (CircleImageView) view.findViewById(R.id.message_img);
            Picasso.with(MessageActivity.this).load(match_user_img)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        }
        TextView text = (TextView) view.findViewById(R.id.message_text);
        text.setText(message.message);
        TextView date = (TextView) view.findViewById(R.id.message_date);
        date.setText(getAgoDate(message.date));
        lv.addView(view);
    }

    private String getAgoDate(long date) {
        DateTime myBirthDate = new DateTime(date);
        DateTime now = new DateTime();
        Period period = new Period(myBirthDate, now);

        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendSeconds().appendSuffix(" seconds ago\n")
                .appendMinutes().appendSuffix(" minutes ago\n")
                .appendHours().appendSuffix(" hours ago\n")
                .appendDays().appendSuffix(" days ago\n")
                .appendWeeks().appendSuffix(" weeks ago\n")
                .appendMonths().appendSuffix(" months ago\n")
                .appendYears().appendSuffix(" years ago\n")
                .printZeroNever()
                .toFormatter();

        String elapsed = formatter.print(period);
        return elapsed;
    }

}
