package com.example.DataHandler;

public class Month {
    private String month;
    private Day day;
    private double totalCost;

    public Month(String m, Day d, double t){
        month = m;
        day = d;
        totalCost = t;
    }

    public String getName(){
        return month;
    }
}
