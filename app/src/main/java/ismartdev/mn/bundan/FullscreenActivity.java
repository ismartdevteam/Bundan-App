package ismartdev.mn.bundan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ismartdev.mn.bundan.models.User;
import ismartdev.mn.bundan.models.UserGender;
import ismartdev.mn.bundan.util.Constants;


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

                        hideProgressDialog();
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
                            startMainAc();
                        } catch (JSONException e) {
                            Toast.makeText(FullscreenActivity.this, e.getMessage() + "", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }


                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,gender,education,birthday,picture.type(large),email,friends,work,likes");
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


                                StorageReference userImageRef = storageRef.child("user-photos/" + uid + "/main.jpg");

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                if (responseImage == null) {
                                    Log.e(TAG,"fuck");
                                    startMainAc();
                                } else {
                                    responseImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();

                                    uploadTask = userImageRef.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle unsuccessful uploads
                                            startMainAc();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            SharedPreferences.Editor editor = sharedPreferences.edit();

                                            User user = new User();
                                            user.picture = downloadUrl.toString();
                                            editor.putString("picture", user.picture);
                                            editor.commit();

                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put(Constants.user + "/" + uid + "/picture", user.picture);
                                            childUpdates.put(Constants.user + "-" + gender + "/" + uid + "/picture", user.picture);
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
        user.birthday = df.format(new Date(obj.getString("birthday")));
        user.education = obj.getJSONArray("education").toString();
        user.email = obj.getString("email");
        user.gender = obj.getString("gender");
        user.fb_id = obj.getString("id");
          user.picture=obj.getJSONObject("picture").getJSONObject("data").getString("url");

        user.name = obj.getString("name");
        user.work = obj.getJSONArray("work").toString();
        user.user_friends = obj.getJSONObject("friends") + "";
        user.user_likes = obj.getJSONObject("likes") + "";
        UserGender userGender = new UserGender(uid, user.birthday, user.fb_id, user.name,user.picture);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(Constants.user + "/" + uid, user.toMap());
        childUpdates.put(Constants.user + "-" + user.gender + "/" + uid, userGender.toMap());

        ref.updateChildren(childUpdates);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (user.gender.equals(Constants.male)) {
            editor.putString("gender", Constants.female);
        } else {
            editor.putString("gender", Constants.male);
        }
        editor.commit();

//        try {
//            uploadImage(uid, user.fb_id, user.gender);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
