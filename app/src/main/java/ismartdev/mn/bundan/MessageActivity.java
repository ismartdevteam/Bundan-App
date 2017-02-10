package ismartdev.mn.bundan;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.HashMap;
import java.util.Map;

import ismartdev.mn.bundan.models.Messages;
import ismartdev.mn.bundan.util.CircleImageView;
import ismartdev.mn.bundan.util.Constants;

public class MessageActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MessageActivity";
    private String match_id;
    private String match_name;
    private String match_user_img;
    private String match_uid;
    private LinearLayout lv;
    private ActionBar actionBar;
    Bundle b;
    private TextView send;
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_messagedetail_list);
        JodaTimeAndroid.init(this);
        send = (TextView) findViewById(R.id.chat_send);
        message = (EditText) findViewById(R.id.chat_edit);
        b = getIntent().getExtras();
        match_id = b.getString("matchID");
        match_uid = b.getString("uid");
        match_name = b.getString("match_name");
        match_user_img = b.getString("match_user_img");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(match_name);
        lv = (LinearLayout) findViewById(R.id.list);
        send.setOnClickListener(this);
        ref.child(Constants.user_matches + "/" + match_id + "/messages").addChildEventListener(childEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
        childUpdates.put(Constants.user_matches + "/" + match_id + "/messages" +"/"+ newMessagekey, messages.toMap());
        childUpdates.put(Constants.user_matches + "/" + match_id + "/last_message", messages.toMap());
        ref.updateChildren(childUpdates);

    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
            Messages message = dataSnapshot.getValue(Messages.class);
            if (message != null) {
                createMessageView(message);
            }
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
