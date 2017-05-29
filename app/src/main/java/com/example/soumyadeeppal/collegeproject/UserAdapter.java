package com.example.soumyadeeppal.collegeproject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskCompletionSource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.USER_SERVICE;

/**
 * Created by Soumyadeep Pal on 23-01-2017.
 */

public class UserAdapter extends BaseAdapter implements Filterable{
    Context c;
    LayoutInflater inflater;
    ArrayList<UserInfo> multi_send;
    ViewHolder viewHolder;
    private ItemFilter mFilter=new ItemFilter();

    private static ProgressDialog progressDialog = null;

    //ArrayList<HashMap<String, String>> object;

    ArrayList<UserInfo> object;

    ArrayList<UserInfo> filtered_objects;
    CheckBox cb;

    SparseBooleanArray checked_items;
    UserAdapter(Context c1, ArrayList<UserInfo> object) {

        this.c = c1;
        progressDialog = new ProgressDialog(c1);
        this.object = object;
        inflater = LayoutInflater.from(c);
        this.filtered_objects=object;
        this.checked_items=new SparseBooleanArray(object.size());
        this.multi_send=new ArrayList<UserInfo>(filtered_objects.size());
    }
    public static void showProgressDialog(String title, String message)
    {
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


    public int getCount() {
        return filtered_objects.size();
    }

    public Object getItem(int position) {
        return filtered_objects.get(position);
    }


    public long getItemId(int position) {
        return position;
    }

    public boolean isChecked(int position)
    {
        return checked_items.get(position,false);
    }

    public void setChecked(int position,boolean isChecked)
    {
        checked_items.put(position,isChecked);
    }

    public void toggle(int position)
    {
        setChecked(position,!isChecked(position));
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        //convertView=new PackageView(c);




        viewHolder=new ViewHolder();

        if (convertView==null) {


            convertView = inflater.inflate(R.layout.user_element, null);

            viewHolder.p1 = (ImageView) convertView.findViewById(R.id.photo);

            viewHolder.username = (TextView) convertView.findViewById(R.id.username);

            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.checkbox);

            viewHolder.invite = (Button) convertView.findViewById(R.id.invite);

            viewHolder.get=(Button) convertView.findViewById(R.id.get);



            convertView.setTag(viewHolder);
            convertView.setTag(R.id.photo, viewHolder.p1);
            convertView.setTag(R.id.invite,viewHolder.invite);
            convertView.setTag(R.id.username,viewHolder.username);
            convertView.setTag(R.id.checkbox, viewHolder.cb);



            viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    int getPosition=(Integer)buttonView.getTag();
                    if (isChecked==true)
                    {
                        filtered_objects.get(getPosition).setChecked(true);
                        if (!multi_send.contains(filtered_objects.get(getPosition)))
                            multi_send.add(filtered_objects.get(getPosition));
                    }
                    else
                    {
                        filtered_objects.get(getPosition).setChecked(false);
                        //setChecked(getPosition,false);
                        multi_send.remove(filtered_objects.get(getPosition));
                    }

                }
            });
       }
        else
        {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.cb.setChecked(filtered_objects.get(position).isChecked());



        viewHolder.cb.setTag(position);

        if (filtered_objects.get(position)!=null) {


            if ((filtered_objects.get(position).getPic_path()) != null) {
                viewHolder.p1.setImageURI(Uri.parse(filtered_objects.get(position).getPic_path()));
            } else {
                viewHolder.p1.setImageResource(R.mipmap.ic_launcher);
            }


            //username.setText("" + object.get(position).entrySet().toString());

            viewHolder.username.setText("" + filtered_objects.get(position).getName());


            viewHolder.invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

                    if (activeNetworkInfo != null) {

                        TelephonyManager tm=(TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);

                        new getGeolocationDataAsyncTask(c).execute("" + filtered_objects.get(position).getPhNo(),""+tm.getDeviceId());
                        //Intent i = new Intent(parent.getContext(), ShareLocation.class);
                        Log.d("Phno:", filtered_objects.get(position).getPhNo());
                        //i.putExtra("phNo", object.get(position).getPhNo());
                        //parent.getContext().startActivity(i);
                    } else
                        Toast.makeText(c, "GSM or WIFI conectivity not available", Toast.LENGTH_LONG).show();
                }
            });

            viewHolder.get.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

                    if (activeNetworkInfo != null) {

                        TelephonyManager tm=(TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);


                        //Intent i = new Intent(parent.getContext(), ShareLocation.class);
                        Log.d("Phno:", filtered_objects.get(position).getPhNo());
                        //i.putExtra("phNo", object.get(position).getPhNo());
                        //parent.getContext().startActivity(i);

                        new getLocationFromDatabaseAsyncTask().execute(""+filtered_objects.get(position).getPhNo(),""+tm.getDeviceId());
                    } else
                        Toast.makeText(c, "GSM or WIFI conectivity not available", Toast.LENGTH_LONG).show();

                }
            });
        }
        else
        {
            Toast.makeText(c,"No contacts to display",Toast.LENGTH_LONG).show();
        }
        return convertView;
    }


    public class ViewHolder
    {
        CheckBox cb;
        ImageView p1;
        TextView username;
        Button invite;
        Button get;
    }
    class getLocationFromDatabaseAsyncTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showProgressDialog("Getting last known location from database","Please wait...");


        }

        @Override
        protected String doInBackground(String... params) {

            String BASE_URL = "http://locationfinder.000webhostapp.com/getLocationFromDatabase.php?";
            String PARAM_STRING="to_phNo=" + params[0] +"&from_imei="+params[1];
            String CONNECTION_URL = BASE_URL + PARAM_STRING;


            System.out.println("Share location URL : " + CONNECTION_URL);

            StringBuffer sb = new StringBuffer();

            String response="";
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
                    response = sb.toString();
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

            /*

            JSONObject res=new JSONObject();
            double lat=0.0,lng=0.0;
            try {
                res = new JSONObject(response);
                lat=Double.parseDouble(res.getString("lat"));
                lng=Double.parseDouble(res.getString("lng"));
            }catch (Exception e)
            {
                System.out.println(e.toString());
            }

            String address = "", city = "", country="",state = "", postalCode = "", knownName = "", fullAddress = "";
            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(c, Locale.getDefault());

                addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country=addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();
            } catch (Exception e) {
            }
            if (address == null && city == null && state == null && country==null && postalCode == null)
                fullAddress = knownName;
            else
                fullAddress = "" + address +" " + city +" " + state ;

            System.out.println(fullAddress);

            */

            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            dismissProgressDialog();

            if (s.equals("1\n"))
            {
                Toast.makeText(UserAdapter.this.c,"You have notifications in notification panel...",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(UserAdapter.this.c,"Error in php script", Toast.LENGTH_LONG).show();
            }



            /*
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+s);

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent switch_to_map = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
            switch_to_map.setPackage("com.google.android.apps.maps");

            */

            /*

            PendingIntent mapIntent=PendingIntent.getActivity(UserAdapter.this.c,0,switch_to_map,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager nm=(NotificationManager)UserAdapter.this.c.getSystemService(NOTIFICATION_SERVICE);

            Notification notification = new Notification(R.mipmap.location_share, null,
                    System.currentTimeMillis());

            RemoteViews notificationView = new RemoteViews(UserAdapter.this.c.getPackageName(),
                    R.layout.notification_layout);



            notificationView.setTextViewText(R.id.nmessage,"Last known location :+"+"\n"+s);
            notificationView.setImageViewResource(R.id.nimage,R.mipmap.location_share);






            //the intent that is started when the notification is clicked (works)
            //Intent notificationIntent = new Intent(this, User_Screen.class);
            //PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);

            notification.contentView = notificationView;
            notification.contentIntent = mapIntent;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            */



            //this is the intent that is supposed to be called when the
            //button is clicked
            /*Intent switchIntent = new Intent(this, User_Screen.class);
            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0,
                    switchIntent, 0);

            notificationView.setOnClickPendingIntent(R.id.share,
                    pendingSwitchIntent);

             */

            //nm.notify(1, notification);



        }
    }


    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            String search_string=constraint.toString().toLowerCase();
            if (search_string==null || search_string.length()==0)
            {
                UserAdapter.this.object=object;

                UserAdapter.this.filtered_objects=object;


            }
                ArrayList<UserInfo> temp_objects = new ArrayList<UserInfo>();
                for (int i = 0; i < UserAdapter.this.object.size(); i++) {
                    String contactName=object.get(i).getName().toString().toLowerCase();
                    if ((object.get(i).getName().toString().toLowerCase()).contains(search_string)) {
                        System.out.println(""+object.get(i).getPhNo() +"\t" + contactName);
                        temp_objects.add(object.get(i));
                    }
                }

                results.values = temp_objects;
                results.count = temp_objects.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            UserAdapter.this.filtered_objects = (ArrayList<UserInfo>) (results.values);

            //System.out.println("size of filtered objects :"+ filtered_objects.size());
            notifyDataSetInvalidated();

        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }




    class getGeolocationDataAsyncTask extends AsyncTask<String, Void, String> {

        Context c;

        getGeolocationDataAsyncTask(Context context)
        {
            this.c=context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showProgressDialog("Sharing location","Please wait...");
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... params) {
            TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            int home_mcc, home_mnc, considerIp;

            //carrier Name :
            String carrierName = tm.getNetworkOperatorName();

            boolean gsmTrue=false;

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
                        System.out.println("" + (identityLte.getCi()));
                        System.out.println("" + identityLte.getTac());
                        System.out.println("" + lte.getDbm());
                        System.out.println("" + identityLte.getMcc());
                        System.out.println("" + identityLte.getMnc());

                        // end of debugging


                        cellObj.put("cellId", (identityLte.getCi()));
                        cellObj.put("lac", identityLte.getTac());
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
                String location_latlong="";
            if (gsmTrue==true)
                location_latlong = call.getLatLongString(mcc, mnc, "gsm", carrierName, true, cellList);
            else
                location_latlong=call.getLatLongString(mcc, mnc, "lte", carrierName, false, cellList);
                double lat = 0.0, lng = 0.0, accuracy = 0.0;

                try {

                    JSONObject res = new JSONObject(location_latlong);
                    JSONObject location = res.getJSONObject("location");

                    lat = location.getDouble("lat");
                    lng = location.getDouble("lng");
                    accuracy = res.getDouble("accuracy");
                    System.out.println("lat : "+lat+"\n"+"long : "+lng+"\n"+"accuracy : "+accuracy+" metres");



                } catch (Exception e) {
                    System.out.println("Error in Response JSON Parsing");
                }

                /*ShareLocation sloc = new ShareLocation("Message%20for:",
                        "Latitude:" + lat + "Longitude:" + lng + "%20" + "Accuracy%20:%20" + accuracy + "%20" + "metres", params[0]);

                 */

            String address = "", city = "", country="",state = "", postalCode = "", knownName = "", fullAddress = "";
            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(c, Locale.getDefault());

                addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country=addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();
            } catch (Exception e) {
            }
            if (address == null && city == null && state == null && country==null && postalCode == null)
                fullAddress = knownName;
            else
                fullAddress = "" + address +" " + city +" " + state ;


            ShareLocation sloc = new ShareLocation("Message for:", "Address : "+ fullAddress , params[0],params[1],""+lat,""+lng);

                String response = sloc.send();
                 return response;
            }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            dismissProgressDialog();

            /*
            if (response.equals("0\n")==true)
                Toast.makeText(c.getApplicationContext(), "Recipient device not registered...unable to share location ", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(c.getApplicationContext(),"Location successfully shared with recipient",Toast.LENGTH_LONG).show();
            */

            Toast.makeText(c.getApplicationContext(),"Result:\n"+response,Toast.LENGTH_LONG).show();

        }
    }
}
