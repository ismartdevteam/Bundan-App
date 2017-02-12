package ismartdev.mn.bundan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import ismartdev.mn.bundan.fragments.MessageFragment;
import ismartdev.mn.bundan.fragments.SearchFragment;
import ismartdev.mn.bundan.fragments.UserFragment;
import ismartdev.mn.bundan.util.Constants;
import ismartdev.mn.bundan.util.SelectiveViewPager;


public class MainActivity extends BaseActivityNoApp implements View.OnClickListener,SearchFragment.OnFragmentInteractionListener, MessageFragment.OnFragmentInteractionListener, UserFragment.OnFragmentInteractionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static final int CARD_REQ = 1000;
    private static final String TAG = "MainActivity";
    private SelectiveViewPager mViewPager;
    private SharedPreferences sharedPreferences;
    private String url = "";
    public static boolean isSearchAgain = false;
    private ImageButton user_menu;
    private ImageButton search_menu;
    private ImageButton chat_menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(Constants.sp_search, MODE_PRIVATE);
        if (sharedPreferences.getString(Constants.fcm, "").equals(""))
            checkFcm(sharedPreferences);

        user_menu =(ImageButton)findViewById(R.id.user_menu);
        search_menu =(ImageButton)findViewById(R.id.search_menu);
        chat_menu =(ImageButton)findViewById(R.id.chat_menu);
        user_menu.setOnClickListener(this);
        search_menu.setOnClickListener(this);
        chat_menu.setOnClickListener(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (SelectiveViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);



        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeMenu(position);
                if (position == 1) {
                    if (isSearchAgain) {

                        Log.e("isSearchAgain", isSearchAgain + "");
                        isSearchAgain = false;
                        SearchFragment fragment = SearchFragment.getInstance();
                        fragment.getListService();

                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(1);
    }

    private void checkFcm(SharedPreferences sp) {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (!TextUtils.isEmpty(refreshedToken)) {
            Log.e(TAG, "!TextUtils.isEmpty(refreshedToken)");
            sp.edit().putString(Constants.fcm, refreshedToken).commit();
            ref.child(Constants.user).child(getUid()).child(Constants.user_info).child(Constants.fcm).setValue(refreshedToken);
        }

    }


    @Override
    public void onChangeViewPagerState(boolean isPager)

    {
        Log.e("setPaging", isPager + "---");
        mViewPager.setPaging(isPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onChangeUserFind(boolean changed) {

        isSearchAgain = changed;

    }

    private void changeMenu(int pos){
        switch (pos){
            case 0:
                user_menu.setSelected(true);
                chat_menu.setSelected(false);
                search_menu.setSelected(false);
                break;
            case 1:
                user_menu.setSelected(false);
                chat_menu.setSelected(false);
                search_menu.setSelected(true);
                break;
            case 2:
                user_menu.setSelected(false);
                chat_menu.setSelected(true);
                search_menu.setSelected(false);
                break;
        }
    }
    @Override
    public void onClick(View v) {
        if(v==user_menu)
        {
            mViewPager.setCurrentItem(0);
        }
        if(v==search_menu)
        {
            mViewPager.setCurrentItem(1);
        }
        if(v==chat_menu)
        {
            mViewPager.setCurrentItem(2);
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        private final FragmentManager mFragmentManager;
        private Map<Integer, String> mFragmentTags;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object object = super.instantiateItem(container, position);
            if (object instanceof Fragment) {
                Fragment fragment = (Fragment) object;
                String tag = fragment.getTag();
                mFragmentTags.put(position, tag);
            }
            return object;
        }

        public Fragment getFragment(int position) {
            Fragment fragment = null;
            String tag = mFragmentTags.get(position);
            if (tag != null) {
                fragment = mFragmentManager.findFragmentByTag(tag);
            }
            return fragment;
        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
            mFragmentTags = new HashMap<Integer, String>();
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return UserFragment.newInstance(getUid(), url);
                case 1:

                    return SearchFragment.newInstance(getUid(), url);
                case 2:
                    return MessageFragment.newInstance(getUid(), url);
                default:
                    return MessageFragment.newInstance(getUid(), url);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", requestCode + "-" + resultCode);

        if (resultCode == RESULT_OK) {
            if (data != null) {
                final boolean swipe = data.getBooleanExtra("interact", false);
                Log.e("onActivityResult", data.getBooleanExtra("interact", false) + "");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SearchFragment fragment = SearchFragment.getInstance();
                        fragment.DoSwipe(swipe);
                    }
                }, 500);
            }
        }
    }
}
