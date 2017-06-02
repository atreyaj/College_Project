package com.example.soumyadeeppal.collegeproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.telephony.CellLocation;
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
import java.net.URLEncoder;
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
    TextView name;
    String token;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone = (EditText) findViewById(R.id.phone);

        b = (Button) findViewById(R.id.register);

        name=(TextView)findViewById(R.id.name);

        getSupportActionBar().hide();

        //displayToken = (Button) findViewById(R.id.display);

        //tv = (TextView) findViewById(R.id.tv);
        auth = auth.getInstance();

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ph_no = phone.getText().toString();


                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                Intent i = new Intent(MainActivity.this, MyFirebaseInstanceIDService.class);
                startService(i);


                String gcm_id = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
                String imei_id = tm.getDeviceId();

                String username=""+name.getText().toString();

                if (username=="" || ph_no=="")
                {
                    Toast.makeText(MainActivity.this,"Please enter both fields",Toast.LENGTH_LONG).show();
                }
                else {
                    RegisterAsyncTask obj = new RegisterAsyncTask(getBaseContext());
                    obj.execute(imei_id, ph_no, gcm_id, username);
                }

                /*
                System.out.println("imei_id : "+imei_id);
                System.out.println("phno : "+phno);
                System.out.println("FCM id : "+ gcm_id);

                */
            }
        });


        /*displayToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyFirebaseInstanceIDService service = new MyFirebaseInstanceIDService(getBaseContext());
                String gcm_id = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
                tv.setText("Registration Token : " + gcm_id);
                System.out.println("Token : " + gcm_id);
            }
        });*/
    }

    class RegisterAsyncTask extends AsyncTask<String, Void, String> {
        Context c;

        RegisterAsyncTask(Context context) {
            this.c = context;
        }


        @Override
        protected String doInBackground(String... params) {

            InputStream is = null;
            String result = null;


            String BASE_URL = "http://locationfinder.000webhostapp.com/RegisterDevice.php?";
            String PARAM_STRING="";
            try {
                PARAM_STRING = "imei_id=" + params[0] + "&ph_no=" + params[1] + "&gcm_id=" + params[2] + "&username="+
                        URLEncoder.encode(params[3],"UTF-8");

            }catch (Exception e)
            {
                System.out.println(e.toString());
            }

            String CONNECTION_URL = "" + BASE_URL + PARAM_STRING;

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
                    String line = null;
                    while ((line = br.readLine()) != null) {
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

            Toast.makeText(MainActivity.this, "" + s, Toast.LENGTH_LONG).show();

            Intent i = new Intent(MainActivity.this, User_Screen.class);

            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            this.c.startActivity(i);


        }
    }
}
