package com.example.alper_arik.smart_phonebook;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {

 @Override
  public void onReceive(Context context, Intent intent) {
       if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Log.i("SMS_LOG","sms received");
       }
  }

    public static void getSMSDetails(Context context){
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        ArrayList<Person> persons = db.readAll();
        for(Person p : persons){
            FileOperation.clearFile(p.get_mobilePhoneNumber()+"_SMS", context);
        }
        db.close();

        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = context.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(c.getString(c.getColumnIndexOrThrow("address"))); //Gönderen kişi
                //sb.append(";"+c.getString(c.getColumnIndex("read")));
                String simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(Long.parseLong(c.getString(c.getColumnIndexOrThrow("date")))));
                sb.append(";"+simpleDateFormat);
                sb.append(";"+c.getString(c.getColumnIndexOrThrow("body")));   //mesaj

                String info = sb.toString();
                String [] parsedInfo = info.split(";");

                if(parsedInfo[0].charAt(0) == '0'){
                    String fix = "+9"+parsedInfo[0];
                    parsedInfo[0] = fix;
                }
                //Telefon numarı olmayanlar yakalamaz (reklam vs.)
                if(parsedInfo[0].charAt(0) == '+'){
                    //incomig SMS
                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                        FileOperation.appendToFile(parsedInfo[0]+"_SMS","incoming;"+parsedInfo[1]+";"+parsedInfo[2],context);
                    }
                    //outgoing sms
                    else {
                        FileOperation.appendToFile(parsedInfo[0] + "_SMS", "outgoing;" + parsedInfo[1]+";"+parsedInfo[2], context);
                    }
                }

                c.moveToNext();
            }
        }

        c.close();

    }
}
