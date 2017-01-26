package ismartdev.mn.bundan.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;

import java.util.List;

import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.models.SearchList;
import ismartdev.mn.bundan.models.SearchParams;
import ismartdev.mn.bundan.models.UserGender;
import ismartdev.mn.bundan.util.ApiClient;
import ismartdev.mn.bundan.util.ApiInterface;
import ismartdev.mn.bundan.util.Constants;
import ismartdev.mn.bundan.views.TinderCard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, TinderCard.CardCallback {
    // TODO: Rename parameter arguments, choose names that match
    private static final String UID = "uid";
    private static final String URL = "url";
    private static final String TAG = "SearchFragment";
    private static SearchFragment fragment;

    // TODO: Rename and change types of parameters
    private String uid;
    private String url;
    private OnFragmentInteractionListener mListener;

    private SharedPreferences sp;
    private String gender;
    private String age_range;
    private ApiInterface apiService;
    private SwipePlaceHolderView mSwipView;

    public SearchFragment() {
    }

    public static SearchFragment newInstance(String param1, String param2) {
        fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(UID, param1);
        args.putString(URL, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchFragment getInstance() {
        if (fragment != null)
            return fragment;
        else
            return fragment = new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(UID);
            url = getArguments().getString(URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton dislike = (ImageButton) v.findViewById(R.id.search_dislike);
        ImageButton like = (ImageButton) v.findViewById(R.id.search_like);
        dislike.setOnClickListener(this);
        like.setOnClickListener(this);
        mSwipView = (SwipePlaceHolderView) v.findViewById(R.id.swipeView);

        mSwipView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                Log.e(TAG, count + "--");


            }
        });
        mSwipView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeType(SwipePlaceHolderView.SWIPE_TYPE_HORIZONTAL)

                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setSwipeRotationAngle(45)

                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
        sp = getActivity().getSharedPreferences(Constants.sp_search, Context.MODE_PRIVATE);
        gender = sp.getString("gender", "female");
        age_range = sp.getString("age_range", "18-22");
        String limit = sp.getString("limit", "20");

        apiService =
                ApiClient.getClient(Constants.url).create(ApiInterface.class);
        SearchParams searchParams = new SearchParams(age_range, gender, limit, uid);

        Call<SearchList> call = apiService.searchPeople(searchParams);
        call.enqueue(new Callback<SearchList>() {
            @Override
            public void onResponse(Call<SearchList> call, Response<SearchList> response) {
                if (response.body().getCode() == 200) {
                    List<UserGender> searchList = response.body().getData();
                    makeTinderList(searchList);
                }
            }

            @Override
            public void onFailure(Call<SearchList> call, Throwable t) {
                Log.e("retrofit onFailure ", t.toString());
            }
        });


        return v;
    }

    private void makeTinderList(List<UserGender> userGenders) {
        for (UserGender item : userGenders) {
            mSwipView.addView(new TinderCard(uid, getActivity(), item, this, gender, age_range));
        }


    }

    public void changeStateViewpager(boolean isPager) {
        if (mListener != null) {
            mListener.onFragmentInteraction(isPager);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchFragment.OnFragmentInteractionListener) {
            mListener = (SearchFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_dislike:
                mSwipView.doSwipe(false);
                break;

            case R.id.search_like:
                mSwipView.doSwipe(true);
//                mProductStackView.likeOrDislike(true);
                break;

        }
    }

    @Override
    public void onSwiping() {
        changeStateViewpager(false);
    }

    @Override
    public void onSwipingEnd() {
        changeStateViewpager(true);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(boolean isPager);
    }

}
