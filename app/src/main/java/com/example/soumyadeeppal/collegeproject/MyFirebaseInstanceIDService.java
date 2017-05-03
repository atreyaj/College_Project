package com.example.soumyadeeppal.collegeproject;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Soumyadeep Pal on 02-02-2017.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
        @Override
        public void onTokenRefresh() {
            String token= FirebaseInstanceId.getInstance().getToken();

            storeToken(token);
        }

        public void storeToken(String token) {
            SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
        }

        /*public String getToken()
        {
            onTokenRefresh();
            SharedPreferences sp=c.getSharedPreferences("FIREBASE",Context.MODE_PRIVATE);
            return sp.getString("token",null);
        }*/
    }
