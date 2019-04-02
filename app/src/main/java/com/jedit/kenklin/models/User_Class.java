package com.jedit.kenklin.models;

public class User_Class {

    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String FIRSTNAME = "firstName";
    public static final String LASTNAME = "lastName";
    public static final String FIREID = "firebaseID";
    public static final String ID = "_id";
    public static final String TABLE = "USERS";

    private String firebaseID;
    private String fn;
    private String ln;
    private String number;

    public User_Class() {
    }

    public User_Class(String fn, String ln, String number) {
        this.fn = fn;
        this.ln = ln;
        this.number = number;
    }

    public User_Class(String firebaseID, String fn, String ln, String number) {
        this.firebaseID = firebaseID;
        this.fn = fn;
        this.ln = ln;
        this.number = number;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getLn() {
        return ln;
    }

    public void setLn(String ln) {
        this.ln = ln;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }
}
