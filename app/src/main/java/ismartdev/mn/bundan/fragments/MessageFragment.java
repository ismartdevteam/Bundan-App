package ismartdev.mn.bundan.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.models.UserMatched;
import ismartdev.mn.bundan.util.CircleImageView;
import ismartdev.mn.bundan.util.Constants;
import ismartdev.mn.bundan.util.FirebaseRecyclerAdapter;
import ismartdev.mn.bundan.views.MatchViewHolder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String UID = "uid";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String uid;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DatabaseReference ref;
    private FirebaseRecyclerAdapter<UserMatched, MatchViewHolder> matchAdapter;

    public MessageFragment() {
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
    public static MessageFragment newInstance(String uid, String param2) {
        MessageFragment fragment = new MessageFragment();
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
        View v = inflater.inflate(R.layout.fragment_matches, container, false);
        ref = FirebaseDatabase.getInstance().getReference();
        // Inflate the layout for this fragment
        createNewMatchView(v);
        return v;
    }

    private void createNewMatchView(View v) {



        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.new_match_List);
        mRecyclerView.setLayoutManager(layoutManager);

        matchAdapter = new FirebaseRecyclerAdapter<UserMatched, MatchViewHolder>(UserMatched.class, R.layout.match_item, MatchViewHolder.class,
                ref.child(Constants.user_matches +uid).orderByChild("ischat").startAt("no").getRef()) {
            @Override
            protected void populateViewHolder(final MatchViewHolder viewHolder, final UserMatched userMatched, int position, String key) {
                Log.e("usermatche",userMatched.getDate()+"--");
                makeMatchItem(viewHolder.userImage, key);

            }
        };
        mRecyclerView.setAdapter(matchAdapter);


    }

    private void makeMatchItem(final CircleImageView circleImageView, String key) {

        ref.child(Constants.user + "/" + key + "/picture").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    Picasso.with(getActivity()).load(dataSnapshot.getValue().toString()).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
    @Override
    public void onDestroy() {
        super.onDestroy();
        matchAdapter.cleanup();
    }
}
