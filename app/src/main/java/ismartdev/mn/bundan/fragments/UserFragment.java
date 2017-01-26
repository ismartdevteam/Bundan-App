package ismartdev.mn.bundan.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.models.User;
import ismartdev.mn.bundan.util.CircleImageView;
import ismartdev.mn.bundan.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    private static final String UID = "uid";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String uid;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DatabaseReference ref;
    private CircleImageView image;
    private TextView nameTv;
    private TextView workTv;
    private TextView eduTv;
    private TextView findGenderTv;
    private TextView ageRange;
    private RangeBar agePicker;
    private SharedPreferences sp;
    private CardView logout;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uid    Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String uid, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(UID, uid);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(UID);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        image = (CircleImageView) v.findViewById(R.id.user_image);
        nameTv = (TextView) v.findViewById(R.id.user_name);
        workTv = (TextView) v.findViewById(R.id.user_work);
        eduTv = (TextView) v.findViewById(R.id.user_edu);
        findGenderTv = (TextView) v.findViewById(R.id.user_find_gender);
        ageRange = (TextView) v.findViewById(R.id.user_age_range_text);
        agePicker = (RangeBar) v.findViewById(R.id.age_range_bar);
        logout = (CardView) v.findViewById(R.id.logout_btn);
        logout.setOnClickListener(this);
        sp = getActivity().getSharedPreferences(Constants.sp_search, Context.MODE_PRIVATE);

        ref = FirebaseDatabase.getInstance().getReference();
        ref.child(Constants.user + "/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null)
                    makeViews(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }

    private void makeViews(User user) {
        if (user.picture != null)
            Picasso.with(getActivity()).load(user.picture.get(0).url).into(image);
        nameTv.setText(user.name);
        workTv.setText(user.work);
        eduTv.setText(user.education);
        findGenderTv.setText(sp.getString("gender", "female"));
        final String ages = sp.getString("age_range", "18-22");
        ageRange.setText(ages);

        agePicker.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                String ages=leftPinValue+"-"+rightPinValue;
                ageRange.setText(ages);
                sp.edit().putString("age_range",ages).commit();

            }
        });

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public void onClick(View v) {
            if(v==logout){
                // TODO: 1/24/2017 Call dialog

            }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
