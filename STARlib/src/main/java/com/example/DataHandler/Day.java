package com.example.DataHandler;

import java.util.ArrayList;

/**
 * Created by figo on 5/7/17.
 */

public class Day {
    private int day;
    private ArrayList<Register> history;

    public Day(int d, ArrayList<Register> r){
        day = d;
        history = r;
    }

    public ArrayList<Register> getHistory(){
        return history;
    }
}
