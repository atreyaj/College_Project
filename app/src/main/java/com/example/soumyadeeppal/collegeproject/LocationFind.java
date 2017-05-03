package com.example.soumyadeeppal.collegeproject;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static android.R.attr.country;

public class LocationFind extends AppCompatActivity {

    String address,city,state,country,postalCode,knownName;
    String fullAddress;

    LocationManager manager;
    LocationListener listener;

    String phNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_find);

        phNo=getIntent().getStringExtra("phNo");

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new MyLocation();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);

        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
    }

    class MyLocation implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat=location.getLatitude();
            double longt=location.getLongitude();


            Toast.makeText(LocationFind.this, "Latitude : " + lat + "\n" + "Longitude : " + longt, Toast.LENGTH_LONG).show();

            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(LocationFind.this, Locale.getDefault());

                addresses = geocoder.getFromLocation(lat, longt, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();
            }catch(Exception e)
            {}
            if (address==null && city==null && state==null && country==null && postalCode==null)
                fullAddress=knownName;
            else
                fullAddress=""+address + city + state + country + postalCode;


            Log.v("Address : ",fullAddress);


            if (fullAddress.length()==0)
                Toast.makeText(LocationFind.this,"Location listener did not receive location update yet",Toast.LENGTH_LONG).show();
            else
            {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                manager.removeUpdates(listener);
                manager=null;
                Toast.makeText(LocationFind.this,"Location Listener disabled ", Toast.LENGTH_LONG).show();
                Toast.makeText(LocationFind.this,"Adress : "+fullAddress, Toast.LENGTH_LONG).show();
                Intent i=new Intent(LocationFind.this,ReportSMSStatus.class);
                i.putExtra("phNo", phNo);
                i.putExtra("fullAddress",fullAddress);
                startActivity(i);
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(LocationFind.this, "GPS ENABLED", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(LocationFind.this, "GPS DISABLED", Toast.LENGTH_LONG).show();

        }
    }
}

    /*
    String BASE_URL = "http://locationfinder.000webhostapp.com/json.php?";
    URL url;
    String JSONString;
    int cellid;
    int lac;
    String deviceid;
    String phonenumber;
    String softwareversion;
    String operatorname;
    String simcountrycode;
    String simoperator;
    String simserialno;
    String subscriberid;
    String myURL;
    byte array[];
    ByteArrayOutputStream bastream;
    HttpURLConnection conn = null;
    InputStream is = null;
    TelephonyManager tel;
    int mcc = 0, mnc = 0, ntwrktype;
    String operatorName;
    String networkOperator;
    @Override



        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        networkOperator = tel.getNetworkOperator();

        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
        }

        operatorName = tel.getNetworkOperatorName();

        ntwrktype = tel.getNetworkType();

        GsmCellLocation loc = (GsmCellLocation) tel.getCellLocation();
        int cellid = loc.getCid();
        int lac = loc.getLac();
        deviceid = tel.getDeviceId();
        phonenumber = tel.getLine1Number();
        softwareversion = tel.getDeviceSoftwareVersion();
        simcountrycode = tel.getSimCountryIso();
        simoperator = tel.getSimOperatorName();
        simserialno = tel.getSimSerialNumber();
        subscriberid = tel.getSubscriberId();

        String s=getData();
        Toast.makeText(getBaseContext(),""+s,Toast.LENGTH_LONG).show();
    }

    public String getData()
    {

        try {

            myURL = BASE_URL + "homeMobileCountryCode=" + mcc + "&homeMobileNetworkCode=" + mnc + "&radioType=" + ntwrktype + "&carrier=" + operatorName + "&cellId=" + deviceid + "&locationAreaCode=" + lac + "&signalStrength=" + -60;
            url = new URL(myURL);

        }catch (Exception e) {
            Log.e("exception", e.toString());
        }

            Log.v("URL : ",myURL);


            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                //conn.setDoOutput(true);
                conn.connect();

                is = conn.getInputStream();

                StringBuffer buffer = new StringBuffer();

                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line=null;

                while (  (line = br.readLine()) != null ) {
                    buffer.append(line);
                }


                JSONString = buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                    conn.disconnect();
                }catch (Exception e) {

                }
            }

            return JSONString;
        }

        */
