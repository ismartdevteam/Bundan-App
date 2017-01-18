package ismartdev.mn.bundan.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.adapters.UserAdapter;
import ismartdev.mn.bundan.models.AgeRanges;
import ismartdev.mn.bundan.models.InteractModel;
import ismartdev.mn.bundan.models.MatchPost;
import ismartdev.mn.bundan.models.SearchList;
import ismartdev.mn.bundan.models.SearchParams;
import ismartdev.mn.bundan.models.User;
import ismartdev.mn.bundan.models.UserGender;
import ismartdev.mn.bundan.models.UserMatched;
import ismartdev.mn.bundan.util.ApiClient;
import ismartdev.mn.bundan.util.ApiInterface;
import ismartdev.mn.bundan.util.Constants;
import ismartdev.mn.bundan.views.ProductStackView;
import ismartdev.mn.bundan.views.SingleProductView;
import ismartdev.mn.bundan.views.UserHolder;
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
public class SearchFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    private static final String UID = "uid";
    private static final String URL = "url";
    private static SearchFragment fragment;
    private DatabaseReference reference;

    // TODO: Rename and change types of parameters
    private String uid;
    private String url;
    private OnFragmentInteractionListener mListener;

    private SharedPreferences sp;
    private ProductStackView mProductStackView;
    private String gender;
    private String age_range;
    private ApiInterface apiService;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        Button dislike = (Button) v.findViewById(R.id.search_dislike);
        Button like = (Button) v.findViewById(R.id.search_like);
        dislike.setOnClickListener(this);
        like.setOnClickListener(this);
        mProductStackView = (ProductStackView) v.findViewById(R.id.tinder_mProductStack);
        reference = FirebaseDatabase.getInstance().getReference();
        url = "https://bundan-e28d3.appspot-preview.com/";

        sp = getActivity().getSharedPreferences(Constants.sp_search, Context.MODE_PRIVATE);
        gender = sp.getString("gender", "female");
        // TODO: 1/14/2017 delete whe production
        gender = "female";
        age_range = sp.getString("age_range", "18-22");
        String limit = sp.getString("limit", "20");

        apiService =
                ApiClient.getClient(url).create(ApiInterface.class);
        SearchParams searchParams = new SearchParams(age_range, gender, limit, uid);

        Call<SearchList> call = apiService.searchPeople(searchParams);
        call.enqueue(new Callback<SearchList>() {
            @Override
            public void onResponse(Call<SearchList> call, Response<SearchList> response) {
                if (response.body().getCode() == 200) {
                    List<UserGender> searchList = response.body().getData();
                    UserAdapter userAdapter = new UserAdapter(getActivity(), searchList);
                    makeAdapter(searchList, userAdapter);
                }
            }

            @Override
            public void onFailure(Call<SearchList> call, Throwable t) {
                Log.e("retrofit onFailure ", t.toString());
            }
        });


        return v;
    }

    private void makeAdapter(final List<UserGender> searchList, final UserAdapter adapter) {
        mProductStackView.setAdapter(adapter);
        mProductStackView.setmProductStackListener(new ProductStackView.ProductStackListener() {
            @Override
            public void onUpdateProgress(boolean positif, float percent, View view) {
                changeStateViewpager(false);
                UserHolder userHolder = (UserHolder) view.getTag();
                userHolder.singleProductView.onUpdateProgress(positif, percent, view);

            }

            @Override
            public void onCancelled(View beingDragged) {
                changeStateViewpager(false);
                UserHolder userHolder = (UserHolder) beingDragged.getTag();
                userHolder.singleProductView.onCancelled(beingDragged);

            }

            @Override
            public void onChoiceMade(boolean choice, View beingDragged) {
                UserHolder userHolder = (UserHolder) beingDragged.getTag();
                userHolder.singleProductView.onChoiceMade(choice, beingDragged);
                interactUser(userHolder.userGender.getUid(), choice);
                searchList.remove(0);
                changeStateViewpager(true);
            }
        });


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
                        Toast.makeText(getActivity(), "matched", Toast.LENGTH_LONG).show();
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


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_dislike:
                mProductStackView.likeOrDislike(false);
                break;

            case R.id.search_like:
                mProductStackView.likeOrDislike(true);
                break;

        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(boolean isPager);
    }

}
