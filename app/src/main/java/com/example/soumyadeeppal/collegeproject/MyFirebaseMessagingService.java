package com.example.soumyadeeppal.collegeproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

/**
 * Created by Soumyadeep Pal on 31-01-2017.
 */

// this class handles displaying push notifications in a particular device

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if ((remoteMessage.getData().size())>0)
        {

            //System.out.println("Message received : "+remoteMessage.getNotification().getTitle()+"\n"+remoteMessage.getNotification().getBody());

            try
            {
                JSONObject obj=new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(obj);
            }catch (Exception e)
            {
                System.out.println("Error in retreiving push notification json");
            }
        }
    }

    public void sendPushNotification(JSONObject obj)
    {
        try {
            JSONObject data=obj.getJSONObject("data");
            String title = data.getString("title");
            String message=data.getString("message");

            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+message.substring(10));

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent switch_to_map = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
            switch_to_map.setPackage("com.google.android.apps.maps");

            PendingIntent mapIntent=PendingIntent.getActivity(getApplicationContext(),0,switch_to_map,PendingIntent.FLAG_UPDATE_CURRENT);




            NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(),100,new Intent(MyFirebaseMessagingService.this,User_Screen.class),PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder nbuilder=new Notification.Builder(getApplicationContext());
            Notification notification=nbuilder.setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(mapIntent)
                    .build();

            nm.notify(100,notification);

        }catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
