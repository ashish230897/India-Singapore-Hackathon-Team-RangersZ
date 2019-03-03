package com.example.scoco.facedetection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserDB";
    private static final String TABLE_NAME = "User";
    private static final String USER_ID = "id";
    private static final String USER_FIRST_NAME = "firstName";
    private static final String USER_LAST_NAME = "lastName";
    private static final String USER_CONTACT = "contact";
    private static final String USER_EMAIL = "userEmail";
    private static final String CHECK = "face";
    private static final String TABLE_NAME2 = "CheckFace";

    private static final String[] COLUMNS = {USER_ID,USER_FIRST_NAME,USER_LAST_NAME, USER_CONTACT, USER_EMAIL};

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE User ( "
                + "id TEXT , " + "firstName TEXT, " + "lastName TEXT, "
                + "contact TEXT, " + " userEmail TEXT)";
        String CREATION_TABLE2 = "CREATE TABLE CheckFace ( "
                + "face TEXT )";

        db.execSQL(CREATION_TABLE);
        db.execSQL(CREATION_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_ID, user.id);
        values.put(USER_FIRST_NAME, user.firstName);
        values.put(USER_LAST_NAME, user.lastName);
        values.put(USER_CONTACT, user.contact);
        values.put(USER_EMAIL, user.email);
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public void setFace(String face) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CHECK, face);
        // insert
        db.insert(TABLE_NAME2,null, values);
        db.close();
    }

    public User getUser(){
        //SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        //Cursor cursor = db.query(TABLE_NAME,COLUMNS,"id=?",new String[]{id},null,null,null,null);
        if(cursor!=null) {
            cursor.moveToLast();


            User user = new User();
            user.id = cursor.getString(0);
            user.firstName = cursor.getString(1);
            user.lastName = cursor.getString(2);
            user.contact = cursor.getString(3);
            user.email = cursor.getString(4);

            return user;
        }else{
            return null;
        }

    }

    public String getFace(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_NAME2;
        Cursor cursor = db.rawQuery(query, null);

        //Cursor cursor = db.query(TABLE_NAME,COLUMNS,"id=?",new String[]{id},null,null,null,null);
        if(cursor!=null) {
            cursor.moveToFirst();
            return cursor.getString(0);
        }else{
            return "false";
        }

    }
}