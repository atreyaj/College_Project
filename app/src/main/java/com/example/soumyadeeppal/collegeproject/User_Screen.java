package com.example.soumyadeeppal.collegeproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class User_Screen extends AppCompatActivity {

    ListView lv;

    Context c;


    UserAdapter adapter;
    Contact_Loader ld;
    Cursor cursor;
    ArrayList<UserInfo> user_objects;
    EditText search_text;
    private ProgressDialog progressDialog = null;

    ArrayList<HashMap<String,String>> contactHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__screen);

        View v=findViewById(R.id.lv);
        v.getRootView().setBackgroundColor(Color.WHITE);

        lv = (ListView) findViewById(R.id.lv);
        lv.setTextFilterEnabled(true);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        search_text=(EditText)findViewById(R.id.search_text);
        ld = new Contact_Loader();
        ld.execute();



        getSupportActionBar().setDisplayShowTitleEnabled(true);

        c=getBaseContext();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {

        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    Log.e(TAG, "onMenuOpened", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {




        switch (item.getItemId())
        {
            case R.id.multi_send:

                int count=0;

                UserAdapter ad=(UserAdapter)lv.getAdapter();


                try {

                    //send_to_nos = new JSONArray(ad.multi_send.size());

                    System.out.println("**********Selected recipients*********");

                    progressDialog = new ProgressDialog(User_Screen.this);

                    new SendToMultiple(getBaseContext()).execute(ad.multi_send);
                    /*for (int i=0;i<ad.multi_send.size();i++)
                    {
                        UserInfo ob=ad.multi_send.get(i);

                        System.out.println(ob.getName()+"\t"+ob.getPhNo());



                    }

                    */

                }catch (Exception e)
                {
                    System.out.println(e.toString());
                }




                break;

            case R.id.know_my_location: ConnectivityManager connMgr = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

                if (activeNetworkInfo != null) { // connected to the internet
                    Toast.makeText(User_Screen.this, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

                    progressDialog = new ProgressDialog(User_Screen.this);

                    KnowMyLocationAsyncTask myloc = new KnowMyLocationAsyncTask(getBaseContext());
                    myloc.execute();
                }
                else
                {
                    Toast.makeText(User_Screen.this,"Internet Connectivity not available",Toast.LENGTH_LONG).show();
                }
                break;
            default: System.out.println("invalid item selection");
                break;

        }        return super.onOptionsItemSelected(item);
    }



    public void showProgressDialog(String title, String message)
    {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog()
    {
        progressDialog.dismiss();
    }
    public void clearDialog()
    {
        progressDialog = null;
    }

    class KnowMyLocationAsyncTask extends AsyncTask<Void,String,String>
    {
        Context c;

        KnowMyLocationAsyncTask(Context c)
        {
            this.c=c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showProgressDialog("Getting your location","Fetching results and reverse geocoding..");

        }

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
            //CellLocation info = tm.getCellLocation();

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

            if (gsmTrue == true)
                location_latlong = call.getLatLongString(mcc, mnc, "gsm", carrierName, true, cellList);
            else
                location_latlong = call.getLatLongString(mcc, mnc, "lte", carrierName, true, cellList);
            return location_latlong;
        }

        @Override
        protected void onPostExecute(String s) {

            dismissProgressDialog();
            double lat = 0.0, lng = 0.0 , accuracy=0.0;

            try {

                JSONObject res = new JSONObject(s);
                JSONObject location = res.getJSONObject("location");

                lat = location.getDouble("lat");
                lng = location.getDouble("lng");
                accuracy=res.getDouble("accuracy");

            } catch (Exception e) {
                System.out.println("Error in Response JSON Parsing");
            }


            //regToken.setText("Latitude :"+lat+"\n"+"Longitude :"+lng);
            //Toast.makeText(MainActivity.this,"Latitude :"+lat+"\n"+"Longitude :"+lng,Toast.LENGTH_LONG).show();

            String address = "", city = "", country="",state = "", postalCode = "", knownName = "", fullAddress = "";
            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(User_Screen.this, Locale.getDefault());

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
                fullAddress = "" + address +" "+ city +" "+ state +" "+ country +" "+ postalCode;

            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+fullAddress);

            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent switch_to_map = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
            switch_to_map.setPackage("com.google.android.apps.maps");
            switch_to_map.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(switch_to_map);
            //Toast.makeText(MainActivity.this, "Latitude :" + lat + "\n" + "Longitude :" + lng+"\n"+"Address : "+fullAddress, Toast.LENGTH_LONG).show();
        }

    }

    public class Contact_Loader extends AsyncTask<Void, Void, ArrayList<UserInfo>> {

        @Override
        protected ArrayList<UserInfo> doInBackground(Void... params) {
            /*int i = 0;
            while (c.moveToNext()) {

                String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                UserInfo obj = new UserInfo();

                obj.setName(name);
                obj.setPhNo(number);
                object.add(obj);
                //object[i] = obj;
                Log.d("name : ", object.get(i).getName()+" : "+object.get(i).getPhNo());
                i++;
            }
            return object;
            */

            // -------------------------Code using set and arraylist user_objects-------------------------------------------------

            user_objects = new ArrayList<UserInfo>();
            ContentResolver resolver = getContentResolver();
            cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            try {

                cursor = getApplicationContext().getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                int Idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
                cursor.moveToFirst();
                Set<String> ids = new HashSet<>();


                do {
                    System.out.println("=====>in while");
                    String contactid = cursor.getString(Idx);
                    if (!ids.contains(contactid)) {
                        ids.add(contactid);
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        UserInfo ui = new UserInfo();
                        String name = cursor.getString(nameIdx);
                        String phoneNumber = cursor.getString(phoneNumberIdx);
                        String image = cursor.getString(photoIdIdx);
                        //System.out.println("Id--->" + contactid + "Name--->" + name);
                        //System.out.println("Id--->" + contactid + "Number--->" + phoneNumber);
                            //hashMap.put("contactid", "" + contactid);




                        if (phoneNumber.length()>=10) {
                            ui.setPhNo(phoneNumber);
                            ui.setName(name);
                            if (image!=null)
                                ui.setPic_path(image);
                            if (ui!=null) {
                                user_objects.add(ui);
                            }
                        }
                        //ui.setPhNo(phoneNumber);

                        System.out.println(ui.getName()+ "\t" + ui.getPhNo() +"\t" + ui.getPic_path());
                            //hashMap.put("image", "" + image);
                            // hashMap.put("email", ""+email);
                    }
                } while (cursor.moveToNext());


                /*
                cursor.moveToFirst();
                Set<String> ids = new HashSet<>();


                contactHolder = new ArrayList<HashMap<String, String>>();
                do {
                    System.out.println("=====>in while");
                    String contactid = cursor.getString(Idx);
                    if (!ids.contains(contactid)) {
                        ids.add(contactid);
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        String name = cursor.getString(nameIdx);
                        String phoneNumber = cursor.getString(phoneNumberIdx);
                        String image = cursor.getString(photoIdIdx);
                        System.out.println("Id--->" + contactid + "Name--->" + name);
                        System.out.println("Id--->" + contactid + "Number--->" + phoneNumber);

                        if (!phoneNumber.contains("*")) {
                            //hashMap.put("contactid", "" + contactid);
                            hashMap.put("name", "" + name);
                            hashMap.put("phoneNumber", "" + phoneNumber);
                            //hashMap.put("image", "" + image);
                            // hashMap.put("email", ""+email);
                            if (contactHolder != null) {
                                contactHolder.add(hashMap);
                            }
//                    hashMapsArrayList.add(hashMap);
                        }
                    }

                } while (cursor.moveToNext());

                */

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }

                return user_objects;
                //return contactHolder;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<UserInfo> user_objects) {
            super.onPostExecute(user_objects);


            if (user_objects != null) {
                adapter = new UserAdapter(User_Screen.this, user_objects);
                lv.setAdapter(adapter);
                lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

                /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SparseBooleanArray sp=lv.getCheckedItemPositions();
                        System.out.println("No of items selected :"+sp.size());
                    }
                });*/


                search_text.addTextChangedListener(new MyTextWatcher(adapter));

            } /*else {
                lv.invalidate();
            }*/
        }
    }



    public class MyTextWatcher implements TextWatcher
    {
        private UserAdapter user_adapter;

        MyTextWatcher(UserAdapter adapter)
        {
            this.user_adapter=adapter;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {


        }

        @Override
        public void afterTextChanged(Editable s) {
           // user_adapter.getFilter().filter(s.toString());

            user_adapter.getFilter().filter(s.toString().toLowerCase());

        }
    }

    public class SendToMultiple extends AsyncTask<ArrayList<UserInfo>,Void,String>
    {
        Context c;
        SendToMultiple(Context context)
        {
            this.c=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showProgressDialog("Sending location","Please wait...");
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(ArrayList<UserInfo>... params) {
            TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);


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
            //CellLocation info = tm.getCellLocation();

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

            if (gsmTrue == true)
                location_latlong = call.getLatLongString(mcc, mnc, "gsm", carrierName, true, cellList);
            else
                location_latlong = call.getLatLongString(mcc, mnc, "lte", carrierName, false, cellList);
            double lat = 0.0, lng = 0.0 , accuracy=0.0;

            try {

                JSONObject res = new JSONObject(location_latlong);
                JSONObject location = res.getJSONObject("location");

                lat = location.getDouble("lat");
                lng = location.getDouble("lng");
                accuracy=res.getDouble("accuracy");

            } catch (Exception e) {
                System.out.println("Error in Response JSON Parsing");
            }


            //regToken.setText("Latitude :"+lat+"\n"+"Longitude :"+lng);
            //Toast.makeText(MainActivity.this,"Latitude :"+lat+"\n"+"Longitude :"+lng,Toast.LENGTH_LONG).show();


            String address = "", city = "", country="",state = "", postalCode = "", knownName = "", fullAddress = "";


            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(User_Screen.this, Locale.getDefault());

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
            if (address == null && city == null && state == null && /*country==null*/ postalCode == null)
                fullAddress = knownName;
            else
                fullAddress = "" + address +" " +city +" " +state +" "+ country;





            /*----------------Sending location to multiple recipients-----------------------*/

            String response="";
            int sendcount=0;
            for (int i=0;i<params[0].size();i++) {
                ShareLocation sloc = new ShareLocation("Message for:", "Address : "+fullAddress, params[0].get(i).phNo,
                        tm.getDeviceId(),""+lat,""+lng);
                response = sloc.send();
                /*if (response.equals("0\n"))
                    Toast.makeText(User_Screen.this, "Recipient "+params[0].get(i).getName()+" is not registered", Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(User_Screen.this,"Succesfully shared location with "+params[0].get(i).getName(),Toast.LENGTH_LONG).show();

                    */
                if (response.equals("0\n")==false)
                    sendcount++;
                }
            return String.valueOf(sendcount);

        }

        @Override
        protected void onPostExecute(String sendcount) {
            super.onPostExecute(sendcount);
            dismissProgressDialog();

            Toast.makeText(User_Screen.this,"Message successfully sent to "+sendcount+" selected recipients",Toast.LENGTH_LONG).show();
        }
    }
}