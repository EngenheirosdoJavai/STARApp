package engenheirosdojavai.starapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.DataHandler.DataFactory;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load data
        DataFactory.buildDataFromJSON(loadJSONFromAsset());

        //UI
        ScrollView history = (ScrollView) findViewById(R.id.history);

        LinearLayout history_day = (LinearLayout) View.inflate(history.getContext(), R.layout.history_day, null);
        history.addView(history_day);

        LinearLayout day_history = ((LinearLayout) history_day.findViewById(R.id.day_history));
        for (int i = 0; i < 20; i++){
            View v = View.inflate(day_history.getContext(), R.layout.history_entry, null);

            ((TextView) v.findViewById(R.id.domain)).setText("Chuveiro");
            ((TextView) v.findViewById(R.id.timeInterval)).setText("(15 minutos)");

            day_history.addView(v);
        }

        //Grafico
        LineChart chart = (LineChart) findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<Entry>();
        List<Entry> entries2 = new ArrayList<Entry>();

        entries.add(new Entry(1, 20));
        entries.add(new Entry(2, 22));
        entries.add(new Entry(3, 21));
        entries.add(new Entry(4, 22));
        entries.add(new Entry(5, 19));
        entries.add(new Entry(6, 22));
        entries.add(new Entry(7, 21));

        LineDataSet dataSet = new LineDataSet(entries, "LABEL");
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(255);
        dataSet.setColor(Color.argb(0, 0, 0, 0));
        dataSet.disableDashedHighlightLine();

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_lower);
        dataSet.setFillDrawable(drawable);
        dataSet.setDrawCircles(false);

        LineData lineData = new LineData(dataSet);

        chart.getXAxis().setAxisLineColor(Color.WHITE);
        chart.getXAxis().setGridColor(Color.WHITE);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.getAxisRight().setAxisLineColor(Color.WHITE);
        chart.getAxisRight().setGridColor(Color.WHITE);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        chart.setData(lineData);

        chart.invalidate();
    }

    public String loadJSONFromAsset() {
        String json = "Erro";
        try {
            InputStream is = getAssets().open("data/sampleData");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
