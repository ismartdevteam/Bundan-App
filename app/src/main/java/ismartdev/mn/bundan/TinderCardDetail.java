package ismartdev.mn.bundan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ismartdev.mn.bundan.models.User;
import ismartdev.mn.bundan.util.Constants;

public class TinderCardDetail extends BaseActivity implements View.OnClickListener{
    private SliderLayout sliderLayout;
    private Bundle b;
    private TextView nameAndAge;
    private TextView education;
    private TextView work;
    private static final String TAG = "TinderCardDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinder_card_detail);
        nameAndAge = (TextView) findViewById(R.id.user_nameAndAge);
        education = (TextView) findViewById(R.id.user_edu);
        work = (TextView) findViewById(R.id.user_work);
        ImageButton dislike = (ImageButton) findViewById(R.id.search_dislike);
        ImageButton like = (ImageButton) findViewById(R.id.search_like);
        dislike.setOnClickListener(this);
        like.setOnClickListener(this);
        b = getIntent().getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameAndAge.setText(b.getString("name"));
        sliderLayout = (SliderLayout) findViewById(R.id.detail_slider);

        ref.child(Constants.user + "/" + b.getString("uid", "")).child(Constants.user_info).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    work.setText(user.work + "");
                    Log.e(TAG,user.picture.size()+"-");
                    addImages(user.picture);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return false;
        }
    }
    private void interactUser(boolean status){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("interact",status);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
    private void addImages(List<String> images) {
        for (String image : images) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .image(image)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
//                            Intent imageIntent = new Intent(ProjectDetail.this, ImageDetailAc.class);
//                            imageIntent.putExtra("url", slider.getUrl());
//                            startActivity(imageIntent);
                        }
                    });


            sliderLayout.addSlider(textSliderView);
        }

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
