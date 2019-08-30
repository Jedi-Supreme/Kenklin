package com.jedit.kenklin.models;

import java.util.ArrayList;

public class Request_Class {

    public static final String TABLE = "REQUESTS";
    public static final String ID = "_id";
    public static final String REQDATE = "reqDate";
    public static final String COMPDATE = "compDate";
    public static final String COMPSTATUS = "compStatus";
    public static final String REQ_TIME_STAMP = "reqPushID";

    private String reqTime_stamp;
    private String requesterID;
    private String reqDate;
    private String completeDate;
    private String status;
    private ArrayList<Basket_Items> laundrylist;

    public Request_Class() {
    }

    public Request_Class(String requesterID, String reqDate,
                         String completeDate, String status, ArrayList<Basket_Items> laundrylist) {
        this.requesterID = requesterID;
        this.reqDate = reqDate;
        this.completeDate = completeDate;
        this.status = status;
        this.laundrylist = laundrylist;
    }

    public Request_Class(String reqTime_stamp, String reqDate,
                         String completeDate, String status) {
        this.reqTime_stamp = reqTime_stamp;
        this.reqDate = reqDate;
        this.completeDate = completeDate;
        this.status = status;
    }

    public String getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public ArrayList<Basket_Items> getLaundrylist() {
        return laundrylist;
    }

    public void setLaundrylist(ArrayList<Basket_Items> laundrylist) {
        this.laundrylist = laundrylist;
    }

    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReqTime_stamp() {
        return reqTime_stamp;
    }

    public void setReqTime_stamp(String reqTime_stamp) {
        this.reqTime_stamp = reqTime_stamp;
    }
}
