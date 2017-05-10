package com.example.DataHandler;

/**
 * Created by figo on 5/7/17.
 */

import org.json.*;

public class DataFactory {
    private static DataFactory ourInstance = new DataFactory();

    public static DataFactory getInstance() {
        return ourInstance;
    }

    private DataFactory() {
    }

    public static Month buildDataFromJSON(String json){
        Month month = null;

        JSONObject m = new JSONObject(json);
        System.out.print(m);


        return month;
    }
}
