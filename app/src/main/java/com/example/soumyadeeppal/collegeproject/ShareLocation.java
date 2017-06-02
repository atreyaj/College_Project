package com.example.soumyadeeppal.collegeproject;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Soumyadeep Pal on 12-02-2017.
 */

public class ShareLocation {

    String title;
    String location_message;
    String sendto_no;
    String from_imei;
    String lat;
    String lng;

    String response;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    ShareLocation(String title, String location_message, String sendto_no,String from_imei,String lat,String lng) {
        this.title = title;
        this.location_message = location_message;
        this.sendto_no = (sendto_no.charAt(0)=='+')?(PhoneNumberUtils.normalizeNumber(sendto_no)).substring(3):
                (PhoneNumberUtils.normalizeNumber(sendto_no));

        this.from_imei=from_imei;

        this.lat=lat;
        this.lng=lng;
    }

    public String send() {
        String BASE_URL = "http://locationfinder.000webhostapp.com/sendSinglePush.php?";
        String PARAM_STRING="";
        try {
            PARAM_STRING = "title=" + URLEncoder.encode(this.title+this.sendto_no, "UTF-8") + "&message=" + URLEncoder.encode(this.location_message,"UTF-8") + "&ph_no=" + this.sendto_no
            +"&from_imei=" + URLEncoder.encode(this.from_imei,"UTF-8") + "&lat="+URLEncoder.encode(this.lat,"UTF-8") + "&lng="+URLEncoder.encode(this.lng,"UTF-8");
        }catch (Exception e)
        {}
        String CONNECTION_URL = BASE_URL + PARAM_STRING;


        System.out.println("Share lo" +
                "" +
                "cation URL : " + CONNECTION_URL);

        StringBuffer sb = new StringBuffer();

        HttpURLConnection urlConnection = null;
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
                this.response = sb.toString();
                System.out.println(response);

            } else {
                System.out.println(urlConnection.getResponseMessage());
            }
        } catch (Exception e) {
            Log.d("Error message : ", e.toString());
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return response;
    }
}
