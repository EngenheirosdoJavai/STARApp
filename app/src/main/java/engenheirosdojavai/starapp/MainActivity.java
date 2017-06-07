package engenheirosdojavai.starapp;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.URL;
import java.util.Locale;

import engenheirosdojavai.starlib.DataCollection;
import engenheirosdojavai.starlib.DataEntry;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeLayout;
    private StackedAreaChart chart;
    private ScrollView scrollView;
    private Integer selectedYear;
    private Integer selectedMonth;
    private DataCollection dataCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v("Teste", "Teste");
                new FetchData().execute("http://private-30c162-javaistars.apiary-mock.com/user/2/history");
                swipeLayout.setRefreshing(false);
            }
        });

        new FetchData().execute("http://private-30c162-javaistars.apiary-mock.com/user/2/history");


        scrollView = (ScrollView) findViewById(R.id.history_m);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                int scrollY = ((ScrollView) findViewById(R.id.history_m)).getScrollY();
                if(scrollY == 0) swipeLayout.setEnabled(true);
                else swipeLayout.setEnabled(false);

            }
        });

        selectedMonth = 5;
        selectedYear  = 2017;
    }

    public class ScrollToDay{
        public ScrollToDay(){}

        public void scroll(String d){
            LinearLayout history = (LinearLayout) findViewById(R.id.history);

            View v = history.findViewWithTag(d);

            scrollView.smoothScrollTo(0, Math.round(v.getY()));
        }
    }

    private void populateScreen(){
        chart = new StackedAreaChart(findViewById(R.id.chart), getBaseContext(), new ScrollToDay());

        DataCollection.Month month = dataCollection.getMonth(2017, 5);

        LinearLayout history = (LinearLayout) findViewById(R.id.history);
        history.removeAllViews();


        Integer i = 0;

        for(DataCollection.Day d : month.getDays().values()){
            LinearLayout historyDay = (LinearLayout) View.inflate(history.getContext(), R.layout.history_day, null);
            historyDay.setTag(d.getDateOfFirstEntry().toString());

            ((TextView) historyDay.findViewById(R.id.day_week)).setText(d.getDayOfWeek().substring(0,3));
            ((TextView) historyDay.findViewById(R.id.day_month)).setText(d.getDay().toString());
            ((TextView) historyDay.findViewById(R.id.month)).setText("/0" + Integer.toString(month.getMonth()));

            history.addView(historyDay);

            LinearLayout dayHistory = ((LinearLayout) historyDay.findViewById(R.id.day_history));
            for(DataEntry de : d.getDataEntries()){
                View v = View.inflate(dayHistory.getContext(), R.layout.history_entry, null);

                ((TextView) v.findViewById(R.id.domain)).setText(de.getDomain());
                ((TextView) v.findViewById(R.id.timeInterval)).setText("(" + de.getDuration() + " minutos)");
                ((TextView) v.findViewById(R.id.cost)).setText(Float.toString(de.getCost()));

                dayHistory.addView(v);
            }

            LinearLayout viewDayTotal = (LinearLayout) View.inflate(dayHistory.getContext(), R.layout.history_entry_total, null);
            ((TextView) viewDayTotal.findViewById(R.id.totalDay)).setText( String.format("%.2f", d.getTotalCost()));

            dayHistory.addView(viewDayTotal);

            chart.addDayEntry(d.getTotalCostGas(), d.getTotalCostElectricity(), d.getTotalCostWater(), d.getDateOfFirstEntry());
            chart.invalidate();
        }
    }

    private class FetchData extends AsyncTask<String, String, String>{
        private String response;

        protected void onPreExecute() {

        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    //Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                response = buffer.toString();
                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            dataCollection = new DataCollection().dataCollectionBuild(response);
            populateScreen();
        }
    }
}
