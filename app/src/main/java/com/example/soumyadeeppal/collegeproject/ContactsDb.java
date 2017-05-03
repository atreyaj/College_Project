package com.example.soumyadeeppal.collegeproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Soumyadeep Pal on 14-03-2017.
 */

public class ContactsDb {
    private static final String CONTACT_ID = "_contact_id";
    private static final String CONTACT_NAME = "_contact_name";
    private static final String CONTACT_NUMBER = "_contact_number";


    private static final String DATABASE_NAME = "ContactDb";
    private static final String TABLE_NAME = "_contacts_table";
    private static final int DATABASE_VERSION = 7;

    private final Context context;

    private SQLiteDatabase db;

    private DbHelper helper;

    private class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String create_query = "CREATE TABLE " + TABLE_NAME + " (" +
                    CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + CONTACT_NAME + " TEXT NOT NULL , " + CONTACT_NUMBER
                    + " TEXT NOT NULL);";

            db.execSQL(create_query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public ContactsDb(Context context) {
        this.context = context;
    }

    public ContactsDb open() throws SQLException
    {
        helper=new DbHelper(context);
        db=helper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        helper.close();
    }

    public long insertContacts(String name,String number) throws SQLException {
        ContentValues cv = new ContentValues();
        cv.put(CONTACT_NAME, name);
        cv.put(CONTACT_NUMBER, number);
        System.out.println("" + cv.toString());
        return db.insert(TABLE_NAME, null, cv);

    }

    public ArrayList<UserInfo> getContactDetails() throws Exception
    {
        ArrayList<UserInfo> userinfo=new ArrayList<UserInfo>();
        String[] columns=new String[]{CONTACT_ID,CONTACT_NAME,CONTACT_NUMBER};
        Cursor c= db.query(true,TABLE_NAME,columns,null,null,null,null,null,null);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            UserInfo data = new UserInfo();
            data.setName(c.getString(c.getColumnIndex(CONTACT_NAME)));
            data.setPhNo(c.getString(c.getColumnIndex(CONTACT_NUMBER)));

            userinfo.add(data);
        }
        return userinfo;
    }
}
