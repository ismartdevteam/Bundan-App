package ismartdev.mn.bundan.util;

/**
 * Created by Ulzii on 1/19/2017.
 */

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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

import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.models.UserGender;


@NonReusable
@Layout(R.layout.tinder_card_view)
public class TinderCard {

    private UserGender userGender;

    private CardCallback callback;
    private Context context;

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    @Click(R.id.profileImageView)
    private void onClick() {
        Log.d("DEBUG", "profileImageView");
    }

    public TinderCard(Context context, UserGender gender,CardCallback callback) {
        this.context = context;
        this.userGender = gender;
        this.callback = callback;
    }


    @Resolve
    private void onResolve() {
        Picasso.with(context).load(userGender.getPicture()).into(profileImageView);

        nameAgeTxt.setText(userGender.getName() + "," + userGender.getAge());
    }

    @SwipeOut
    private void onSwipedOut() {
        callback.onSwipingEnd();
    }

    @SwipeCancelState
    private void onSwipeCancelState() {
        callback.onSwipingEnd();
    }

    @SwipeIn
    private void onSwipeIn() {
        callback.onSwipingEnd();
    }

    @SwipeInState
    private void onSwipeInState() {
        callback.onSwiping();
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