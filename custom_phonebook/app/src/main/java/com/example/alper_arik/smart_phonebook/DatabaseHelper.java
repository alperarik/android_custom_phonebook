package com.example.alper_arik.smart_phonebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;
import java.util.ArrayList;

/**
 * Uses for manipulating database
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    //Database infos and table columns
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "person.db";
    private static final String TABLE_PERSON = "person";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "_name";
    private static final String COLUMN_HOMEPHONENUMBER = "_homePhoneNumber";
    private static final String COLUMN_MOBILEPHONENUMBER = "_mobilePhoneNumber";
    private static final String COLUMN_WORKPHONENUMBER = "_workPhoneNumber";
    private static final String COLUMN_EMAIL = "_eMail";

    //uses for singleton retrieving database
    private static DatabaseHelper dbHelper;

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*Other classes must access Database with this method
    * not creating new DatabaseHelper
    * (Singleton)
    */
    public static synchronized DatabaseHelper getInstance(Context context) {
        //if there is DatabaseHelper instance will not create another instance
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context.getApplicationContext());
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PERSON + "(" +
                COLUMN_ID + " INTEGER ," +
                COLUMN_NAME + " TEXT ," +
                COLUMN_HOMEPHONENUMBER + " TEXT ," +
                COLUMN_MOBILEPHONENUMBER + " TEXT ," +
                COLUMN_WORKPHONENUMBER + " TEXT ," +
                COLUMN_EMAIL + " INTEGER" +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_PERSON);
        onCreate(db);
    }

    public void deleteDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON);
        onCreate(db);
        Log.d("TAG", "Table has deleted successfully");
    }

    /**
     * Adds a new tuple to table
     *
     * @param person Person info
     */
    public void addPerson(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //puts informations to ContentValues instance
        values.put(COLUMN_ID,person.get_id());
        values.put(COLUMN_NAME, person.get_name());
        values.put(COLUMN_HOMEPHONENUMBER, person.get_homePhoneNumber());
        values.put(COLUMN_MOBILEPHONENUMBER, person.get_mobilePhoneNumber());
        values.put(COLUMN_WORKPHONENUMBER, person.get_workPhoneNumber());
        values.put(COLUMN_EMAIL, person.get_eMail());

        //insert new tuple to table
        db.insert(TABLE_PERSON, null, values);
        //closing db
        db.close();
        Log.i("TAG", "person has added ->" + person.get_name() + " " + person.get_mobilePhoneNumber() + " " +
                person.get_homePhoneNumber() + " " + person.get_workPhoneNumber() + " " + person.get_eMail());
    }

    public void deletePerson(String id){
        if(id == null) return;

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+TABLE_PERSON+" WHERE "+COLUMN_ID+" ='"+id+"';";
        db.execSQL(query);
        db.close();
    }
    /**
     * Gets selected person which has passed id
     *
     * @param id person's id
     * @return person which has got passed id
     */
    public Person getPerson(String id) {
        if(id == null) return null;

        //temp Person variable
        Person person = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PERSON + " WHERE " + COLUMN_ID + "='" + id + "';";
        // executes query
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            //initialize person object with empty constructor
            person = new Person();
            //sets person informations
            person.set_id(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            person.set_name(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            person.set_homePhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_HOMEPHONENUMBER)));
            person.set_mobilePhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_MOBILEPHONENUMBER)));
            person.set_workPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_WORKPHONENUMBER)));
            person.set_eMail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
        }
        //closing cursor and db
        cursor.close();
        db.close();
        return person;
    }

    public ArrayList<Person> readAll(){

        Person p = null;
        ArrayList<Person> al = new ArrayList<>();
        String mobilePhone;
        String workPhone;
        String homePhone;
        String eMail;

        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM "+TABLE_PERSON;
        //executes query
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            //initialize person object with empty constructor
            p = new Person();

            p.set_name(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            p.set_id(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));

            workPhone = cursor.getString(cursor.getColumnIndex(COLUMN_WORKPHONENUMBER));
            mobilePhone = cursor.getString(cursor.getColumnIndex(COLUMN_MOBILEPHONENUMBER));
            homePhone = cursor.getString(cursor.getColumnIndex(COLUMN_HOMEPHONENUMBER));
            eMail = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));

            if(workPhone != null)
                p.set_workPhoneNumber(workPhone);

            if(mobilePhone != null)
                p.set_mobilePhoneNumber(mobilePhone);

            if(homePhone != null)
                p.set_homePhoneNumber(homePhone);

            if(eMail != null)
                p.set_eMail(eMail);

            //adds to arraylist
            al.add(p);

            cursor.moveToNext();
        }
        //closing cursor and db
        cursor.close();
        db.close();
        return al;
    }
}
