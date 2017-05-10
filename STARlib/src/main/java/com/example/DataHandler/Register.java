package com.example.DataHandler;

/**
 * Created by figo on 5/7/17.
 */

public class Register {
    private String domain;
    private    int   time;
    private double  value;

    public Register(String d, int t, double v){
        domain = d;
        time   = t;
        value  = v;
    }

    public void setDomain(String d){
        domain = d;
    }

    public void setTime(int t){
        time = t;
    }

    public void setValue(double d){
        value = d;
    }

    public String getDomain(){
        return domain;
    }

    public int getTime(){
        return time;
    }

    public double getValue(){
        return value;
    }
}
