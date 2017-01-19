package ismartdev.mn.bundan.util;

/**
 * Created by Ulzii on 1/19/2017.
 */

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.models.AgeRanges;
import ismartdev.mn.bundan.models.InteractModel;
import ismartdev.mn.bundan.models.MatchPost;
import ismartdev.mn.bundan.models.UserGender;
import ismartdev.mn.bundan.models.UserMatched;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@NonReusable
@Layout(R.layout.tinder_card_view)
public class TinderCard {

    private UserGender userGender;

    private CardCallback callback;
    private Context context;
    private String uid;
    private String gender;
    private String age_range;

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;
    private DatabaseReference reference;
    private ApiInterface apiService;

    @Click(R.id.profileImageView)
    private void onClick() {
        Log.d("DEBUG", "profileImageView");
    }

    public TinderCard(String uid, Context context, UserGender usergender, CardCallback callback, String gender, String age_range) {
        this.context = context;
        this.uid = uid;
        this.gender = gender;
        this.age_range = age_range;
        this.userGender = usergender;
        reference = FirebaseDatabase.getInstance().getReference();
        this.callback = callback;

    }


    @Resolve
    private void onResolve() {
        Picasso.with(context).load(userGender.getPicture()).into(profileImageView);

        nameAgeTxt.setText(userGender.getName() + "," + userGender.getAge());
    }

    @SwipeOut
    private void onSwipedOut() {

        interactUser(userGender.getUid(),false);
        callback.onSwipingEnd();
    }

    @SwipeCancelState
    private void onSwipeCancelState() {
        callback.onSwipingEnd();
    }

    @SwipeIn
    private void onSwipeIn() {

        interactUser(userGender.getUid(),true);
        callback.onSwipingEnd();
    }

    @SwipeInState
    private void onSwipeInState() {
        callback.onSwiping();
    }

    public void interactUser(final String interUid, boolean choice) {
        Log.e("interUid-", interUid);
        InteractModel inter = new InteractModel(interUid, ServerValue.TIMESTAMP);
        AgeRanges ageRanges = new AgeRanges(interUid);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(Constants.user + Constants.getInteractName(choice) + uid + "/" + interUid, inter.toMap());
        childUpdates.put(Constants.user + "/" + uid + Constants.search_ranges + gender + "/" + age_range, ageRanges.toMap());

        reference.updateChildren(childUpdates);
        Log.e("match picker", Constants.user + Constants.getInteractName(true) + interUid + "/" + uid);
        if (choice)
            reference.child(Constants.user + Constants.getInteractName(true) + interUid + "/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        UserMatched userMatched = new UserMatched(uid, ServerValue.TIMESTAMP);
                        UserMatched interMatched = new UserMatched(interUid, ServerValue.TIMESTAMP);
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(Constants.user_matches + uid, interMatched.toMap());
                        childUpdates.put(Constants.user_matches + interUid, userMatched.toMap());
                        reference.updateChildren(childUpdates);
                        sendPushNotificationMatch(uid, interUid);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    private void sendPushNotificationMatch(String uid, String interUid) {
        apiService =
                ApiClient.getClient(Constants.url).create(ApiInterface.class);
        Call<MatchPost> call = apiService.matchPush(new MatchPost(uid, interUid));
        call.enqueue(new Callback<MatchPost>() {
            @Override
            public void onResponse(Call<MatchPost> call, Response<MatchPost> response) {
                if (response.body().getCode() == 200) {
                    Log.e("retrofit  matchPush ", "yes");
                }
            }

            @Override
            public void onFailure(Call<MatchPost> call, Throwable t) {
                Log.e("onFailure matchPush ", t.toString());
            }
        });

    }

    @SwipeOutState
    private void onSwipeOutState() {
        callback.onSwiping();
    }

    public interface CardCallback {
        void onSwiping();

        void onSwipingEnd();


    }
}