package com.duylong.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.duylong.currencyconverter.adapter.CustomAdapter;
import com.duylong.currencyconverter.util.Api;
import com.tiper.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    RequestQueue queue;



    String toCurrency = "";
    String fromCurrency = "";
    Double currencyRate = 0.0;
    Double exchangeAmount;

    TextView currencyRateTxt;
    EditText amountEditText;
    Button convertBtn;
    MaterialSpinner fromSpinner, toSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        fromSpinner = (MaterialSpinner) findViewById(R.id.fromCurrency);
        toSpinner = (MaterialSpinner) findViewById(R.id.toCurrency);

        currencyRateTxt = (TextView) findViewById(R.id.currencyRate);

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), R.layout.custom_spinner_item, Api.COUNTRIES, Api.FLAGS);

        fromSpinner.setAdapter(customAdapter);
        toSpinner.setAdapter(customAdapter);

        fromSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner materialSpinner) {

            }

            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, View view, int i, long l) {
                fromCurrency = Api.COUNTRIES[i];
                calculateCurrencyRate();
            }
        });

        toSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner materialSpinner) {

            }

            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, View view, int i, long l) {
                toCurrency = Api.COUNTRIES[i];
                calculateCurrencyRate();
            }
        });

        amountEditText = (EditText) findViewById(R.id.amountEditText);
        convertBtn = (Button) findViewById(R.id.convertBtn);
        final TextView resultTxt = (TextView) findViewById(R.id.result);

        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountTxt = amountEditText.getText().toString();
                if (amountTxt.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter exchange amount", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int amount = Integer.parseInt(amountTxt);
                        exchangeAmount = amount * currencyRate;
                        if ((exchangeAmount == Math.floor(exchangeAmount)) && !Double.isInfinite(exchangeAmount)) {
                            // integer type
                            resultTxt.setText(exchangeAmount.intValue() + " " + toCurrency);
                        } else  {
                            resultTxt.setText(exchangeAmount + " " + toCurrency);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Please enter a integer value", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        Button staticBtn = (Button) findViewById(R.id.staticBtn);
        staticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), StaticActivity.class);
                startActivity(intent);
            }
        });
    }

    private void calculateCurrencyRate() {
        if ("".equals(fromCurrency) || "".equals(toCurrency)) {
            currencyRate = 0.0;
            currencyRateTxt.setText("");
            return;
        }
        if (fromCurrency.equals(toCurrency)) {
            currencyRate = 1.0;
            currencyRateTxt.setText("1 " + fromCurrency + " = " + "1 " + toCurrency);
        } else {
            String apiUrl = Api.EXCHANERATEURL.replace("{currencies}", fromCurrency + "," + toCurrency);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    handleApiResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    currencyRate = 0.0;
                    currencyRateTxt.setText("");
                    Toast.makeText(getApplicationContext(), "Api Failed !", Toast.LENGTH_SHORT).show();
                    Log.i("info", error.toString());
                }
            });
            queue.add(stringRequest);
        }
    }

    private void handleApiResponse(String response) {
        try {
            JSONObject responseJson = new JSONObject(response);
            JSONObject rates = responseJson.getJSONObject("rates");
            Double fromRate =  Double.parseDouble(rates.getString(fromCurrency));
            Double toRate = Double.parseDouble(rates.getString(toCurrency));
            currencyRate = Math.round((toRate * 100.0) / fromRate) / 100.0;
            currencyRateTxt.setText("1 " + fromCurrency + " = " + Double.toString(currencyRate) + " " + toCurrency);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed while reading response", Toast.LENGTH_SHORT).show();
        }
    }
}