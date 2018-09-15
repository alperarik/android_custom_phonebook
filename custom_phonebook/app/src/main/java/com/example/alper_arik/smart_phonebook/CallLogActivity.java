package com.example.alper_arik.smart_phonebook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Collections;

public class CallLogActivity extends AppCompatActivity {

    private TextView callInfoTextView;
    private ListView callLogListView;
    private Person p = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        callInfoTextView = (TextView) findViewById(R.id.callInfoTextView);
        callLogListView = (ListView)findViewById(R.id.callLogListView);

        Intent i = getIntent();
        Person person = (Person) i.getSerializableExtra("PERSON");
        if(person != null){
            p = person;
            initCallInfoTextField(CallLogActivity.this);
        }
    }


    private void initCallInfoTextField(Context context) {
        int totalIncomingDuration = 0;
        int totalOutgoingDuration = 0;

        int missingCall = 0;
        int incomingCall = 0;
        int outgoingCall = 0;


        ArrayList<String> al = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences("Call_info", MODE_PRIVATE);
        //duration call_type date
        String info = sp.getString(p.get_mobilePhoneNumber(), "0 none 0");
        if (!info.equalsIgnoreCase("0 none 0")) {
            FileOperation.appendToFile(p.get_mobilePhoneNumber(), info, context);
            Log.i("SP_CALL_LOG", info);
            //sp sıfırla
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(p.get_mobilePhoneNumber(),"0 none 0");
            editor.commit();
        }
        String data = FileOperation.readFromFile(p.get_mobilePhoneNumber(), context);
        Log.i("FILE_CALL_LOG",data);
        if (data.equalsIgnoreCase("")) return;
        String[] parsedInfo = data.split("#");

        for (int i = 0; i < parsedInfo.length; i++) {
            String[] tmp = parsedInfo[i].split(";");
            String log = "";

            if (tmp[1].equalsIgnoreCase("missed")) {
                missingCall++;
                log += "missed\n" + tmp[2];
            } else if (tmp[1].equalsIgnoreCase("incoming")) {
                totalIncomingDuration += Integer.parseInt(tmp[0]);
                incomingCall++;

                log += "incoming\n" + tmp[2]+"\nDuration : " +convertTime(tmp[0]);
            } else if (tmp[1].equalsIgnoreCase("outgoing")) {
                totalOutgoingDuration += Integer.parseInt(tmp[0]);
                outgoingCall++;
                log += "outgoing\n" + tmp[2]+"\nDuration : " +convertTime(tmp[0]);
            }
            al.add(log);
        }

        String callInfo = "\nTotal incoming duration : " + convertTime(totalIncomingDuration) + "\nTotal outgoing duration : " + convertTime(totalOutgoingDuration) + "\nMissing : " + String.valueOf(missingCall) + "  Incoming : " + String.valueOf(incomingCall) + "  Outgoing : " + String.valueOf(outgoingCall);
        callInfoTextView.setText(callInfo);

        Collections.reverse(al);
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
        callLogListView.setAdapter(arrayAdapter);

    }

    private String convertTime(int time){
        int hours;
        int minutes;
        int seconds;

        hours = time / 3600;
        minutes = (time % 3600) / 60;
        seconds = time % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    private String convertTime(String sTime){
        int hours;
        int minutes;
        int seconds;

        int time = Integer.parseInt(sTime);
        hours = time / 3600;
        minutes = (time % 3600) / 60;
        seconds = time % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
