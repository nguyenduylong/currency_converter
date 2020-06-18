package com.duylong.currencyconverter;

import android.graphics.Color;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.duylong.currencyconverter.adapter.CustomAdapter;
import com.duylong.currencyconverter.util.Api;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tiper.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StaticActivity extends AppCompatActivity{

    String fromCurrency = "", toCurrency = "";

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    RequestQueue queue;

    LineChart lineChart;

    MaterialSpinner fromSpinner, toSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fromSpinner = (MaterialSpinner) findViewById(R.id.fromCurrency);
        toSpinner = (MaterialSpinner) findViewById(R.id.toCurrency);

        queue = Volley.newRequestQueue(this);

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), R.layout.custom_spinner_item, Api.COUNTRIES, Api.FLAGS);

        fromSpinner.setAdapter(customAdapter);
        toSpinner.setAdapter(customAdapter);

        fromSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, View view, int i, long l) {
                fromCurrency = Api.COUNTRIES[i];
                handleData();
            }

            @Override
            public void onNothingSelected(MaterialSpinner materialSpinner) {

            }
        });

        toSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, View view, int i, long l) {
                toCurrency = Api.COUNTRIES[i];
                handleData();
            }

            @Override
            public void onNothingSelected(MaterialSpinner materialSpinner) {

            }
        });

        lineChart = (LineChart) findViewById(R.id.chart1);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setViewPortOffsets(2f, 0f, 0f, 2f);


        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(179, 122, 0));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd");

            @Override
            public String getFormattedValue(float value) {

                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleData() {
        if ("".equals(fromCurrency) || "".equals(toCurrency)) {
            Toast.makeText(getApplicationContext(), "Please select both From and To", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fromCurrency.equals(toCurrency)) {
            Toast.makeText(getApplicationContext(), "From and To are same", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String apiUrl = Api.EXCHANERATEURL.replace("{currencies}", fromCurrency + "," + toCurrency);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject responseJson = new JSONObject(response);
                        String endDateStr = responseJson.getString("date");
                        if (!endDateStr.equals("")) {
                            Date endDateTime = dateFormat.parse(endDateStr);
                            float startDateTime = endDateTime.getTime() - 6*86400000.0f;
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis((long) startDateTime);
                            String startDateStr = dateFormat.format(calendar.getTime());
                            getHistoricalData(startDateStr, endDateStr);
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Api Failed !", Toast.LENGTH_SHORT).show();
                    Log.i("info", error.toString());
                }
            });
            queue.add(stringRequest);
        }
    }

    private void getHistoricalData(String startDate, String endDate) {
        if(toCurrency.equals("") || fromCurrency.equals("")) {
            return;
        }
        if(startDate.equals("") || endDate.equals("")) {
            return;
        }
        String apiUrl = Api.HISTORICALURL.replace("{currencies}", fromCurrency + "," + toCurrency);
        apiUrl = apiUrl.replace("{start_at}", startDate).replace("{end_at}", endDate);
        Log.i("api link:", apiUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJson = new JSONObject(response);
                    JSONObject ratesJson = responseJson.getJSONObject("rates");
                    ArrayList<Entry> historicalData = new ArrayList<Entry>();
                    Iterator<String> iterator = ratesJson.keys();
                    int yMax = 10;
                    while(iterator.hasNext()) {
                        String dateStr = iterator.next();
                        Long dateTime = TimeUnit.MILLISECONDS.toHours(dateFormat.parse(dateStr).getTime());
                        JSONObject rate = ratesJson.getJSONObject(dateStr);
                        float rateVal = calculateRate(rate);
                        if ( rateVal > yMax) {
                            yMax = (int) (rateVal * 1.2);
                        }
                        historicalData.add(new Entry(dateTime, rateVal));
                    }
                    Collections.sort(historicalData, new Comparator<Entry>() {
                        @Override
                        public int compare(Entry entry, Entry t1) {
                            return (int) (entry.getX() - t1.getX());
                        }
                    });
                    setData(historicalData, yMax);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Api Failed !", Toast.LENGTH_SHORT).show();
                Log.i("info", error.toString());
            }
        });
        queue.add(stringRequest);
    }

    private  float calculateRate(JSONObject rate) throws JSONException {
        float fromRate =  Float.parseFloat(rate.getString(fromCurrency));
        float toRate = Float.parseFloat(rate.getString(toCurrency));
        float rateVal = (float) (Math.round((toRate * 100) / fromRate) * 0.01);

        return rateVal;
    }

    private void setData(ArrayList<Entry> chartData, int yMax) {
        Log.i("yMax", yMax + "");
        Log.i("data", chartData.toString());

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(yMax);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(179, 122, 0));
        leftAxis.setDrawAxisLine(true);
        leftAxis.setTextSize(12);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        LineDataSet set1 = new LineDataSet(chartData, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(3.0f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        lineChart.setData(data);
        lineChart.invalidate();
    }
}
