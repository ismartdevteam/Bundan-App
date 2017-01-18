package ismartdev.mn.bundan;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ismartdev.mn.bundan.util.Constants;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Log.e(TAG, token);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // TODO: Implement this method to send token to your app server.
            Log.e(TAG, "islogged");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(Constants.user).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Constants.fcm).setValue(token);
        }


    }
}