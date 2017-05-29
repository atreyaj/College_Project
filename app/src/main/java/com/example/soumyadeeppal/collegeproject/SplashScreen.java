package com.example.soumyadeeppal.collegeproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SplashScreen extends Activity {

    private static ProgressDialog progressDialog = null;

    public static void showProgressDialog(Context context, String title, String message)
    {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void dismissProgressDialog()
    {
        progressDialog.dismiss();
    }
    public static void clearDialog()
    {
        progressDialog = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei_id = tm.getDeviceId();

        ConnectivityManager connMgr = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            Toast.makeText(SplashScreen.this, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

            new CheckRegistrationStatus().execute(imei_id);
        }
        else
        {
            Toast.makeText(SplashScreen.this,"Internet Connectivity not available",Toast.LENGTH_LONG).show();
            finishAndRemoveTask();
        }


    }



    class CheckRegistrationStatus extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute()
        {
            /*
            progressDialog = ProgressDialog.show(SplashScreen.this,"Getting registration status...",
                    "Verifying user data, please wait...", false, false);

                    */
            showProgressDialog(SplashScreen.this, "Getting Registration Status", "Verifying User Data...");
        }

    //Update the progress

    @Override
        protected String doInBackground(String... params) {
        InputStream is = null;
        String result = null;

        HttpURLConnection urlConnection = null;


        String BASE_URL = "http://locationfinder.000webhostapp.com/CheckRegistrationStatus.php?";

        String PARAM_STRING = "imei_id=" + params[0];
        String CONNECTION_URL = "" + BASE_URL + PARAM_STRING;
        System.out.println(CONNECTION_URL);
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(CONNECTION_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            int HttpResult = urlConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                System.out.println("Result code : " + HttpResult);
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                Log.v("response : ", "Response : " + sb.toString());
                result = sb.toString();
                System.out.println(result);

            } else {
                System.out.println(urlConnection.getResponseMessage());
            }
        } catch (Exception e) {
            Log.d("Error message : ", e.toString());
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return result;
    }

        @Override
        protected void onPostExecute(String result) {
            dismissProgressDialog();

            System.out.println("Query truth value :"+result);

            if (result!=null) {
                if (result.equals("1\n")) {
                    Toast.makeText(SplashScreen.this, "Device is registered .... loading contacts", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(SplashScreen.this, User_Screen.class);
                    startActivity(i);

                } else {
                    Toast.makeText(SplashScreen.this, "Device not registered . Please Register Device", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);


                }
            }
            super.onPostExecute(result);
        }
    }

}
