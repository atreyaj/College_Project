package com.example.soumyadeeppal.collegeproject;

import android.content.Context;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Soumyadeep Pal on 24-01-2017.
 */

public class GeolocationApiCall {

    String weatherJSONString;
    byte array[];
    ByteArrayOutputStream bastream;

    String result;

    //public String APP_ID="AIzaSyD61TaY2_ncBoygseOtZKWbPln01BU4Ymk";

    public String BASE_URL = "http://locationfinder.000webhostapp.com/json.php?";
    public String PARAM_STRING;
    public String CONNECTION_URL;

    public String getLatLongString(int homeNetworkMcc,int homeNetworkMnc,String radioType,String carrierName,boolean considerIp,
                                   JSONArray cellTowers) {
        System.out.println("Parameters inside GeoLocationApiClass getLatLongString method--------");

        System.out.println(""+homeNetworkMcc);
        System.out.println(""+homeNetworkMnc);
        System.out.println(""+radioType);
        System.out.println(""+carrierName);
        System.out.println(""+considerIp);

        StringBuffer sb = new StringBuffer();
            try {
                System.out.println("mcc" + homeNetworkMcc);
                PARAM_STRING = "homeMobileCountryCode=" + homeNetworkMcc + "&homeMobileNetworkCode=" + homeNetworkMnc + "&radioType=" + radioType + "&carrier=" + carrierName + "&considerIp=" + considerIp + "&cellId=" +
                        cellTowers.getJSONObject(0).getInt("cellId")
                        + "&locationAreaCode=" + cellTowers.getJSONObject(0).getInt("lac") + "&mobileCountryCode="
                        + cellTowers.getJSONObject(0).getInt("mcc") + "&mobileNetworkCode="
                        + cellTowers.getJSONObject(0).getInt("mnc") + "&signalStrength="
                        + cellTowers.getJSONObject(0).getInt("dbm");

            } catch (Exception e) {
                Log.v("Exception :", "Error in getting JSON Data in PARAM_STRING");
            }



        CONNECTION_URL=BASE_URL+PARAM_STRING;
        System.out.println("API URL : "+CONNECTION_URL);

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
                System.out.println("Result code : "+HttpResult);
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                Log.v("response : ", "Response : "+sb.toString());
                result=sb.toString();
                //System.out.println(result);

            } else {
                System.out.println(urlConnection.getResponseMessage());
            }
        } catch (Exception e)
        {
            Log.d("Error message : ", e.toString());
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return result;
    }
}

/*
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));;
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
    }
}

*/
