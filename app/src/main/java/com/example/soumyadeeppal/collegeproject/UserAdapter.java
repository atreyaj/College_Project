package com.example.soumyadeeppal.collegeproject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.NeighboringCellInfo;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by Soumyadeep Pal on 23-01-2017.
 */

public class UserAdapter extends BaseAdapter {
    Context c;
    LayoutInflater inflater;
    //ArrayList<HashMap<String, String>> object;

    ArrayList<UserInfo> object;

    UserAdapter(Context c1, ArrayList<UserInfo> object) {

        this.c = c1;
        this.object = object;
        inflater = LayoutInflater.from(c);
    }


    public int getCount() {
        return object.size();
    }

    public Object getItem(int position) {
        return object.get(position);
    }


    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
            convertView = inflater.inflate(R.layout.user_element, null);

            ImageView p1 = (ImageView) convertView.findViewById(R.id.photo);

            TextView username = (TextView) convertView.findViewById(R.id.username);

            // have to set onclick listener of invite button

            Button invite = (Button) convertView.findViewById(R.id.invite);


            //username.setText("" + object.get(position).entrySet().toString());

        username.setText(""+object.get(position).getName()+"\n"+object.get(position).getPhNo());


            invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new getGeolocationDataAsyncTask().execute("" + object.get(position).getPhNo());
                    //Intent i = new Intent(parent.getContext(), ShareLocation.class);
                    Log.d("Phno:", object.get(position).getPhNo());
                    //i.putExtra("phNo", object.get(position).getPhNo());
                    //parent.getContext().startActivity(i);
                }
            });

        return convertView;
    }


    class getGeolocationDataAsyncTask extends AsyncTask<String, Void, String> {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... params) {
            TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            int home_mcc, home_mnc, considerIp;

            //carrier Name :
            String carrierName = tm.getNetworkOperatorName();

            // Network type:
            int phoneTypeInt = tm.getPhoneType();
            String phoneType = null;

            if (phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM)
                phoneType = "gsm";
            else
                phoneType = "cdma";

            /*-----Code for setting up cellTower object from Google GeolocationAPI------*/
            JSONArray cellList = new JSONArray();

            List<CellInfo> infos = tm.getAllCellInfo();

            System.out.println("No of elements in list :" + infos.size());
            //for (int i = 0; i < infos.size(); ++i) {
                try {
                    JSONObject cellObj = new JSONObject();
                    //CellInfo info = infos.get(i);
                    CellInfo info=infos.get(0);
                    if (info instanceof CellInfoGsm) {
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

                        CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                        CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();

                        // for debugging purpose - printing is done
                        System.out.println("LTE info:");
                        System.out.println("" + identityLte.getCi());
                        System.out.println("" + identityLte.getTac());
                        System.out.println("" + lte.getDbm());
                        System.out.println("" + identityLte.getMcc());
                        System.out.println("" + identityLte.getMnc());

                        // end of debugging


                        cellObj.put("cellId", identityLte.getCi());
                        cellObj.put("tac", identityLte.getTac());
                        cellObj.put("mcc", identityLte.getMcc());
                        cellObj.put("mnc", identityLte.getMnc());
                        cellObj.put("dbm", lte.getDbm());
                        cellList.put(cellObj);
                    }

                } catch (Exception ex) {
                    System.out.println("Error in cellTower object");
                }

            // } end of for


            String networkOperator = tm.getNetworkOperator();
            int mcc = 0, mnc = 0;

            if (!TextUtils.isEmpty(networkOperator)) {
                mcc = Integer.parseInt(networkOperator.substring(0, 3));
                mnc = Integer.parseInt(networkOperator.substring(3));
            }


            System.out.println("Home Network MCC and MNC : " + mcc + " and " + mnc);
            GeolocationApiCall call = new GeolocationApiCall();
            String location_latlong = call.getLatLongString(mcc, mnc, phoneType, carrierName, true, cellList);
            double lat = 0.0, lng = 0.0;

            try {

                JSONObject res = new JSONObject(location_latlong);
                JSONObject location = res.getJSONObject("location");

                lat = location.getDouble("lat");
                lng = location.getDouble("lng");
            } catch (Exception e) {
                System.out.println("Error in Response JSON Parsing");
            }

            ShareLocation sloc = new ShareLocation("Message:", "Latitude:"+lat+"%20Longitude:"+lng, params[0]);

            String response = sloc.send();

            return response;


        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Toast.makeText(c.getApplicationContext(), "Response : " + response, Toast.LENGTH_LONG).show();


            //Toast.makeText(c.getApplicationContext(),"Result:\n"+result,Toast.LENGTH_LONG).show();

        }
    }
}
