package com.example.soumyadeeppal.collegeproject;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class User_Screen extends AppCompatActivity {

    ListView lv;


    UserAdapter adapter;
    Contact_Loader ld;
    Cursor cursor;
    ArrayList<UserInfo> user_objects;
    EditText search_text;

    ArrayList<HashMap<String,String>> contactHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__screen);
        lv = (ListView) findViewById(R.id.lv);

        lv.setTextFilterEnabled(true);

        search_text=(EditText)findViewById(R.id.search_text);




                ld = new Contact_Loader();

        ld.execute();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.multi_send:

        }
        return super.onOptionsItemSelected(item);
    }

    /*
    private void setCallLogs(Cursor managedCursor) {
        contactHolder = new ArrayList<UserInfo>();

        int _number = managedCursor
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int _name = managedCursor
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int _id = managedCursor
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);


        while (managedCursor.moveToNext()) {

            UserInfo holder = new UserInfo();
            holder.setPhNo(managedCursor.getString(_number));
            holder.setName(managedCursor.getString(_name));
            contactHolder.add(holder);
        }
                for(int i=0; i<contactHolder.size(); i++){
                    try{
                        ContactsDb merger = new ContactsDb(User_Screen.this);
                        merger.open();
                        merger.insertContacts(contactHolder.get(i).getName(),
                                contactHolder.get(i).getPhNo());
                        merger.close();

                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }

            }

            */


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


            /*

            setCallLogs(c);

            ArrayList<UserInfo> user_objects=new ArrayList<UserInfo>();
            ContactsDb cdb=new ContactsDb(User_Screen.this);
            cdb.open();
            try
            {
                user_objects=cdb.getContactDetails();

            }catch (Exception e)
            {
                System.out.println(e.toString());
            }

            cdb.close();



            return user_objects;


        }
        */

        }

        @Override
        protected void onPostExecute(ArrayList<UserInfo> user_objects) {
            super.onPostExecute(user_objects);


            if (user_objects != null) {
                adapter = new UserAdapter(getBaseContext(), user_objects);
                lv.setAdapter(adapter);
                lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SparseBooleanArray sp=lv.getCheckedItemPositions();
                        System.out.println("No of items selected :"+sp.size());
                    }
                });
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
            user_adapter.getFilter().filter(s.toString().toLowerCase());



        }

        @Override
        public void afterTextChanged(Editable s) {
           // user_adapter.getFilter().filter(s.toString());


        }
    }
}