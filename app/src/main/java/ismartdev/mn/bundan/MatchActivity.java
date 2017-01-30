package ismartdev.mn.bundan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import ismartdev.mn.bundan.models.MatchedUser;
import ismartdev.mn.bundan.util.CircleImageView;
import ismartdev.mn.bundan.util.Constants;

public class MatchActivity extends BaseActivity implements View.OnClickListener {
    private Bundle b;
    private CircleImageView myPic;
    private CircleImageView friendPic;
    private TextView matchedText;
    private Button sendMessage;
    private Button keepSwipe;
    private String uid;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_match);
        sharedPreferences = getSharedPreferences(Constants.sp_search, Context.MODE_PRIVATE);
        myPic = (CircleImageView) findViewById(R.id.match_my_pic);
        friendPic = (CircleImageView) findViewById(R.id.match_friend_pic);
        matchedText = (TextView) findViewById(R.id.match_name);
        sendMessage = (Button) findViewById(R.id.send_message);
        keepSwipe = (Button) findViewById(R.id.keep_swipe);
        sendMessage.setOnClickListener(this);
        keepSwipe.setOnClickListener(this);
        b = getIntent().getExtras();

        keepSwipe.setVisibility(View.GONE);
        uid = b.getString("matched", "");
        matchedText.setText(String.format(getString(R.string.matchText), b.getString("name", "")));
        Picasso.with(getApplicationContext()).
                load(b.getString("picture", ""))
                .into(friendPic);
        Picasso.with(getApplicationContext()).
                load(sharedPreferences.getString("picture", ""))
                .into(myPic);

    }


    @Override
    public void onClick(View view) {
        if (view == keepSwipe) {
            onBackPressed();
        }
        if (view == sendMessage) {
            // TODO: 1/16/2017  send to message activity

        }
    }
}
