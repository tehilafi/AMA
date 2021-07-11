package com.tehilafi.ama.not;

import android.app.Activity;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tehilafi.ama.R;
import com.tehilafi.ama.db.Users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationSender extends Activity {
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String KEY = "AAAAz9nakfs:APA91bF0bPducWYEktyo853c12ijZky7ibg0RycbOKgrY7tLKrBxjiKY9b9rb6nKDmiBuy4IfSIGy6A-vIMk4l2s9pIYLqYEATwEbB9i_zjKFRx73hK_KcmO2ygedHE7nl2nHEU073Ho";
    private static final String SERVER_KEY = "key=" + KEY;
    private static final String CONTENT_TYPE = "application/json";
    private static DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child( "Users" );

    public static void sendNotification(ArrayList<String> receiverId, String senderName, String message, String send, String to_activity){

        for(int i = 0; i <receiverId.size(); i++) {
            String s = receiverId.get( i );
            String[] parts1 = s.split(",");
            String id = parts1[1];

            reff = FirebaseDatabase.getInstance().getReference( "Users" ).child(id);
            reff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String token = dataSnapshot.getValue( Users.class).getToken();
                    JSONObject json = new JSONObject();
                    try {
                        json.put( "title", senderName );
                        JSONObject notification = new JSONObject();
                        notification.put( "title", senderName );
                        notification.put( "body", message );
                        notification.put("click_action", to_activity);
                        json.put( "notification", notification );
                        json.put( "to", token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AndroidNetworking.post(NotificationSender.FCM_API)
                            .addJSONObjectBody( json )
                            .addHeaders( "Authorization", SERVER_KEY )
                            .addHeaders( "Content-Type", CONTENT_TYPE )
                            .setTag( "test" )
                            .setPriority( Priority.HIGH )

                            .build()
                            .getAsString( new StringRequestListener() {
                                @Override
                                public void onResponse(String result) {
                                }

                                @Override
                                public void onError(ANError error) {
                                }
                            } );
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}
