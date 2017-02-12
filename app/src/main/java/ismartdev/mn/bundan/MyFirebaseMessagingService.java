package ismartdev.mn.bundan;

/**
 * Created by Ulzii on 11/9/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import ismartdev.mn.bundan.MainActivity;
import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.util.Constants;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = null;
        if (remoteMessage.getData().size() > 0) {
            data = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "notification");
            if (data != null) {
                Bundle b = new Bundle();
                if (!TextUtils.isEmpty(data.get("matched"))) {
                    b.putString("matched", remoteMessage.getData().get("matched"));
                    b.putString("picture", remoteMessage.getData().get("picture"));
                    b.putString("name", remoteMessage.getData().get("name"));
                    Intent matchIntent = new Intent(getApplicationContext(), MatchActivity.class);
                    matchIntent.putExtras(b);
                    matchIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(matchIntent);
                } else if (!TextUtils.isEmpty(data.get("matchID"))) {
                    String matchID = data.get("matchID");
                    String uid = data.get("uid");
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.sp_app, Context.MODE_PRIVATE);
                    if (!sharedPreferences.getString("matchID", "").equals(matchID)) {
                        Bundle messageBundle = new Bundle();
                        messageBundle.putString("matchID", matchID);
                        messageBundle.putString("uid", uid);
                        sendNotificationChat(Integer.parseInt(uid.replaceAll("[\\D]", "")), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getTitle(), messageBundle);
                    }
                }


            } else {
                sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getTitle());
            }

        }

    }

    private void sendNotificationChat(int notiId, String title, String messageBody, Bundle b) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtras(b);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)

                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        notificationManager.notify(notiId, notificationBuilder.build());
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)

                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}