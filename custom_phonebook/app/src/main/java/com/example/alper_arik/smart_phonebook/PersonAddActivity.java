package com.example.alper_arik.smart_phonebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

public class PersonAddActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText homePhoneEditText;
    private EditText mobilePhoneEditText;
    private EditText workPhoneEditText;
    private EditText emailEditText;
    private Button addLocationButton;
    private Button addPersonButton;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_add);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        surnameEditText = (EditText) findViewById(R.id.surnameEditText);
        homePhoneEditText = (EditText) findViewById(R.id.homePhoneEditText);
        mobilePhoneEditText = (EditText) findViewById(R.id.mobilePhoneEditText);
        workPhoneEditText = (EditText) findViewById(R.id.workPhoneEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        addPersonButton = (Button) findViewById(R.id.addPersonButton);
        addLocationButton = (Button) findViewById(R.id.addLocationButton);

        nameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEditText.setText("");
            }
        });
        surnameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surnameEditText.setText("");
            }
        });
        homePhoneEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePhoneEditText.setText("");
            }
        });
        mobilePhoneEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobilePhoneEditText.setText("");
            }
        });
        workPhoneEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workPhoneEditText.setText("");
            }
        });
        emailEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailEditText.setText("");
            }
        });

        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEditText.getText().toString().equals("") || surnameEditText.getText().toString().equals("") || mobilePhoneEditText.getText().toString().equals("")){
                    Toast.makeText(PersonAddActivity.this,"Name surname and mobile phone informations must be entered!",Toast.LENGTH_SHORT).show();
                    return;
                }

                Person p = new Person();
                p.set_name(nameEditText.getText().toString().toLowerCase() + " " +
                           surnameEditText.getText().toString().toLowerCase());

                //
                if(homePhoneEditText.getText().toString().equals(""))
                    p.set_homePhoneNumber(null);
                else
                    p.set_homePhoneNumber(homePhoneEditText.getText().toString().toLowerCase());

                //
                if(mobilePhoneEditText.getText().toString().equals(""))
                    p.set_mobilePhoneNumber(null);
                else{
                    String mNumber = mobilePhoneEditText.getText().toString().toLowerCase();
                    if(mNumber.charAt(0) != '+'){
                        String fix = "+9"+ mNumber;
                        mNumber = fix;
                    }
                    p.set_mobilePhoneNumber(mNumber);
                }

                //
                if(workPhoneEditText.getText().toString().equals(""))
                    p.set_workPhoneNumber(null);
                else
                    p.set_workPhoneNumber(workPhoneEditText.getText().toString().toLowerCase());

                //
                if(emailEditText.getText().toString().equals(""))
                    p.set_eMail(null);
                else
                    p.set_eMail(emailEditText.getText().toString().toLowerCase());

                String id = UUID.nameUUIDFromBytes(p.get_name().getBytes()).toString();
                p.set_id(id);

                DatabaseHelper db = DatabaseHelper.getInstance(PersonAddActivity.this);
                if(db.getPerson(p.get_id()) == null){
                    db.addPerson(p);
                    Log.i("TAG", p.get_name() + " " + p.get_homePhoneNumber() + " " +
                            p.get_mobilePhoneNumber() + " " + p.get_workPhoneNumber() + " " + p.get_eMail());

                    Toast.makeText(PersonAddActivity.this,"Contact saved successfully",Toast.LENGTH_SHORT).show();

                    Intent i =  new Intent(PersonAddActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(PersonAddActivity.this,"This contact is already exist!",Toast.LENGTH_SHORT).show();
                }

                db.close();

                FileOperation.appendToFile(p.get_mobilePhoneNumber()+"_LOCATION",String.valueOf(latitude)+";"+String.valueOf(longitude),getBaseContext());
            }
        });

        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSService gps = new GPSService(getBaseContext());

                if(gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    Toast.makeText(getBaseContext(),"Your Location is -\nLat: " + latitude + "\nLong: "+ longitude, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
