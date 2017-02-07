package ismartdev.mn.bundan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ismartdev.mn.bundan.fragments.ImageItemFragment;
import ismartdev.mn.bundan.models.User;
import ismartdev.mn.bundan.util.Constants;
import ismartdev.mn.bundan.views.PullToZoomScrollViewEx;
import me.relex.circleindicator.CircleIndicator;

public class TinderCardDetail extends FragmentActivity implements View.OnClickListener {
    private Bundle b;
    private TextView nameAndAge;
    private TextView education;
    private TextView work;
    private static final String TAG = "TinderCardDetail";
    private PullToZoomScrollViewEx scrollView;
    private ViewPager viewPager;
    private CircleIndicator indicator;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinder_card_detail);
        ref = FirebaseDatabase.getInstance().getReference();
        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.content_tinder_card_detail_scroll);

        View zoomView = LayoutInflater.from(this).inflate(R.layout.tinder_card_detail_image, null, false);
        View contentView = LayoutInflater.from(this).inflate(R.layout.tinder_card_detail_info, null, false);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, mScreenWidth);
        scrollView.setHeaderLayoutParams(localObject);

        viewPager = (ViewPager) zoomView.findViewById(R.id.tinder_det_view_pager);

        indicator = (CircleIndicator) zoomView.findViewById(R.id.indicator);


        nameAndAge = (TextView) contentView.findViewById(R.id.user_nameAndAge);
        education = (TextView) contentView.findViewById(R.id.user_edu);
        work = (TextView) contentView.findViewById(R.id.user_work);


        ImageButton dislike = (ImageButton) findViewById(R.id.search_dislike);
        ImageButton like = (ImageButton) findViewById(R.id.search_like);
        dislike.setOnClickListener(this);
        like.setOnClickListener(this);
        b = getIntent().getExtras();

        nameAndAge.setText(b.getString("name"));

        ref.child(Constants.user + "/" + b.getString("uid", "")).child(Constants.user_info).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    work.setText(user.work + " ");
                    education.setText(user.education + " ");
                    Log.e(TAG, user.picture.size() + "-");

                    viewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(), user.picture));
                    indicator.setViewPager(viewPager);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        List<String> picList;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<String> picList) {
            super(fm);
            this.picList = picList;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageItemFragment.newInstance(picList.get(position));
        }

        @Override
        public int getCount() {
            return picList.size();
        }


    }


    private void interactUser(boolean status) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("interact", status);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_dislike:
                interactUser(false);
                break;

            case R.id.search_like:
                interactUser(true);
                break;

        }
    }
}
