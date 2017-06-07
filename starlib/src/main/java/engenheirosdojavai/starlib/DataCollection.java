package engenheirosdojavai.starlib;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Date;

import static android.R.attr.data;
import static android.R.attr.y;

/**
 * Created by figo on 6/6/17.
 */

public class DataCollection {
    private HashMap<Integer,Year> years;

    public DataCollection(){
        years = new HashMap<>();
    }

    private void addYear(Year year){
        years.put(year.getYear(), year);
        Log.v("DataCollection", "added year " + Integer.toString(year.getYear()));
    }

    public Month getMonth(int y, int m){
        return years.get(y).getMonth(m);
    }

    public class Day{
        private int day;
        private String dayOfWeek;
        private ArrayList<DataEntry> dataEntries;

        float totalCostWater       = 0;
        float totalCostGas         = 0;
        float totalCostElectricity = 0;

        public Day(int d){
            day = d;

            dataEntries = new ArrayList<DataEntry>();
        }

        public float getTotalCostWater() {
            return totalCostWater;
        }

        public float getTotalCostGas() {
            return totalCostGas;
        }

        public float getTotalCostElectricity() {
            return totalCostElectricity;
        }

        public float getTotalCost(){
            return totalCostElectricity + totalCostWater + totalCostGas;
        }

        public void addDataEntry(DataEntry de){
            dataEntries.add(de);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(de.getDate());

            switch (calendar.get(Calendar.DAY_OF_WEEK)){
                case 1:
                    dayOfWeek = "Domingo";
                    break;

                case 2:
                    dayOfWeek = "Segunda";
                    break;
                case 3:
                    dayOfWeek = "Terça";
                    break;
                case 4:
                    dayOfWeek = "Quarta";
                    break;
                case 5:
                    dayOfWeek = "Quinta";
                    break;
                case 6:
                    dayOfWeek = "Sexta";
                    break;
                case 7:
                    dayOfWeek = "Sábado";
                    break;
                default:
                    dayOfWeek = "Nope";
                    break;
            }

            Log.v("TT", Integer.toString(calendar.get(Calendar.DAY_OF_WEEK)));
        }

        public String getDayOfWeek(){
            return dayOfWeek;
        }

        public ArrayList<DataEntry> getDataEntries(){
            return dataEntries;
        }

        public Integer getDay(){
            return day;
        }

        public void generateData(){
            for(DataEntry de : dataEntries){
                switch (de.getType()){
                    case WATER:
                        totalCostWater += de.getCost();
                        break;
                    case GAS:
                        totalCostGas += de.getCost();
                        break;
                    case ELECTRICITY:
                        totalCostElectricity += de.getCost();
                        break;
                }
            }
        }

        public Date getDateOfFirstEntry() {
            return dataEntries.get(0).getDate();
        }
    }

    public class Month{
        private int month;
        private String monthName;

        private HashMap<Integer, Day> days;

        public Month(int m){
            month = m;
            days = new HashMap<>();
        }

        public Integer getMonth(){
            return month;
        }

        public HashMap<Integer, Day> getDays(){
            return days;
        }

        public void addDay(Day d){
            d.generateData();
            days.put(d.getDay(), d);

            Log.v("DataCollection", "added day " + Integer.toString(d.getDay()));
        }
    }

    private class Year{
        public int year;

        private HashMap<Integer, Month> months;

        public Year(int y){
            year = y;
            months = new HashMap<>();
        }

        public void addMonth(Month m){
            months.put(m.getMonth() + 1, m);
            Log.v("DataCollection", "added month " + Integer.toString(m.getMonth()));
        }

        public Month getMonth(int i){
            return months.get(i);
        }

        public int getYear(){
            return year;
        }

    }

    public DataCollection dataCollectionBuild(String j){
        ArrayList<DataEntry> dataEntries = DataEntry.dataEntriesBuild(j);

        int actualYear  = -1;
        int actualMonth = -1;
        int actualDay   = -1;

        Year   year = null;
        Month month = null;
        Day     day = null;

        Calendar calendar = Calendar.getInstance();

        for(DataEntry e : dataEntries){
            calendar.setTime(e.getDate());

            //Verifica se o ano da DataEntry e o mesmo da anterior, se nao cria um novo
            if((int)calendar.get(Calendar.YEAR) != actualYear){
                if(year != null){
                    this.addYear(year);
                }

                year = new Year((int) calendar.get(Calendar.YEAR));
                actualYear = (int) calendar.get(Calendar.YEAR);
            }

            if((int)calendar.get(Calendar.MONTH) != actualMonth){
                if(month != null){
                    year.addMonth(month);
                }

                month = new Month((int)calendar.get(Calendar.MONTH));
                actualMonth = (int) calendar.get(Calendar.MONTH);
            }

            if((int)calendar.get(Calendar.DAY_OF_MONTH) != actualDay){
                if(day != null){
                    month.addDay(day);
                }

                day = new Day((int)calendar.get(Calendar.DAY_OF_MONTH));
                actualDay = (int) calendar.get(Calendar.DAY_OF_MONTH);

            }

            day.addDataEntry(e);
        }

        month.addDay(day);
        year.addMonth(month);
        this.addYear(year);

        return this;
    }
}
