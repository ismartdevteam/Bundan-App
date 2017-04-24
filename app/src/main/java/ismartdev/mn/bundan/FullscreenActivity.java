package ismartdev.mn.bundan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ismartdev.mn.bundan.fragments.ImageItemFragment;
import ismartdev.mn.bundan.fragments.ImageTextFragment;
import ismartdev.mn.bundan.models.Image;
import ismartdev.mn.bundan.models.User;
import ismartdev.mn.bundan.models.UserAll;
import ismartdev.mn.bundan.models.UserGender;
import ismartdev.mn.bundan.models.UserSettings;
import ismartdev.mn.bundan.util.Constants;
import ismartdev.mn.bundan.util.Utils;
import me.relex.circleindicator.CircleIndicator;


public class FullscreenActivity extends FragmentActivity {
    private static final String TAG = "FullscreenActivity";
    private FirebaseAuth mAuth;
    private static final List<String> PERMISSIONS = Arrays.asList("public_profile", "email", "user_birthday", "user_work_history", "user_education_history");
    private CallbackManager mCallbackManager;
    private Task uploadTask;
    private SharedPreferences sharedPreferences;
    private String[] titles = {"Заяаны хань зам дээр тосоод зогсож байх нь хаашаа юм..", "Зөвхөн таалагдсан хүнтэйгээ л танилцаарай..", "Зөвхөн таалагдсан хүнтэйгээ л танилцаарай.."};
    private int[] imageRes = {R.drawable.img1, R.drawable.img2, R.drawable.img3};
    private ViewPager viewPager;
    private CircleIndicator indicator;
    public DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_fullscreen);

        initWalk();


        sharedPreferences = getSharedPreferences(Constants.sp_search, Context.MODE_PRIVATE);
        fbHash();
        showProgressDialog();
        mAuth = FirebaseAuth.getInstance();
        Log.e("isLoggedIn",isLoggedIn()+"");
        if (mAuth.getCurrentUser() != null && isLoggedIn()) {
            Log.d(TAG, "is logged");
            finish();
            startMainAc();

        } else {

            mCallbackManager = CallbackManager.Factory.create();

        }
        hideProgressDialog();
    }
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    private void initWalk() {
        viewPager = (ViewPager) findViewById(R.id.walk_pager);

        indicator = (CircleIndicator) findViewById(R.id.indicator);
        viewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        indicator.setViewPager(viewPager);
    }

    private void fbHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "ismartdev.mn.bundan",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void loginFb(View v) {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(FullscreenActivity.this, PERMISSIONS);
        loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }

            }
        });

    }

    private void startMainAc() {
        hideProgressDialog();
        finish();
        startActivity(new Intent(FullscreenActivity.this, MainActivity.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(FullscreenActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            saveUserToFirebase(token);

                        }


                    }
                });
    }

    private void saveUserToFirebase(AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Log.e("data", response.getRawResponse() + "");

                        addToFirebase(object, FirebaseAuth.getInstance().getCurrentUser().getUid());


                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,gender,education,birthday,picture.type(large),email,work");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void uploadImage(final String uid, String fb_id, final String gender) throws IOException {

        ImageRequest.Builder requestBuilder = new ImageRequest.Builder(
                getApplicationContext(),
                ImageRequest.getProfilePictureUri(fb_id, 400, 400));

        ImageRequest request = requestBuilder.setAllowCachedRedirects(false)
                .setCallerTag(this)
                .setCallback(
                        new ImageRequest.Callback() {
                            @Override
                            public void onCompleted(ImageResponse response) {
                                Bitmap responseImage = response.getBitmap();
                                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.storage_path));
                                StorageReference userImageRef = storageRef.child("user-photos/" + uid + "/image0.jpg");
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                if (responseImage == null) {
                                    callErrorDialog();
                                } else {
                                    responseImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();

                                    uploadTask = userImageRef.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            callErrorDialog();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("picture", downloadUrl.toString());
                                            editor.commit();
                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put(Constants.user + "/" + uid + "/" + Constants.user_info + "/picture/0/", downloadUrl);
                                            childUpdates.put(Constants.user + "-" + gender + "/" + uid + "/picture", downloadUrl);
                                            ref.updateChildren(childUpdates);
                                            startMainAc();
                                        }
                                    });
                                }
                            }
                        })
                .build();
        ImageDownloader.downloadAsync(request);

    }


    private void addToFirebase(final JSONObject obj, final String uid) {
        ref.child(Constants.user + "/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAll userAll = dataSnapshot.getValue(UserAll.class);
                Date date = null;
                try {
                    try {
                        date = new Date(obj.getString("birthday"));
                    } catch (JSONException e) {
                        callErrorDialog(getString(R.string.error_birthday));
                        return;
                    }
                    if (userAll != null) {

                        checkUserSettings(userAll.user_settings, uid, userAll.user_info.gender, date, obj.getString("name"));
                        if(userAll.user_info.picture!=null && userAll.user_info.picture.size()>0){
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("picture", userAll.user_info.picture.get(0).toString());
                            editor.commit();
                        }

                        startMainAc();
                    } else {

                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        User user = new User();

                        user.birthday = df.format(date);
                         try {
                             user.education = getSchool(obj.getJSONArray("education"));
                         }catch (JSONException e){
                             user.education = "";
                         }
                        try {
                            user.email = obj.getString("email");
                        }catch (JSONException e){
                            user.email = "";
                        }

                        user.gender = obj.getString("gender");
                        user.fb_id = obj.getString("id");
                        user.name = obj.getString("name");
                        try {
                            user.work = getWork(obj.getJSONArray("work"));
                        }catch (JSONException e){
                             user.work = "";
                        }

                        UserGender userGender = new UserGender(uid, user.birthday, user.fb_id, user.name, user.work);
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(Constants.user + "/" + uid + "/" + Constants.user_info, user.toMap());
                        childUpdates.put(Constants.user + "-" + user.gender + "/" + uid, userGender.toMap());
                        checkUserSettings(null, uid, user.gender, date, user.name);
                        ref.updateChildren(childUpdates);
                        try {
                            uploadImage(uid, user.fb_id, user.gender);
                        } catch (IOException e) {
                            e.printStackTrace();
                            callErrorDialog();
                        }


                    }
                } catch (JSONException e) {
                    callErrorDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callErrorDialog();
            }
        });


    }

    private void callErrorDialog(String error) {
        Toast.makeText(FullscreenActivity.this, error + "\n"+getString(R.string.error_do_again), Toast.LENGTH_LONG).show();
        hideProgressDialog();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    private void callErrorDialog() {
        Toast.makeText(FullscreenActivity.this, R.string.error_do_again, Toast.LENGTH_LONG).show();
        hideProgressDialog();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    private void checkUserSettings(UserSettings settings, final String uid, final String gender, final Date birthday, final String name) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        if (settings != null) {
            editor.putString("gender", settings.gender);
            editor.putString("age_range", settings.age_range);
            editor.commit();
        } else {
            UserSettings userSettings = new UserSettings();
            userSettings.gender = gender.equals("male") ? "female" : "male";
            int age = Utils.getAge(birthday);
            int firstAge = age <= 22 ? 18 : age - 4;
            int lastAge = age + 4;
            userSettings.age_range = firstAge + "-" + lastAge;
            editor.putString("gender", userSettings.gender);
            editor.putString("age_range", userSettings.age_range);
            editor.commit();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(Constants.user + "/" + uid + "/" + Constants.user_settings, userSettings.toMap());
            ref.updateChildren(childUpdates);
        }


    }

    private String getSchool(JSONArray school) {
        String schoolStr = "";
        if (school.length() > 0) {
            try {
                JSONObject item = school.getJSONObject(0);

                schoolStr = item.getJSONObject("school").getString("name");
                return schoolStr;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private String getWork(JSONArray work) {
        String workStr = "";
        if (work.length() > 0) {
            try {
                JSONObject item = work.getJSONObject(0);
                if (!item.isNull("position"))
                    workStr = item.getJSONObject("position").getString("name") + " at ";
                if (!item.isNull("employer"))
                    workStr = workStr + " " + item.getJSONObject("employer").getString("name");
                if (!item.isNull("location"))
                    workStr = workStr + " " + item.getJSONObject("location").getString("name");
                return workStr;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {


        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);


        }

        @Override
        public Fragment getItem(int position) {
            return ImageTextFragment.newInstance(titles[position], imageRes[position]);
        }

        @Override
        public int getCount() {
            return 3;
        }


    }

}
