package engenheirosdojavai.starapp;

import android.os.AsyncTask;
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
import java.util.Iterator;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeLayout;
    private StackedAreaChart chart;
    private ScrollView scrollView;

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

    }

    public class ScrollToDay{
        public ScrollToDay(){}

        public void scroll(Integer d){
            LinearLayout history = (LinearLayout) findViewById(R.id.history);

            View v = history.findViewWithTag(d.toString());

            scrollView.smoothScrollTo(0, Math.round(v.getY()));
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
            super.onPostExecute(result);
            response = response.substring(1, response.length() - 1);

            chart = new StackedAreaChart(findViewById(R.id.chart), getBaseContext(), new ScrollToDay());

            Float totalWater = 0.0f;
            Float totalGas = 0.0f;
            Float totalElectricity = 0.0f;
            float totalMonth = 0f;

            try{
                JSONObject month = new JSONObject(response);
                JSONObject days = month.getJSONObject("days");
                String monthName = month.getString("month");
                String year = month.getString("year");

                TextView monthNameView = (TextView) findViewById(R.id.monthName);
                //monthNameView.setText(monthName);

                TextView yearSufixView = (TextView) findViewById(R.id.yearSufix);
                yearSufixView.setText(year.substring(2,4));

                LinearLayout history = (LinearLayout) findViewById(R.id.history);
                history.removeAllViews();

                Iterator<String> keys = days.keys();
                while(keys.hasNext()){
                    String k = keys.next();

                    LinearLayout historyDay = (LinearLayout) View.inflate(history.getContext(), R.layout.history_day, null);
                    historyDay.setTag(k);

                    ((TextView) historyDay.findViewById(R.id.day_week)).setText(days.getJSONObject(k).getString("day").substring(0,3));
                    ((TextView) historyDay.findViewById(R.id.day_month)).setText(k.toString());

                    history.addView((historyDay));

                    JSONArray day = days.getJSONObject(k).getJSONArray("history");

                    totalWater = 0.0f;
                    totalElectricity = 0.0f;
                    totalGas = 0.0f;

                    LinearLayout dayHistory = ((LinearLayout) historyDay.findViewById(R.id.day_history));
                    for (int h = 0; h < day.length(); h++){
                        View v = View.inflate(dayHistory.getContext(), R.layout.history_entry, null);

                        ((TextView) v.findViewById(R.id.domain)).setText(day.getJSONObject(h).getString("domain"));
                        ((TextView) v.findViewById(R.id.timeInterval)).setText("(" + day.getJSONObject(h).getString("duration") + " minutos)");
                        ((TextView) v.findViewById(R.id.cost)).setText(day.getJSONObject(h).getString("cost"));

                        String t = day.getJSONObject(h).getString("type");
                        float  vl = Float.valueOf(day.getJSONObject(h).getString("cost"));
                        if (t.equals("water")) {
                            totalWater += vl;
                        }else if (t.equals("gas")){
                            totalGas += vl;
                        }else{
                            totalElectricity += vl;
                        }

                        dayHistory.addView(v);
                    }

                    totalMonth += totalElectricity + totalGas + totalWater;

                    LinearLayout viewDayTotal = (LinearLayout) View.inflate(dayHistory.getContext(), R.layout.history_entry_total, null);
                    ((TextView) viewDayTotal.findViewById(R.id.totalDay)).setText( String.format("%.2f", totalElectricity + totalGas + totalWater) );

                    dayHistory.addView(viewDayTotal);

                    chart.addDayEntry(totalGas, totalElectricity, totalWater, Integer.valueOf(k));
                }


                ((TextView) findViewById(R.id.totalMonth)).setText(String.format("%.2f", totalMonth));

                chart.invalidate();

            }catch (org.json.JSONException e){
                Log.w("JSONWarning", e.toString());
            }
        }
    }
}
