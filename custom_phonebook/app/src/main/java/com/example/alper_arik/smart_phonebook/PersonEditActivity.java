package com.example.alper_arik.smart_phonebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

public class PersonEditActivity extends AppCompatActivity {

    private EditText nameEditField;
    private EditText homePhoneEditField;
    private EditText mobilePhoneEditField;
    private EditText workPhoneEditField;
    private EditText emailEditField;
    private Button editOKButton;
    private Button editLocationButton;
    private Person p = null;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);

        p = (Person) getIntent().getSerializableExtra("Person");

        nameEditField = (EditText) findViewById(R.id.nameEditField);
        homePhoneEditField = (EditText) findViewById(R.id.homePhoneEditField);
        mobilePhoneEditField = (EditText) findViewById(R.id.mobilePhoneEditField);
        workPhoneEditField = (EditText) findViewById(R.id.workPhoneEditField);
        emailEditField = (EditText) findViewById(R.id.emailEditField);
        editOKButton = (Button) findViewById(R.id.editOKButton);
        editLocationButton = (Button) findViewById(R.id.editLocationButton);

        initFields();

        nameEditField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEditField.setText("");
            }
        });

        homePhoneEditField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePhoneEditField.setText("");
            }
        });

        mobilePhoneEditField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobilePhoneEditField.setText("");
            }
        });

        workPhoneEditField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workPhoneEditField.setText("");
            }
        });

        emailEditField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailEditField.setText("");
            }
        });

        editOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOKButtonOnClick();
            }
        });

        editLocationButton.setOnClickListener(new View.OnClickListener() {
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

    private  void initFields(){
        if(p == null) return;

        nameEditField.setText(p.get_name());
        homePhoneEditField.setText(p.get_homePhoneNumber());
        mobilePhoneEditField.setText(p.get_mobilePhoneNumber());
        workPhoneEditField.setText(p.get_workPhoneNumber());
        emailEditField.setText(p.get_eMail());
    }

    private void editOKButtonOnClick(){

        DatabaseHelper db = DatabaseHelper.getInstance(PersonEditActivity.this);
        db.deletePerson(p.get_id());

        if(nameEditField.getText().toString().equals("") || mobilePhoneEditField.getText().toString().equals("")){
            Toast.makeText(PersonEditActivity.this, "Name and mobile phone informations must be entered!", Toast.LENGTH_SHORT).show();
            return;
        }

        Person p = new Person();

        p.set_name(nameEditField.getText().toString());
        String id = UUID.nameUUIDFromBytes(p.get_name().getBytes()).toString();
        p.set_id(id);
        p.set_homePhoneNumber(homePhoneEditField.getText().toString());
        p.set_mobilePhoneNumber(mobilePhoneEditField.getText().toString());
        p.set_workPhoneNumber(workPhoneEditField.getText().toString());
        p.set_eMail(emailEditField.getText().toString());

        db.addPerson(p);
        db.close();

        FileOperation.clearFile(p.get_mobilePhoneNumber()+"_LOCATION",getBaseContext());
        FileOperation.appendToFile(p.get_mobilePhoneNumber() + "_LOCATION", String.valueOf(latitude) + ";" + String.valueOf(longitude), getBaseContext());

        Toast.makeText(PersonEditActivity.this,"Contact has edited successfully!",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(PersonEditActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
