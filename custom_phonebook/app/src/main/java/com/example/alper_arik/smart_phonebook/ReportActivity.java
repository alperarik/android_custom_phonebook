package com.example.alper_arik.smart_phonebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    private TextView reportTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        reportTextView = (TextView) findViewById(R.id.reportTextView);
        String tmp = calcTotalDuration(this);
        reportTextView.setText(tmp);
    }

    private String calcTotalDuration(Context context){

        DatabaseHelper db = DatabaseHelper.getInstance(context);
        ArrayList<Person> al = db.readAll();
        int totalIncomingDuration = 0;
        int totalOutgoingDuration = 0;

        int missed = 0;
        int prevMissed = 0;
        Person maxMissed = null;

        int incomingDuration = 0;
        int prevIncomingDuration = 0;
        Person maxIncomingDuration = null;

        int outgoingDuration = 0;
        int prevOutgoingDuration = 0;
        Person maxOutgoingDuration = null;

        int totalCall = 0;
        int prevTotalCall = 0;
        Person maxTotalCall = null;

        for(Person p :al){
            SharedPreferences sp = context.getSharedPreferences("Call_info", MODE_PRIVATE);
            //duration call_type date
            String info = sp.getString(p.get_mobilePhoneNumber(), "0 none 0");
            if (!info.equalsIgnoreCase("0 none 0")) {
                FileOperation.appendToFile(p.get_mobilePhoneNumber(), info, context);
                //sp sıfırla
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(p.get_mobilePhoneNumber(),"0 none 0");
                editor.commit();
            }

            String infos = FileOperation.readFromFile(p.get_mobilePhoneNumber(),context);
            if (infos.equalsIgnoreCase("")) continue;
            String [] parsedInfos = infos.split("#");

            for(String s : parsedInfos){
                String [] tmp = s.split(";");
                if (tmp[1].equalsIgnoreCase("incoming")) {
                    totalIncomingDuration += Integer.parseInt(tmp[0]);
                    incomingDuration += Integer.parseInt(tmp[0]);
                    totalCall += Integer.parseInt(tmp[0]);
                }
                else if (tmp[1].equalsIgnoreCase("outgoing")) {
                    totalOutgoingDuration += Integer.parseInt(tmp[0]);
                    outgoingDuration += Integer.parseInt(tmp[0]);
                    totalCall += Integer.parseInt(tmp[0]);
                }
                else{
                    missed++;
                }
            }

            if(missed > prevMissed){
                maxMissed = p;
                prevMissed = missed;
                missed = 0;
            }

            if(incomingDuration > prevIncomingDuration){
                maxIncomingDuration = p;
                prevIncomingDuration = incomingDuration;
                incomingDuration = 0;
            }

            if(outgoingDuration > prevOutgoingDuration){
                maxOutgoingDuration = p;
                prevOutgoingDuration = outgoingDuration;
                outgoingDuration = 0;
            }

            if(totalCall > prevTotalCall){
                maxTotalCall = p;
                prevTotalCall = totalCall;
                totalCall = 0;
            }
        }

        return "Total Incoming Duration : "+convertTime(totalIncomingDuration)+"\nTotal Outgoing Duration : "+convertTime(totalOutgoingDuration)+
                "\n\nPerson with max missed call\n"+maxMissed.get_name()+", "+prevMissed+
                "\n\nPerson with max incoming duration\n"+maxIncomingDuration.get_name()+" ,   "+convertTime(prevIncomingDuration)+
                "\n\nPerson with max outgoing duration\n"+maxOutgoingDuration.get_name()+" ,   "+convertTime(prevOutgoingDuration)+
                "\n\nPerson with max total duration\n"+maxTotalCall.get_name()+" ,   "+convertTime(prevTotalCall);
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
}
