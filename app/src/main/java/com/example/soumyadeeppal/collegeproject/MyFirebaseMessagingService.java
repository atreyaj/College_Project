package com.example.soumyadeeppal.collegeproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

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
            String type=data.getString("type");
            double lat=Double.parseDouble(data.getString("lat"));
            double lng=Double.parseDouble(data.getString("lng"));

            System.out.println("lat :"+lat+ "\n" + "lng :"+lng);

            NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            Notification notification = new Notification(R.mipmap.location_share, null,
                    System.currentTimeMillis());

            if (type.equals("0")) {

                String address = "", city = "", country="",state = "", postalCode = "", knownName = "", fullAddress = "";
                    try {
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                        addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        country = addresses.get(0).getCountryName();
                        postalCode = addresses.get(0).getPostalCode();
                        knownName = addresses.get(0).getFeatureName();
                    } catch (Exception e) {
                    }
                if (address == null && city == null && state == null && country==null && postalCode == null)
                    fullAddress = knownName;
                else
                    fullAddress = "" + address +" " + city +" " + state ;

                System.out.println(fullAddress);



                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + fullAddress);

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent switch_to_map = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                switch_to_map.setPackage("com.google.android.apps.maps");

                PendingIntent mapIntent = PendingIntent.getActivity(getApplicationContext(), 0, switch_to_map, PendingIntent.FLAG_UPDATE_CURRENT);


                RemoteViews notificationView = new RemoteViews(getPackageName(),
                        R.layout.notification_layout);

                notificationView.setTextViewText(R.id.nmessage, title + "\n" + message);
                notificationView.setImageViewResource(R.id.nimage, R.mipmap.location_share);


                //the intent that is started when the notification is clicked (works)
                //Intent notificationIntent = new Intent(this, User_Screen.class);
                //PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);

                Uri notification_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                notification.contentView = notificationView;
                notification.contentIntent = mapIntent;
                notification.sound = notification_sound;
                notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                //this is the intent that is supposed to be called when the
                //button is clicked

                /*
                Intent switchIntent = new Intent(this, User_Screen.class);
                PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0,
                        switchIntent, 0);

                notificationView.setOnClickPendingIntent(R.id.share,
                        pendingSwitchIntent);

                        */

                nm.notify(0,notification);
            }


            if (type.equals("1"))
            {
                Intent switchIntent = new Intent(this, User_Screen.class);

                PendingIntent mapIntent = PendingIntent.getActivity(getApplicationContext(), 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                RemoteViews notificationView = new RemoteViews(getPackageName(),
                        R.layout.notification_layout);

                notificationView.setTextViewText(R.id.nmessage, title + "\n" + message);
                notificationView.setImageViewResource(R.id.nimage, R.mipmap.location_share);


                //the intent that is started when the notification is clicked (works)
                //Intent notificationIntent = new Intent(this, User_Screen.class);
                //PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);

                notification.contentView = notificationView;
                notification.contentIntent = mapIntent;
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                //this is the intent that is supposed to be called when the
                //button is clicked

                /*
                Intent switchIntent = new Intent(this, User_Screen.class);
                PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0,
                        switchIntent, 0);

                notificationView.setOnClickPendingIntent(R.id.share,
                        pendingSwitchIntent);

                        */

                nm.notify(1, notification);

            }




/*


            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(),100,new Intent(MyFirebaseMessagingService.this,User_Screen.class),PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder nbuilder=new Notification.Builder(getApplicationContext());
            Notification notification=nbuilder.setAutoCancel(true)
                    .setContent(notificationView)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(mapIntent)
                    .build();


            nm.notify(100,notification);

            */




        }catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
