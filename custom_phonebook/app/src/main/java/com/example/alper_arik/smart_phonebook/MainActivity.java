package com.example.alper_arik.smart_phonebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button addButton;
    private Button fetchButton;
    private int counter = 0; //uygulamanın açılış sayısını tutar (Shared Pereferences)
    private EditText searchEditText;
    private ArrayList<Person>  contactsPerson = null;
    private ArrayList<Person> contactsDB = null;
    private ArrayList<Person> nonFiltered = null;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = (Button) findViewById(R.id.addButton);
        fetchButton = (Button) findViewById(R.id.fetchButton);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        final ListView listView = (ListView) findViewById(R.id.personListView);

        SharedPreferences sharedPreferences = getSharedPreferences("OpeningCounter",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        counter = sharedPreferences.getInt("counter",0);
        editor.putInt("counter", ++counter);
        editor.commit();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButtonOnClick();
            }
        });
        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchButtonOnClick();
            }
        });
        fetchButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(i);
                return false;
            }
        });

        fetchAllSIMContact();
        getCallDetails();

        DatabaseHelper db = DatabaseHelper.getInstance(MainActivity.this);
        contactsDB = db.readAll();
        nonFiltered = db.readAll();
        db.close();

        //Sorts people by name
        sortListView(contactsDB);

        adapter = new CustomAdapter(this,contactsDB);
        listView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchEditTextOnChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("TAG", Long.toString(id));

                Person selected = (Person) listView.getItemAtPosition(position);
                String personId = UUID.nameUUIDFromBytes(selected.get_name().getBytes()).toString();
                Intent i = new Intent(MainActivity.this, PersonViewActivity.class);
                i.putExtra("ID", personId);
                startActivity(i);
            }
        });

    }

    private void addButtonOnClick(){
        Intent i =  new Intent(this,PersonAddActivity.class);
        startActivity(i);
    }

    public  void refreshActivity(){
        Intent i =  new Intent(MainActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }
    private void fetchButtonOnClick(){
        fetchAllSIMContact();
        refreshActivity();
    }

    private void sortListView(ArrayList<Person> al){
        Collections.sort(al, new Comparator<Person>() {
            @Override
            public int compare(Person lhs, Person rhs) {
                String st1 = lhs.get_name();
                String st2 = rhs.get_name();

                return st1.compareTo(st2);
            }
        });
    }

    private void fetchAllSIMContact() {

        SharedPreferences sp = getSharedPreferences("OpeningCounter",MODE_PRIVATE);
        int openingNum = sp.getInt("counter", 0);
        if(openingNum != 1) return;

        contactsPerson = new ArrayList<>();
        Person p = null;

        try {
            String ClsSimPhonename = null;
            String ClsSimphoneNo = null;

            Uri simUri = Uri.parse("content://icc/adn");
            Cursor cursorSim = this.getContentResolver().query(simUri,null,null, null, null);
            //Cursor cursorSim = this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null, null, null);

            Log.i("PhoneContact", "total: " + cursorSim.getCount());

            while (cursorSim.moveToNext()) {

                ClsSimPhonename =cursorSim.getString(cursorSim.getColumnIndex("name"));
                ClsSimPhonename=ClsSimPhonename.replace("|", "");
                ClsSimphoneNo = cursorSim.getString(cursorSim.getColumnIndex("number"));
                ClsSimphoneNo.replaceAll("\\D", "");
                ClsSimphoneNo.replaceAll("&", "");

                Log.i("PhoneContact", "name: " + ClsSimPhonename + " phone: " + ClsSimphoneNo);

                p =  new Person();
                p.set_name(ClsSimPhonename.toLowerCase());
               if(ClsSimphoneNo.charAt(0) != '+'){
                   String fix = "+9"+ClsSimphoneNo;
                   ClsSimphoneNo = fix;
               }
                p.set_mobilePhoneNumber(ClsSimphoneNo);
                p.set_homePhoneNumber(null);
                p.set_workPhoneNumber(null);
                p.set_eMail(null);
                String id = UUID.nameUUIDFromBytes(p.get_name().getBytes()).toString();
                p.set_id(id);
                contactsPerson.add(p);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        saveContacts();
    }

    private void saveContacts(){
        if(contactsPerson == null) return;

        DatabaseHelper db = DatabaseHelper.getInstance(MainActivity.this);
        for(Person p : contactsPerson){
            //Oluşturulan kişi db de bulunmuyorsa
            if(db.getPerson(p.get_id()) ==  null)
                db.addPerson(p);
        }
        db.close();
    }

    private void searchEditTextOnChanged(){
        if (adapter != null) {
            ArrayList<Person> filtered = new ArrayList<Person>();
            int textLength = searchEditText.getText().toString().length();
            filtered.clear();

            //Search by name
            for (int i = 0; i < nonFiltered.size(); i++) {
                if (textLength <= nonFiltered.get(i).get_name().length()) {
                    for(int j = 0; j < nonFiltered.get(i).get_name().length() - searchEditText.length(); j++)
                    if (searchEditText.getText().toString().equalsIgnoreCase((String) nonFiltered.get(i).get_name().subSequence(0+j, textLength+j))){
                        filtered.add(nonFiltered.get(i));
                    }
                }
            }

            //Search by number
            for (int i = 0; i < nonFiltered.size(); i++) {
                if (textLength <= nonFiltered.get(i).get_mobilePhoneNumber().length()) {
                    for(int j = 0; j < nonFiltered.get(i).get_mobilePhoneNumber().length() - searchEditText.length(); j++)
                        if (searchEditText.getText().toString().equalsIgnoreCase((String) nonFiltered.get(i).get_mobilePhoneNumber().subSequence(0+j, textLength+j))){
                            filtered.add(nonFiltered.get(i));
                        }
                }
            }
            contactsDB.clear();
            for(Person x : filtered){
                contactsDB.add(x);
            }

            Set<Person> set = new HashSet<>();
            set.addAll(contactsDB);
            contactsDB.clear();
            contactsDB.addAll(set);

            sortListView(contactsDB);

            adapter.notifyDataSetChanged();
        }
    }


    private void getCallDetails() {

        SharedPreferences sp = getSharedPreferences("OpeningCounter",MODE_PRIVATE);
        int openingNum = sp.getInt("counter", 0);
        if(openingNum != 1) return;

        Cursor managedCursor = null;
        try{
            managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        }catch (SecurityException e) {e.printStackTrace();}

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        while ( managedCursor.moveToNext() ) {
            StringBuffer sb = new StringBuffer();
            String phNumber = managedCursor.getString( number );
            String callType = managedCursor.getString( type );
            String callDate = managedCursor.getString( date );
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString( duration );
            String dir = null;

            int dircode = Integer.parseInt(callType);

            if(dircode <1 || dircode > 3) continue;

            switch( dircode ) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "outgoing";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "incoming";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "missed";
                    break;
            }
            String simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(callDayTime);
            sb.append(callDuration+";"+dir+";"+simpleDateFormat);
            if(phNumber.charAt(0) != '+'){
                String fix = "+9"+phNumber;
                phNumber = fix;
            }

            FileOperation.appendToFile(phNumber, sb.toString(), getBaseContext());
            Log.i("CALL_LOG",phNumber+" -> "+sb.toString());
        }
        managedCursor.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        //DEBUG PURPOSE ONLY
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            db.deleteDB();
            Toast.makeText(this, "DB deleted", Toast.LENGTH_SHORT).show();
        }
        else if((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            SharedPreferences sp = getSharedPreferences("Call_info",MODE_PRIVATE);
            sp.edit().clear().commit();

            Toast.makeText(this, "SP deleted", Toast.LENGTH_SHORT).show();
            sp = getSharedPreferences("OpeningCounter",MODE_PRIVATE);
            sp.edit().clear().commit();

            DatabaseHelper db = DatabaseHelper.getInstance(getBaseContext());
            ArrayList<Person> persons = db.readAll();
            for(Person p : persons){
                FileOperation.clearFile(p.get_id(), getBaseContext());
                FileOperation.clearFile(p.get_mobilePhoneNumber(), getBaseContext());
            }
            db.close();
        }
        return true;
    }
}
