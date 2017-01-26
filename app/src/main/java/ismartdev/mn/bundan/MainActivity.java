package ismartdev.mn.bundan;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ismartdev.mn.bundan.fragments.MessageFragment;
import ismartdev.mn.bundan.fragments.SearchFragment;
import ismartdev.mn.bundan.util.Constants;
import ismartdev.mn.bundan.util.HMAC;
import ismartdev.mn.bundan.util.SelectiveViewPager;

public class MainActivity extends BaseActivity implements SearchFragment.OnFragmentInteractionListener, MessageFragment.OnFragmentInteractionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final String TAG = "MainActivity";
    private SelectiveViewPager mViewPager;
    private SharedPreferences sharedPreferences;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(Constants.sp_search, MODE_PRIVATE);
        if (sharedPreferences.getString(Constants.fcm, "").equals(""))
            checkFcm(sharedPreferences);

//        Bundle params = new Bundle();
//        Log.e("appsecret_proof",HMAC.hmacDigestSha256());
//        params.putString("appsecret_proof", HMAC.hmacDigestSha256());
//        params.putString("fields", "context.fields(mutual_friends)");
///* make the API call */
//        new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/246518952451357",
//                params,
//                HttpMethod.GET,
//
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//
//                        Log.e("facebook", "onCompleted: " +response.getRawResponse()+" ");
//                        Log.e("facebook", "onCompleted: " +response.getError().getErrorMessage()+" ");
//
//
//                        try {
//                            ArrayList<String> names = new ArrayList<String>();
//                            ArrayList<String> ids = new ArrayList<String>();
//
//                            JSONObject contextObj = response.getJSONObject().getJSONObject("context");
//                            JSONObject mutualFriendsObj = contextObj.getJSONObject("mutual_friends");
//                            JSONArray friendData = mutualFriendsObj.getJSONArray("data");
//
//                            for(int i = 0; i < friendData.length(); i++){
//                                JSONObject obj = friendData.getJSONObject(i);
//                                names.add(obj.getString("name"));
//                                ids.add(obj.getString("id"));
//                            }
//                                Log.e("hha",names.size()+"");
//                        }
//                        catch(JSONException e){
//                            e.printStackTrace();
//                        } catch (NullPointerException n) {
//                            n.printStackTrace();
//                        }
//
//
//            /* handle the result */
////                        try {
////                            new GraphRequest(
////                                    AccessToken.getCurrentAccessToken(),
////                                    "/"+response.getJSONObject().getJSONObject("context").getString("id"),
////                                    null,
////                                    HttpMethod.GET,
////                                    new GraphRequest.Callback() {
////                                        public void onCompleted(GraphResponse ds) {
////                /* handle the result */   Log.e("facebook", "onCompleted: " +ds.getRawResponse());
////                                        }
////                                    }
////                            ).executeAsync();
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
//                    }
//                }
//        ).executeAsync();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (SelectiveViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



    }

    private void checkFcm(SharedPreferences sp) {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, refreshedToken + "refreshedToken");
        if (!TextUtils.isEmpty(refreshedToken)) {
            Log.e(TAG, "!TextUtils.isEmpty(refreshedToken)");
            sp.edit().putString(Constants.fcm, refreshedToken).commit();
            ref.child(Constants.user).child(getUid()).child(Constants.fcm).setValue(refreshedToken);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the SearchFragment/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(boolean isPager) {
        mViewPager.setPaging(isPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 1:
                    return SearchFragment.newInstance(getUid(), url);
                case 2:
                    return MessageFragment.newInstance(getUid(), url);
                default:
                    return PlaceholderFragment.newInstance(position + 1);

            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

}
