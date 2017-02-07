package ismartdev.mn.bundan.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ismartdev.mn.bundan.FullscreenActivity;
import ismartdev.mn.bundan.MainActivity;
import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.UserDeteailActivity;
import ismartdev.mn.bundan.models.User;
import ismartdev.mn.bundan.models.UserAll;
import ismartdev.mn.bundan.models.UserSettings;
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
    private ImageView editProfile;
    private TextView nameTv;
    private TextView workTv;
    private TextView eduTv;
    private TextView findGenderTv;
    private TextView ageRange;
    private RangeBar agePicker;
    private SharedPreferences sp;
    private CardView logout;
    private UserAll user;
    private String genders[] = {"female", "male"};

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
        editProfile = (ImageView) v.findViewById(R.id.user_edit_btn);

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
        ref.child(Constants.user + "/" + uid + "/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(UserAll.class);
                if (user != null) {
                    makeViews(user);
                    editProfile.setEnabled(true);
                    editProfile.setOnClickListener(UserFragment.this);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        findGenderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog();
            }
        });
        String ages[] = sp.getString("age_range", "18-22").split("-");
        agePicker.setDrawTicks(false);
        agePicker.setRangePinsByValue(Integer.parseInt(ages[0]), Integer.parseInt(ages[1]));
        agePicker.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                String ages = leftPinValue + "-" + rightPinValue;


                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(Constants.user + "/" + uid + "/" + Constants.user_settings + "/age_range", ages);
                ref.updateChildren(childUpdates);
                sp.edit().putString("age_range", ages).commit();
                onChangeAge(true);
            }
        });

        return v;
    }

    private void callDialog() {
        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.list_dialog);

        ListView listView = (ListView) dialog.findViewById(R.id.dialog_list);

        final CheckedTextView noneTv = (CheckedTextView) dialog.findViewById(R.id.list_none);
        noneTv.setVisibility(View.GONE);
        int selectedPos = 0;
        if (user.user_settings.gender.equals("male")) {
            selectedPos = 1;
        }

        final CustomAdapter customAdapter = new CustomAdapter(getActivity(), Arrays.asList(genders), selectedPos);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(Constants.user + "/" + uid + "/" + Constants.user_settings + "/gender", genders[position]);
                ref.updateChildren(childUpdates);
                sp.edit().putString("gender", genders[position]).commit();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void makeViews(UserAll user) {
        User userInfo = user.user_info;
        UserSettings userSettings = user.user_settings;
        if (userInfo.picture.size() > 0)
            Picasso.with(getActivity()).load(userInfo.picture.get(0)).into(image);
        nameTv.setText(userInfo.name);
        workTv.setText(userInfo.work);
        eduTv.setText(userInfo.education);
        findGenderTv.setText(userSettings.gender);
        final String ages = userSettings.age_range;
        ageRange.setText(ages);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onChangeAge(boolean changed) {
        if (mListener != null) {
            mListener.onChangeUserFind(changed);
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
        if (v == logout) {
            // TODO: 1/24/2017 Call dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.logout_submission);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    dialogInterface.dismiss();
                    getActivity().getSharedPreferences(Constants.sp_search, Context.MODE_PRIVATE).edit().clear().commit();
                    getActivity().finish();

                    startActivity(new Intent(getActivity(), FullscreenActivity.class));
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();


        }
        if (v == editProfile) {
            Intent intent = new Intent(getActivity(), UserDeteailActivity.class);

            intent.putExtra("User", user.user_info);
            startActivity(intent);
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
        void onChangeUserFind(boolean changed);
    }

    class CustomAdapter extends BaseAdapter {

        List<String> names;
        Context context;
        LayoutInflater inflter;
        int selectedPosition = -1;

        public CustomAdapter(Context context, List<String> names, int selectedPosition) {
            this.context = context;
            this.names = names;
            this.selectedPosition = selectedPosition;
            inflter = (LayoutInflater.from(context));

        }

        @Override
        public int getCount() {
            return names.size();
        }

        @Override
        public Object getItem(int position) {
            return names.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            view = inflter.inflate(R.layout.list_dialog_item, null);
            final CheckedTextView simpleCheckedTextView = (CheckedTextView) view.findViewById(R.id.list_dialog_item);
            simpleCheckedTextView.setText(names.get(position));

            Log.e("selectedPosition", selectedPosition + "---");
            if (selectedPosition == position) {
                simpleCheckedTextView.setCheckMarkDrawable(R.drawable.ic_check);
                simpleCheckedTextView.setChecked(true);
            } else {
                simpleCheckedTextView.setCheckMarkDrawable(null);
                simpleCheckedTextView.setChecked(false);
            }


            return view;
        }
    }

}
