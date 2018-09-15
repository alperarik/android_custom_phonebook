package com.example.alper_arik.smart_phonebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendSMSActivity extends AppCompatActivity {

    private TextView smsReceiverTextView;
    private EditText typeSMSEditText;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        smsReceiverTextView = (TextView) findViewById(R.id.smsReceiverTextView);
        typeSMSEditText = (EditText) findViewById(R.id.typeSMSEditText);
        sendButton = (Button) findViewById(R.id.sendButton);

        Intent i = getIntent();
        final String phoneNumber = i.getStringExtra("PHONE_NUMBER");
        Log.i("SEND_SMS", phoneNumber);

        final String name = i.getStringExtra("PERSON_NAME");
        Log.i("SEND_SMS", name);

        smsReceiverTextView.setText(name.toUpperCase());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SmsManager sms = SmsManager.getDefault();
                String message = typeSMSEditText.getText().toString();
                sms.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(SendSMSActivity.this,"Message has sent",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SendSMSActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}
