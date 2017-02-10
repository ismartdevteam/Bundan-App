package ismartdev.mn.bundan.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import ismartdev.mn.bundan.MessageActivity;
import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.models.MatchedModel;
import ismartdev.mn.bundan.models.User;
import ismartdev.mn.bundan.models.UserMatched;
import ismartdev.mn.bundan.models.UserMatches;
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
    private static final String TAG = "";

    // TODO: Rename and change types of parameters
    private String uid;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DatabaseReference ref;
    private LinearLayout matchesLin;
    private LinearLayout messagesLin;

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
        matchesLin = (LinearLayout) v.findViewById(R.id.new_match_List);
        messagesLin = (LinearLayout) v.findViewById(R.id.match_messageList);

        return v;
    }

    private void getMatches() {
        matchesLin.removeAllViews();

        ref.child(Constants.user + "/" + uid + "/" + Constants.matches).addChildEventListener(childEventListener);
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

            final UserMatches matches = dataSnapshot.getValue(UserMatches.class);
            Log.d(TAG, "UserMatches:" + matches.match_id);
            FirebaseDatabase.getInstance().getReference().child(Constants.user_matches + "/" + matches.match_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snap) {
                    Log.d(TAG, "onDataChange:" + snap.getValue().toString());
                    MatchedModel matchedModel = snap.getValue(MatchedModel.class);
                    if (matchedModel != null) {
                        if (matchedModel.last_message == null) {
                            createNewMatchView(dataSnapshot.getKey(), matches.match_id);
                        } else {
                            createNewMessageView(dataSnapshot.getKey(), matchedModel,matches.match_id);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "postComments:onCancelled", databaseError.toException());
        }
    };

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint())
            return;
        getMatches();
    }

    private void createNewMatchView(final String matchedUid, final String match_id) {


        final View view = getActivity().getLayoutInflater().inflate(R.layout.match_item, null);
        final CircleImageView imageView = (CircleImageView) view.findViewById(R.id.match_image);
        final TextView name = (TextView) view.findViewById(R.id.match_name);
        FirebaseDatabase.getInstance().getReference().child(Constants.user + "/" + matchedUid + "/" + Constants.user_info).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User info = dataSnapshot.getValue(User.class);
                if (info != null) {
                    Picasso.with(getActivity()).load(info.picture.get(0))
                            .placeholder(R.drawable.placeholder)
                            .into(imageView);
                    name.setText(info.name);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), MessageActivity.class);
                            Bundle b = new Bundle();
                            b.putString("uid", matchedUid);
                            b.putString("matchID", match_id);
                            b.putString("match_name", info.name);
                            b.putString("match_user_img", info.picture.get(0));
                            intent.putExtras(b);
                            startActivity(intent);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        matchesLin.addView(view);


    }


    private void createNewMessageView(final String matchedUid, final MatchedModel model,final String match_id) {


        final View view = getActivity().getLayoutInflater().inflate(R.layout.match_message_item, null);
        final CircleImageView imageView = (CircleImageView) view.findViewById(R.id.match_image);
        final TextView name = (TextView) view.findViewById(R.id.match_name);
        final TextView lastmessage = (TextView) view.findViewById(R.id.match_last_sms);

        FirebaseDatabase.getInstance().getReference().child(Constants.user + "/" + matchedUid + "/" + Constants.user_info).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User info = dataSnapshot.getValue(User.class);
                if (info != null) {
                    Picasso.with(getActivity()).load(info.picture.get(0))
                            .placeholder(R.drawable.placeholder)
                            .into(imageView);
                    name.setText(info.name);
                    lastmessage.setText(model.last_message.message);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), MessageActivity.class);
                            Bundle b = new Bundle();
                            b.putString("uid", matchedUid);
                            b.putString("matchID", match_id);
                            b.putString("match_name", info.name);
                            b.putString("match_user_img", info.picture.get(0));
                            intent.putExtras(b);
                            startActivity(intent);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        messagesLin.addView(view);


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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ref.removeEventListener(childEventListener);
    }
}
