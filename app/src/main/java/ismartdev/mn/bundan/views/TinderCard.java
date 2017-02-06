package ismartdev.mn.bundan.views;

/**
 * Created by Ulzii on 1/19/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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

import ismartdev.mn.bundan.MatchActivity;
import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.TinderCardDetail;
import ismartdev.mn.bundan.models.AgeRanges;
import ismartdev.mn.bundan.models.InteractModel;
import ismartdev.mn.bundan.models.MatchPost;
import ismartdev.mn.bundan.models.UserGender;
import ismartdev.mn.bundan.models.UserMatched;
import ismartdev.mn.bundan.util.ApiClient;
import ismartdev.mn.bundan.util.ApiInterface;
import ismartdev.mn.bundan.util.Constants;
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

        callback.onClickCard(userGender);
//        ((Activity) context).startActivityForResult(intent);
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
        locationNameTxt.setText(userGender.getWork()+"");
    }

    @SwipeOut
    private void onSwipedOut() {

        interactUser(userGender, false,uid,context);
        callback.onSwipingEnd();
    }

    @SwipeCancelState
    private void onSwipeCancelState() {
        callback.onSwipingEnd();
    }

    @SwipeIn
    private void onSwipeIn() {

        interactUser(userGender, true,uid,context);
        callback.onSwipingEnd();
    }

    @SwipeInState
    private void onSwipeInState() {
        callback.onSwiping();
    }

    private void interactUser(final UserGender interUser, boolean choice, final String uid, final Context context) {
        Log.e("interUid-", interUser.getUid());
        final InteractModel inter = new InteractModel(interUser.getUid(), ServerValue.TIMESTAMP);
        AgeRanges ageRanges = new AgeRanges(interUser.getUid());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(Constants.user + Constants.getInteractName(choice) + uid + "/" + interUser.getUid(), inter.toMap());
        childUpdates.put(Constants.user + "/" + uid + "/" + Constants.user_settings + "/" + Constants.search_ranges + gender + "/" + age_range, ageRanges.toMap());

        reference.updateChildren(childUpdates);
        Log.e("match picker", Constants.user + Constants.getInteractName(true) + interUser.getUid() + "/" + uid);
        if (choice)
            reference.child(Constants.user + Constants.getInteractName(true) + interUser.getUid() + "/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        DatabaseReference newRef=FirebaseDatabase.getInstance().getReference();
                        String newMatchkey = newRef.child(Constants.user_matches).push().getKey();
                        UserMatched userMatchedMe = new UserMatched(false, ServerValue.TIMESTAMP, uid);
                        UserMatched userMatchedInter = new UserMatched(false, ServerValue.TIMESTAMP, interUser.getUid());
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(Constants.user+"/"+uid + "/" + Constants.matches + newMatchkey, userMatchedInter.toMap());
                        childUpdates.put(Constants.user+"/"+interUser.getUid() + "/" + Constants.matches + newMatchkey, userMatchedMe.toMap());
                        newRef.updateChildren(childUpdates);
                        Log.e("match picker", Constants.user + Constants.getInteractName(true) + interUser.getUid() + "/" + uid);
                        sendPushNotificationMatch(context,uid,interUser.getUid(), interUser.getName(), interUser.getPicture());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    private void sendPushNotificationMatch(Context context, String uid, final String interUid, String name, String picture) {
        apiService =
                ApiClient.getClient(Constants.url).create(ApiInterface.class);
        Call<MatchPost> call = apiService.matchPush(new MatchPost(uid, interUid));
        call.enqueue(new Callback<MatchPost>() {
            @Override
            public void onResponse(Call<MatchPost> call, Response<MatchPost> response) {
                if (response.body().getCode() == 200) {
                    Log.e("Success matchPush ", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<MatchPost> call, Throwable t) {
                Log.e("onFailure matchPush ", t.toString());
            }
        });
        Intent intent = new Intent(context, MatchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("matched", interUid);
        bundle.putString("name", name);
        bundle.putString("picture", picture);
        intent.putExtras(bundle);
        context.startActivity(intent);

    }

    @SwipeOutState
    private void onSwipeOutState() {
        callback.onSwiping();
    }

    public interface CardCallback {
        void onSwiping();

        void onSwipingEnd();

        void onClickCard(UserGender gender);


    }
}