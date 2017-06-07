package engenheirosdojavai.starlib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by figo on 6/6/17.
 */

public class DataEntry implements Comparable<DataEntry>{
    @Override
    public int compareTo(DataEntry o) {
        return getDate().compareTo(o.getDate());
    }

    public enum Type{WATER, ELECTRICITY, GAS};

    private String domain;
    private float  duration;
    private float  cost;
    private Type   type;
    private Date   date;

    public DataEntry(String dom, float duration, float c, String t, String dt){
        domain = dom;
        duration = duration;
        cost = c;
        type = typeFromString(t);
        date = dateFromString(dt);
    }


    public String getDomain() {
        return domain;
    }

    public float getDuration() {
        return duration;
    }

    public float getCost() {
        return cost;
    }

    public Type getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    private Type typeFromString(String t){
        switch (t){
            case "water":
                return Type.WATER;
            case "electricity":
                return Type.ELECTRICITY;
            case "gas":
                return Type.GAS;
        }

        return null;
    }

    private Date dateFromString(String s){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

        try {
            return format.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<DataEntry> dataEntriesBuild(String json){
        ArrayList<DataEntry> dataEntries = new ArrayList<DataEntry>();

        try {
            JSONObject month = new JSONObject(json);
            JSONArray history = month.getJSONArray("_history");

            for(int i = 0 ; i < history.length(); i ++){
                JSONObject h  = history.getJSONObject(i);

                dataEntries.add(new DataEntry(h.getString("domain"), (float)h.getDouble("duration"), (float)h.getDouble("cost"), h.getString("type"), h.getString("date")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(dataEntries);
        return dataEntries;
    }

}
