package com.example.soumyadeeppal.collegeproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.country;

public class MainActivity extends AppCompatActivity {
    HttpURLConnection urlConnection;


    EditText phone;

    private FirebaseAuth auth;

    Button b; // for registration


    // UID example : SntVNXo3F2WR0WQXLFm1mXjYJCq1


    Button displayToken;

    Button display_my_location;

    String token;
    TextView tv;

    Button list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone = (EditText) findViewById(R.id.phone);

        b = (Button) findViewById(R.id.register);

        display_my_location=(Button)findViewById(R.id.mylocation);

        displayToken = (Button) findViewById(R.id.display);

        tv = (TextView) findViewById(R.id.tv);
        list = (Button) findViewById(R.id.list);

        auth = auth.getInstance();

        b.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {


                /*
                auth.createUserWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Authorization Succesful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Authorization Not Successful", Toast.LENGTH_LONG).show();

                        }
                    }
                });

                */


                                     TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                     Intent i = new Intent(MainActivity.this, MyFirebaseInstanceIDService.class);
                                     startService(i);


                                     String gcm_id = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
                                     String imei_id = tm.getDeviceId();
                                     String ph_no = phone.getText().toString();
                                     RegisterAsyncTask obj = new RegisterAsyncTask();
                                     obj.execute(imei_id, ph_no, gcm_id);


                /*
                System.out.println("imei_id : "+imei_id);
                System.out.println("phno : "+phno);
                System.out.println("FCM id : "+ gcm_id);

                */
                                 }

                             });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, User_Screen.class);
                startActivity(i);
            }
        });


        displayToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyFirebaseInstanceIDService service = new MyFirebaseInstanceIDService(getBaseContext());
                String gcm_id = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
                tv.setText("Registration Token : " + gcm_id);
                System.out.println("Token : " + gcm_id);
            }
        });

        display_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KnowMyLocationAsyncTask myloc=new KnowMyLocationAsyncTask();
                myloc.execute();
            }
        });
    }

    class KnowMyLocationAsyncTask extends AsyncTask<Void,String,String>
    {

        @Override
        protected String doInBackground(Void... params) {
            TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);


            boolean gsmTrue=false;
            int home_mcc, home_mnc, considerIp;

            //carrier Name :
            String carrierName = tm.getNetworkOperatorName();

            // Network type:
            int phoneTypeInt = tm.getPhoneType();
            String phoneType = null;

           /* if (phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM)
                phoneType = "gsm";
            else
                phoneType = "lte";

                */

            /*-----Code for setting up cellTower object from Google GeolocationAPI------*/
            JSONArray cellList = new JSONArray();

            List<CellInfo> infos = tm.getAllCellInfo();

            System.out.println("No of elements in list :" + infos.size());
            //for (int i = 0; i < infos.size(); ++i) {
                try {
                    JSONObject cellObj = new JSONObject();
                    CellInfo info = infos.get(0);
                    if (info instanceof CellInfoGsm) {
                        gsmTrue=true;
                        CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                        CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                        System.out.println("GSM info:");
                        System.out.println("" + identityGsm.getCid());
                        System.out.println("" + identityGsm.getLac());
                        System.out.println("" + identityGsm.getMnc());
                        System.out.println("" + identityGsm.getMcc());


                        cellObj.put("cellId", identityGsm.getCid());
                        cellObj.put("lac", identityGsm.getLac());
                        cellObj.put("mcc", identityGsm.getMcc());
                        cellObj.put("mnc", identityGsm.getMnc());
                        cellObj.put("dbm", gsm.getDbm());
                        cellList.put(cellObj);
                    } else if (info instanceof CellInfoLte) {

                        gsmTrue=false;

                        CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                        CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();

                        // for debugging purpose - printing is done
                        System.out.println("LTE info:");
                        System.out.println("" + (identityLte.getCi()& 0xffff));
                        System.out.println("" + identityLte.getTac());
                        System.out.println("" + lte.getDbm());
                        System.out.println("" + identityLte.getMcc());
                        System.out.println("" + identityLte.getMnc());

                        // end of debugging


                        cellObj.put("cellId", (identityLte.getCi()& 0xffff));
                        cellObj.put("lac", identityLte.getTac());
                        cellObj.put("mcc", identityLte.getMcc());
                        cellObj.put("mnc", identityLte.getMnc());
                        cellObj.put("dbm", lte.getDbm());
                        cellList.put(cellObj);
                    }

                } catch (Exception ex) {
                    System.out.println("Error in cellTower object");
                }

             //}


            String networkOperator = tm.getNetworkOperator();
            int mcc = 0, mnc = 0;

            if (!TextUtils.isEmpty(networkOperator)) {
                mcc = Integer.parseInt(networkOperator.substring(0, 3));
                mnc = Integer.parseInt(networkOperator.substring(3));
            }


            System.out.println("Home Network MCC and MNC : " + mcc + " and " + mnc);
            GeolocationApiCall call = new GeolocationApiCall();
            String location_latlong="";
            if (gsmTrue==true)
                location_latlong=call.getLatLongString(mcc, mnc, "gsm", carrierName, true, cellList);
            else
                location_latlong=call.getLatLongString(mcc, mnc, "lte", carrierName, true, cellList);
            return location_latlong;
        }

        @Override
        protected void onPostExecute(String s) {
            double lat = 0.0, lng = 0.0;

            try {

                JSONObject res = new JSONObject(s);
                JSONObject location = res.getJSONObject("location");

                lat = location.getDouble("lat");
                lng = location.getDouble("lng");
            } catch (Exception e) {
                System.out.println("Error in Response JSON Parsing");
            }


            //regToken.setText("Latitude :"+lat+"\n"+"Longitude :"+lng);
            //Toast.makeText(MainActivity.this,"Latitude :"+lat+"\n"+"Longitude :"+lng,Toast.LENGTH_LONG).show();

            String address = "", city = "", country="",state = "", postalCode = "", knownName = "", fullAddress = "";
            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();
            } catch (Exception e) {
            }
            if (address == null && city == null && state == null /*&& country==null*/ && postalCode == null)
                fullAddress = knownName;
            else
                fullAddress = "" + address + city + state + country + postalCode;

            tv.setText("Latitude :"+lat+"\n"+"Longitude :"+lng+"\n"+"Address :"+fullAddress);
            //Toast.makeText(MainActivity.this, "Latitude :" + lat + "\n" + "Longitude :" + lng+"\n"+"Address : "+fullAddress, Toast.LENGTH_LONG).show();
        }

    }

    class RegisterAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            InputStream is=null;
            String result=null;


            String BASE_URL = "http://locationfinder.000webhostapp.com/RegisterDevice.php?";
            String PARAM_STRING="imei_id="+params[0]+"&ph_no="+params[1]+"&gcm_id="+params[2];
            String CONNECTION_URL=""+BASE_URL+PARAM_STRING;

            System.out.println(CONNECTION_URL);



            try {


                URL url = new URL(CONNECTION_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int HttpResult = urlConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    System.out.println("Result code : " + HttpResult);
                    is = urlConnection.getInputStream();
                    StringBuffer sb = new StringBuffer();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line=null;
                    while (  (line = br.readLine()) != null ) {
                        sb.append(line);
                    }
                    Log.v("response : ", "Response : " + sb.toString());
                    result = sb.toString();
                    System.out.println(result);

                } else {
                    System.out.println(urlConnection.getResponseMessage());
                }
            } catch (Exception e) {
                Log.d("Error message : ", e.toString());
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            Toast.makeText(MainActivity.this, "Result : \n"+s, Toast.LENGTH_LONG).show();
        }
    }



}
