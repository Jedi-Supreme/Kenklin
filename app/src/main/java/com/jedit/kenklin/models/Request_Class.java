package com.jedit.kenklin.models;

import java.util.ArrayList;

public class Request_Class {

    public static final String TABLE = "REQUESTS";
    public static final String ID = "_id";
    public static final String REQDATE = "reqDate";
    public static final String COMPDATE = "compDate";
    public static final String COMPSTATUS = "compStatus";
    public static final String REQ_PUSH_ID = "reqPushID";

    private String reqPush_id;
    private String requesterID;
    private String reqDate;
    private String completeDate;
    private Boolean completed;
    private ArrayList<Cloths> laundrylist;

    public Request_Class(String requesterID, String reqDate,
                         String completeDate, Boolean completed, ArrayList<Cloths> laundrylist) {
        this.requesterID = requesterID;
        this.reqDate = reqDate;
        this.completeDate = completeDate;
        this.completed = completed;
        this.laundrylist = laundrylist;
    }

    public Request_Class(String requesterID, String reqDate, ArrayList<Cloths> laundrylist) {
        this.requesterID = requesterID;
        this.reqDate = reqDate;
        this.laundrylist = laundrylist;
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

    public ArrayList<Cloths> getLaundrylist() {
        return laundrylist;
    }

    public void setLaundrylist(ArrayList<Cloths> laundrylist) {
        this.laundrylist = laundrylist;
    }

    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getReqPush_id() {
        return reqPush_id;
    }

    public void setReqPush_id(String reqPush_id) {
        this.reqPush_id = reqPush_id;
    }
}
