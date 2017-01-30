package ismartdev.mn.bundan;

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

import ismartdev.mn.bundan.models.Image;
import ismartdev.mn.bundan.models.User;
import ismartdev.mn.bundan.models.UserGender;
import ismartdev.mn.bundan.models.UserSettings;
import ismartdev.mn.bundan.util.Constants;
import ismartdev.mn.bundan.util.Utils;


public class FullscreenActivity extends BaseActivity {
    private static final String TAG = "FullscreenActivity";
    private FirebaseAuth mAuth;
    private static final List<String> PERMISSIONS = Arrays.asList("public_profile", "email", "user_birthday", "user_friends", "user_likes", "user_work_history", "user_education_history");
    private CallbackManager mCallbackManager;
    private Task uploadTask;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_fullscreen);

        sharedPreferences = getSharedPreferences(Constants.sp_search, Context.MODE_PRIVATE);
        fbHash();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        showProgressDialog();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "is logged");
            finish();
            startMainAc();

        } else {

            mCallbackManager = CallbackManager.Factory.create();

        }
        hideProgressDialog();
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
                        try {
                            addToFirebase(object, getUid());

                        } catch (JSONException e) {
                            Toast.makeText(FullscreenActivity.this, e.getMessage() + "", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }


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

                                    hideProgressDialog();
                                    startMainAc();
                                } else {
                                    responseImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();

                                    uploadTask = userImageRef.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            startMainAc();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("picture", downloadUrl.toString());
                                            editor.commit();
                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put(Constants.user + "/" + uid + "/" + Constants.user_info + "/picture/0", downloadUrl);
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

    private void addToFirebase(JSONObject obj, String uid) throws JSONException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        User user = new User();
        Date date = new Date(obj.getString("birthday"));
        user.birthday = df.format(date);
        user.education = getSchool(obj.getJSONArray("education"));
        user.email = obj.getString("email");
        user.gender = obj.getString("gender");
        user.fb_id = obj.getString("id");
        user.name = obj.getString("name");
        user.work = getWork(obj.getJSONArray("work"));
//        user.user_friends = obj.getJSONObject("friends") + "";
//        user.user_likes = obj.getJSONObject("likes") + "";
        UserGender userGender = new UserGender(uid, user.birthday, user.fb_id, user.name, user.work);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(Constants.user + "/" + uid + "/" + Constants.user_info, user.toMap());
        childUpdates.put(Constants.user + "-" + user.gender + "/" + uid, userGender.toMap());

        ref.updateChildren(childUpdates);

        checkUserSettings(uid, user.gender, date);

        try {
            uploadImage(uid, user.fb_id, user.gender);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            hideProgressDialog();
        }
    }

    private void checkUserSettings(final String uid, final String gender, final Date birthday) {
        ref.child(Constants.user + uid).child(Constants.user_settings).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserSettings settings = dataSnapshot.getValue(UserSettings.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

}
