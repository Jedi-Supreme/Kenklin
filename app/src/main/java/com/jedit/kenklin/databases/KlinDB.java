package com.jedit.kenklin.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.jedit.kenklin.models.Cloths;
import com.jedit.kenklin.models.Request_Class;
import com.jedit.kenklin.models.User_Class;

import java.util.ArrayList;

public class KlinDB extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "KENKLIN.db";

    public KlinDB(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String userQuery = "CREATE TABLE IF NOT EXISTS " + User_Class.TABLE + " ( "
                + User_Class.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + User_Class.FIREID + " TEXT UNIQUE NOT NULL, "
                + User_Class.FIRSTNAME + " TEXT, "
                + User_Class.LASTNAME + " TEXT, "
                + User_Class.MOBILE_NUMBER + " TEXT );";
        db.execSQL(userQuery);

        String reqQuery = "CREATE TABLE IF NOT EXISTS " + Request_Class.TABLE + " ( "
                + Request_Class.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Request_Class.REQ_PUSH_ID + " TEXT UNIQUE, "
                + Request_Class.REQDATE + " TEXT, "
                + Request_Class.COMPDATE + " TEXT, "
                + Request_Class.COMPSTATUS + " TEXT )";
        db.execSQL(reqQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + User_Class.TABLE);
        onCreate(db);
    }

    public void addUser(User_Class user){

        ContentValues userValues = new ContentValues();

        userValues.put(User_Class.FIREID,user.getFirebaseID());
        userValues.put(User_Class.FIRSTNAME,user.getFn());
        userValues.put(User_Class.LASTNAME,user.getLn());
        userValues.put(User_Class.MOBILE_NUMBER,user.getNumber());

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.insertOrThrow(User_Class.TABLE,null,userValues);
        }catch (SQLiteConstraintException ignored){
            updateUser(user);
        }

    }

    public User_Class fetchUser(String fireID){
        User_Class appuser;

        SQLiteDatabase db = getReadableDatabase();
        String userQery = "SELECT * FROM " + User_Class.TABLE + " WHERE "
                + User_Class.FIREID + " = \"" + fireID + "\"";

        Cursor c = db.rawQuery(userQery,null);
        c.moveToFirst();

        appuser = new User_Class(
                c.getString(c.getColumnIndexOrThrow(User_Class.FIRSTNAME)),
                c.getString(c.getColumnIndexOrThrow(User_Class.LASTNAME)),
                c.getString(c.getColumnIndexOrThrow(User_Class.MOBILE_NUMBER)));

        c.close();

        return appuser;
    }

    private void updateUser(User_Class userClass){

        ContentValues userValues = new ContentValues();

        userValues.put(User_Class.FIREID,userClass.getFirebaseID());
        userValues.put(User_Class.FIRSTNAME,userClass.getFn());
        userValues.put(User_Class.LASTNAME,userClass.getLn());
        userValues.put(User_Class.MOBILE_NUMBER,userClass.getNumber());

        SQLiteDatabase db = getWritableDatabase();

        db.update(User_Class.TABLE,userValues,User_Class.FIREID + " = ? ",
                new String[]{userClass.getFirebaseID()});
    }

    public void addRequest(Request_Class request){

        ContentValues reqValues = new ContentValues();
        reqValues.put(Request_Class.REQ_PUSH_ID,request.getReqPush_id());
        reqValues.put(Request_Class.REQDATE,request.getReqDate());
        reqValues.put(Request_Class.COMPDATE,request.getCompleteDate());
        reqValues.put(Request_Class.COMPSTATUS,String.valueOf(request.getCompleted()));

        SQLiteDatabase db = getWritableDatabase();

        db.insertOrThrow(Request_Class.TABLE,null,reqValues);
        createClothsTable(request);

    }

    private void createClothsTable(Request_Class request){

        SQLiteDatabase db = getWritableDatabase();

        String clothsQuery = "CREATE TABLE IF NOT EXISTS " + request.getReqPush_id() + " ( "
                + Request_Class.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Cloths.TYPENAME + " TEXT, "
                + Cloths.QUANTITY + " INTEGER )";
        db.execSQL(clothsQuery);

        addCloths(request);
    }

    private void addCloths(Request_Class request){

        ContentValues clothValues = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        for (Cloths cloth : request.getLaundrylist()){

            clothValues.put(Cloths.TYPENAME,cloth.getTypename());
            clothValues.put(Cloths.QUANTITY,cloth.getQty());

            db.insertOrThrow(request.getReqPush_id(),null,clothValues);
        }

    }
}
