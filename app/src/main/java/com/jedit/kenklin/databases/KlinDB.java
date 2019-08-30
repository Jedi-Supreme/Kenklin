package com.jedit.kenklin.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.jedit.kenklin.models.Basket_Items;
import com.jedit.kenklin.models.Request_Class;
import com.jedit.kenklin.models.Services_offered;
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

        String serviceQuery = "CREATE TABLE IF NOT EXISTS " + Services_offered.TABLE + " ( " +
                Services_offered.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Services_offered.ITEM_SIZE + "INTEGER DEFAULT 0, " +
                Services_offered.NAME + " TEXT UNIQUE )";
        db.execSQL(serviceQuery);

        String reqQuery = "CREATE TABLE IF NOT EXISTS " + Request_Class.TABLE + " ( "
                + Request_Class.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Request_Class.REQ_TIME_STAMP + " TEXT UNIQUE, "
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

    private Cursor cursor_ServTable(){

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + Services_offered.TABLE + " ORDER BY " + Services_offered.NAME;

        return db.rawQuery(query,null);
    }

    private Cursor cursor_ReqTable(){

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + Request_Class.TABLE ;

        return db.rawQuery(query,null);
    }

    private Cursor cursor_itemTable(String servicename){

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + "\"" + servicename.toUpperCase() + "\" ORDER BY " + Basket_Items.NAME;

        return db.rawQuery(query,null);
    }

    private ArrayList<Basket_Items> items_list(String servicename){

        ArrayList<Basket_Items> items = new ArrayList<>();

        Cursor c = cursor_itemTable(servicename);

        while (c.moveToNext()){
            items.add(new Basket_Items(
                    c.getString(c.getColumnIndexOrThrow(Basket_Items.NAME)),
                    servicename,
                    c.getInt(c.getColumnIndexOrThrow(Basket_Items.PRICE))));
        }
        c.close();

        return items;

    }

    //create items table by service name, add items if not empty
    private void itemsTable_create(Services_offered service){

        SQLiteDatabase db = getWritableDatabase();

        String serviceQuery = "CREATE TABLE IF NOT EXISTS " + "\"" + service.getName().toUpperCase() + "\" ( " +
                Basket_Items.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Basket_Items.PRICE + " INTEGER, " +
                Basket_Items.NAME + " TEXT UNIQUE)";
        db.execSQL(serviceQuery);

        if (service.getService_items() != null && service.getService_items().size() > 0){

            additems(service);
        }

    }

    private void additems(Services_offered servicesOffered){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues itemValues = new ContentValues();

        String tablename = "\"" + servicesOffered.getName().toUpperCase() + "\"";

        for (Basket_Items item: servicesOffered.getService_items()) {

            itemValues.put(Basket_Items.NAME,item.getItem_name().toUpperCase());
            itemValues.put(Basket_Items.PRICE,item.getPrice());

            try {
                db.insertOrThrow(tablename,null,itemValues);
            }catch (Exception ignored){}
        }

    }

    public ArrayList<String> service_names(){

        ArrayList<String> names = new ArrayList<>();

        Cursor c = cursor_ServTable();

        while (c.moveToNext()){
            names.add(c.getString(c.getColumnIndexOrThrow(Services_offered.NAME)));
        }
        c.close();

        return names;
    }

    //add service from online DB
    public void AddonlineServices(Services_offered servicesOffered){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues servValues = new ContentValues();

        servValues.put(Services_offered.NAME,servicesOffered.getName().toUpperCase());

        try {
            db.insertOrThrow(Services_offered.TABLE,null,servValues);
            itemsTable_create(servicesOffered);

            if (servicesOffered.getService_items() != null && servicesOffered.getService_items().size() > 0){
                servValues.put(Services_offered.ITEM_SIZE,servicesOffered.getService_items().size());

                additems(servicesOffered);
            }
        }catch (Exception ignored){

            if (servicesOffered.getService_items() != null && servicesOffered.getService_items().size() > 0){
                servValues.put(Services_offered.ITEM_SIZE,servicesOffered.getService_items().size());

                additems(servicesOffered);
            }
        }

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
        reqValues.put(Request_Class.REQ_TIME_STAMP,request.getReqTime_stamp());
        reqValues.put(Request_Class.REQDATE,request.getReqDate());
        reqValues.put(Request_Class.COMPDATE,request.getCompleteDate());
        reqValues.put(Request_Class.COMPSTATUS,String.valueOf(request.getStatus()));

        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(Request_Class.TABLE,null,reqValues);
    }

    public ArrayList<Request_Class> all_requests(){

        ArrayList<Request_Class> reqs = new ArrayList<>();

        Cursor c = cursor_ReqTable();

        while (c.moveToNext()){

            Request_Class req = new Request_Class(
                    c.getString(c.getColumnIndexOrThrow(Request_Class.REQ_TIME_STAMP)),
                    c.getString(c.getColumnIndexOrThrow(Request_Class.REQDATE)),
                    c.getString(c.getColumnIndexOrThrow(Request_Class.COMPDATE)),
                    c.getString(c.getColumnIndexOrThrow(Request_Class.COMPSTATUS))
            );

            reqs.add(req);
        }

        return reqs;
    }

    public ArrayList<Basket_Items> fetch_all_items(){

        ArrayList<Basket_Items> all_items = new ArrayList<>();

        for (String servname : service_names()){

            all_items.addAll(items_list(servname));
        }

        return all_items;
    }

}
