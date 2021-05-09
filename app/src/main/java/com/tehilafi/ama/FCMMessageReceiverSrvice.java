package com.tehilafi.ama;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMMessageReceiverSrvice extends FirebaseMessagingService {

    private static final String FCM_CHANNEL_ID = "FCM_CHANNEL_ID";
    public static final String TAG = "MyTag";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Notification notification = new NotificationCompat.Builder(this, FCM_CHANNEL_ID)
                    .setSmallIcon(R.drawable.masseg).setContentTitle(title).setContentText( body ).setColor( Color.BLUE ).build();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(1002, notification);
        }
        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, "onMessageReceived: Data Size:" + remoteMessage.getData().size());
            for(String key: remoteMessage.getData().keySet()){
                Log.d(TAG, "onMessageReceived key:" + key + "DAta:" + remoteMessage.getData().get(key));
            }
            Log.d(TAG, "onMessageReceived: Data:" + remoteMessage.getData().toString());
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        // upload this token on app server
    }
}
