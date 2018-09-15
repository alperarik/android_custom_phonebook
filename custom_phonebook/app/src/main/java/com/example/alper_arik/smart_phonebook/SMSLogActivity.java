package com.example.alper_arik.smart_phonebook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SMSLogActivity extends AppCompatActivity {

    private TextView smsLogTextView;
    private ListView smsLogListView;
    private Person p = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smslog);
        smsLogTextView = (TextView) findViewById(R.id.smsLogTextView);
        smsLogListView = (ListView) findViewById(R.id.smsLogListView);

        Intent i = getIntent();
        Person person = (Person) i.getSerializableExtra("PERSON");
        if(person != null){
            p = person;
            initSMSInfoTextField(SMSLogActivity.this);
            initSMSLogListView(SMSLogActivity.this);
        }
    }

    private void initSMSInfoTextField(Context context){
        int incomingSMS = 0;
        int outgoingSMS = 0;

        SmsReceiver.getSMSDetails(context);

        String smsInfo = FileOperation.readFromFile(p.get_mobilePhoneNumber()+"_SMS",context);
        if(smsInfo.equals("")){ smsLogTextView.setText("Incoming sms : 0  Outgoing sms : 0"); return;}

        String [] parsedSmsInfo = smsInfo.split("#");

        for(int i = 0; i < parsedSmsInfo.length; i++){
            String [] tmp = parsedSmsInfo[i].split(";");

            if(tmp[0].equalsIgnoreCase("incoming")){
                incomingSMS++;
            }
            else if(tmp[0].equalsIgnoreCase("outgoing")){
                outgoingSMS++;
            }
        }

        String sms = "Incoming sms : "+String.valueOf(incomingSMS)+"  Outgoing sms : "+String.valueOf(outgoingSMS);
        Log.i("TAG1", sms);
        smsLogTextView.setText(sms);
    }

    private void initSMSLogListView(Context context){

        ArrayList<String> al = new ArrayList<>();
        String mPhone = p.get_mobilePhoneNumber();

        String info = FileOperation.readFromFile(mPhone + "_SMS", context);

        String [] parsedInfo = info.split("#");

        for(int i = 0; i < parsedInfo.length; i++){

            String [] tmp = parsedInfo[i].split(";");
            String log = "";

            if(tmp[0].equalsIgnoreCase("incoming")){
                if(tmp.length > 2)
                    log +="incoming  "+tmp[1]+"   "+tmp[2];
                else
                    log +="incoming  "+tmp[1];
            }
            else if (tmp[0].equalsIgnoreCase("outgoing")){
                if(tmp.length > 2)
                    log +="outgoing  "+tmp[1]+"   "+tmp[2];
                else
                    log +="outgoing  "+tmp[1];
            }

            al.add(log);
        }

        String[] arrayAL = al.toArray(new String[al.size()]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, arrayAL){
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(Color.parseColor("#94ffe2"));
                textView.setTextSize(13);

                return view;
            }
        };

        smsLogListView.setAdapter(arrayAdapter);
    }
}
