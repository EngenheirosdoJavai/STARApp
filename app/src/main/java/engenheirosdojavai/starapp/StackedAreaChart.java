package engenheirosdojavai.starapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by figo on 5/30/17.
 */

public class StackedAreaChart {
    private Context mainActivityContext;
    private MainActivity.ScrollToDay scrollToDay;

    private LineChart chart;

    private LineDataSet waterDataSet;
    private LineDataSet electricityDataSet;
    private LineDataSet gasDataSet;

    List<Entry> waterEntries;
    List<Entry> electricityEntries;
    List<Entry> gasEntries;

    public StackedAreaChart(View c, Context cx, MainActivity.ScrollToDay s){
        mainActivityContext = cx;
        scrollToDay = s;

        waterEntries       = new ArrayList<Entry>();
        electricityEntries = new ArrayList<Entry>();
        gasEntries         = new ArrayList<Entry>();

        chart = (LineChart) c;

        chart.getXAxis().setAxisLineColor(Color.WHITE);
        chart.getXAxis().setGridColor(Color.WHITE);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.getAxisRight().setAxisLineColor(Color.WHITE);
        chart.getAxisRight().setGridColor(Color.WHITE);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);

        chart.invalidate();
    }

    @Deprecated
    public void addEntry(Float value, String type, Integer day){
        switch (type){
            case "water":
                waterEntries.add(new Entry(day, value));
                break;
            case "electricity":
                electricityEntries.add(new Entry(day, value));
                break;
            case "gas":
                gasEntries.add(new Entry(day, value));
                break;
        }
    }

    public void addDayEntry(Float g, Float e, Float w, Integer day){
        gasEntries.add(new Entry(day, g + e +w));
        electricityEntries.add(new Entry(day, e + w));
        waterEntries.add(new Entry(day, w));
    }

    public void invalidate(){

        electricityDataSet = new LineDataSet(electricityEntries, "Eletrecidade");
        electricityDataSet.setDrawFilled(true);
        electricityDataSet.setColor(Color.argb(0, 0, 0, 0));
        electricityDataSet.setFillColor(Color.argb(255, 215, 225, 238));
        electricityDataSet.disableDashedHighlightLine();

        Drawable drawable = ContextCompat.getDrawable(mainActivityContext, R.drawable.fade_inter);
        electricityDataSet.setFillDrawable(drawable);
        electricityDataSet.setDrawCircles(false);
        electricityDataSet.setDrawValues(false);

        gasDataSet = new LineDataSet(gasEntries, "Gas");
        gasDataSet.setDrawFilled(true);
        gasDataSet.setColor(Color.argb(0, 0, 0, 0));
        gasDataSet.setFillColor(Color.argb(255, 107, 125, 135));
        gasDataSet.disableDashedHighlightLine();

        gasDataSet.setDrawCircles(false);
        gasDataSet.setDrawValues(false);


        waterDataSet = new LineDataSet(waterEntries, "Água");
        waterDataSet.setDrawFilled(true);
        waterDataSet.setFillAlpha(0);
        waterDataSet.setColor(Color.argb(0, 0, 0, 0));
        waterDataSet.disableDashedHighlightLine();

        Drawable drawable2 = ContextCompat.getDrawable(mainActivityContext, R.drawable.fade_lower);
        waterDataSet.setFillDrawable(drawable2);
        waterDataSet.setDrawCircles(false);
        waterDataSet.setDrawValues(false);

        LineData lineData = new LineData();
        lineData.addDataSet(gasDataSet);
        lineData.addDataSet(electricityDataSet);
        lineData.addDataSet(waterDataSet);

        chart.setData(lineData);
        chart.getLegend().setEnabled(false);
        implementGesture();
        implementValueSelectorListener();
        chart.animateY(3000);
    }

    private void implementGesture(){
        chart.setOnChartGestureListener(new OnChartGestureListener() {
            private boolean showAxis = false;
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                showAxis = !showAxis;

                /*waterDataSet.setDrawValues(showValues);
                gasDataSet.setDrawValues(showValues);
                electricityDataSet.setDrawValues(showValues);*/

                chart.getAxisLeft().setEnabled(showAxis);
                chart.setHighlightPerTapEnabled(showAxis);
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
    }

    private void implementValueSelectorListener(){
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Log.v("Teste", Float.toString(e.getX()));
                scrollToDay.scroll(Math.round(e.getX()));
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

}
