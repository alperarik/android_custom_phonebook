package com.example.alper_arik.smart_phonebook;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public  class CallReceiver extends BroadcastReceiver {


    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;
    private long callDuration;
    private StringBuilder callType = new StringBuilder();

    SharedPreferences sp ;
    SharedPreferences.Editor edit ;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            onCallStateChanged(context, state, number);
        }
    }

    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    onMissedCall(context, savedNumber, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }

    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Log.i("CALL","incoming starded  "+number);
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.i("CALL","outgoing starded  "+number);
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.i("CALL","incoming ended  "+number);
        callDuration = end.getTime() - start.getTime();
        callType.setLength(0);
        callType.append("incoming");
        saveInfo(ctx);
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.i("CALL","outgoing ended  "+number);
        callDuration = end.getTime() - start.getTime();
        callType.setLength(0);
        callType.append("outgoing");
        saveInfo(ctx);
    }

    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.i("CALL","missisng call  "+number);
        callType.setLength(0);
        callType.append("missed");
        saveInfo(ctx);
    }

    public void saveInfo(Context context){
        sp = context.getSharedPreferences("Call_info", Context.MODE_PRIVATE);
        edit = sp.edit();
        if(savedNumber != null){
            Date date = new Date();
            String simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(date);

            String info = String.valueOf(callDuration / 1000)+";"+callType.toString()+";"+simpleDateFormat;
            Log.i("CALL","call info : "+info);
            if(savedNumber.charAt(0) != '+'){
                String fix = "+9"+savedNumber;
                savedNumber = fix;
            }
            edit.putString(savedNumber,info);
            edit.commit();
        }
    }
}