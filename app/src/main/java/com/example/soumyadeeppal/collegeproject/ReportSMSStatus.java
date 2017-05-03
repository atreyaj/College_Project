package com.example.soumyadeeppal.collegeproject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;

public class ReportSMSStatus extends AppCompatActivity {

    TextView sendstatus;
    String phNo;
    String fullAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_smsstatus);

        sendstatus=(TextView)findViewById(R.id.smsstatus);

        phNo=getIntent().getStringExtra("phNo");
        fullAddress=getIntent().getStringExtra("fullAddress");



        sendSMS(phNo,"Current Location :\n"+fullAddress);
    }


    public void sendSMS(String mobile,String subject)
    {
        String sendSms="SEND_SMS";
        String deliverSms="DEL_SMS";    // strings created to be passed in 2 intents . intents created to be passed in pending intent
        PendingIntent sSms=PendingIntent.getBroadcast(getBaseContext(),0,new Intent(sendSms),0);
        PendingIntent sDel=PendingIntent.getBroadcast(getBaseContext(),0,new Intent(deliverSms),0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // registering receiver for sending SMS using Anonymous receiver object

                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS Sent", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // use SmsManager of telephony not cdma
                        Toast.makeText(getBaseContext(),"NO SERVICE",Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(),"GENERIC FAILURE",Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(),"NULL PDU",Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(),"NO RADIO",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        },new IntentFilter(sendSms));


        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // registering receiver for delivering SMS

                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(),"MESSAGE DELIVERED",Toast.LENGTH_LONG).show();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(getBaseContext(),"MESSAGE NOT DELIVERED",Toast.LENGTH_LONG).show();
                        break;
                }

            }
        },new IntentFilter(deliverSms));


        SmsManager manager=SmsManager.getDefault();
        manager.sendTextMessage(mobile,null,subject,sSms,sDel);

    }

}
