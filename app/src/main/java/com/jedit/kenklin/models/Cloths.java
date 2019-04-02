package com.jedit.kenklin.models;

public class Cloths {

    public static final String TYPENAME = "typeName";
    public static final String QUANTITY = "quantity";

    private String typename;
    private int qty;

    public Cloths(String typename, int qty) {
        this.typename = typename;
        this.qty = qty;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
