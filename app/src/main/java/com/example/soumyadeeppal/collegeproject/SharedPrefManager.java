package com.example.soumyadeeppal.collegeproject;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Soumyadeep Pal on 03-02-2017.
 */

public class SharedPrefManager {

    private static SharedPrefManager instance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences("FIREBASE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences("FIREBASE", Context.MODE_PRIVATE);
        return  sharedPreferences.getString("token", null);
    }
}
