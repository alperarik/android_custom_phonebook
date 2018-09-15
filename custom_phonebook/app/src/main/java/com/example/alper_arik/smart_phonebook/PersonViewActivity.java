package com.example.alper_arik.smart_phonebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class PersonViewActivity extends AppCompatActivity {

    private TextView nameField;
    private TextView homePhoneField;
    private TextView mobilePhoneField;
    private TextView workPhoneField;
    private TextView emailField;
    private Button editPersonButton;
    private Button deletePersonButton;
    private Button callLogButton;
    private Button smsLogButton;
    private Person p = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_view);

        Intent i = getIntent();
        String id = i.getStringExtra("ID");
        final DatabaseHelper db = DatabaseHelper.getInstance(PersonViewActivity.this);
        p = db.getPerson(id);

        nameField = (TextView) findViewById(R.id.nameField);
        homePhoneField = (TextView) findViewById(R.id.homePhoneField);
        mobilePhoneField = (TextView) findViewById(R.id.mobilePhoneField);
        workPhoneField = (TextView) findViewById(R.id.workPhoneField);
        emailField = (TextView) findViewById(R.id.emailField);
        editPersonButton = (Button) findViewById(R.id.editPersonButton);
        deletePersonButton = (Button) findViewById(R.id.deletePersonButton);
        callLogButton = (Button) findViewById(R.id.callLogButton);
        smsLogButton = (Button) findViewById(R.id.smsLogButton);

        initEditTextFields();

        editPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PersonViewActivity.this, PersonEditActivity.class);
                i.putExtra("Person", p);
                startActivity(i);
            }
        });

        deletePersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = p.get_name();
                db.deletePerson(p.get_id());
                Toast.makeText(PersonViewActivity.this, name + " is deleted successfully!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PersonViewActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        callLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PersonViewActivity.this,CallLogActivity.class);
                i.putExtra("PERSON", p);
                startActivity(i);
            }
        });

        smsLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PersonViewActivity.this,SMSLogActivity.class);
                i.putExtra("PERSON",p);
                startActivity(i);
            }
        });
        db.close();
    }

    private void initEditTextFields() {
        if (p == null) return;

        nameField.setText(p.get_name());
        homePhoneField.setText(p.get_homePhoneNumber());
        mobilePhoneField.setText(p.get_mobilePhoneNumber());
        workPhoneField.setText(p.get_workPhoneNumber());
        emailField.setText(p.get_eMail());
    }

}
