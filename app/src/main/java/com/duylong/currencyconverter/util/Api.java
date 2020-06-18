package com.duylong.currencyconverter.util;

import com.duylong.currencyconverter.R;

public class Api {
    public final static String[] COUNTRIES = { "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "GBD", "HRK", "HUF", "IDR", "ILS", "INR", "ISK", "JPY",
            "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN", "RON", "RUB", "SEK", "USD"};

    public final static int FLAGS[] = { R.drawable.aud, R.drawable.bgn, R.drawable.brl, R.drawable.cad, R.drawable.chf, R.drawable.cny, R.drawable.czk, R.drawable.dkk, R.drawable.gbd,
            R.drawable.hrk, R.drawable.huf, R.drawable.idr, R.drawable.ils, R.drawable.inr, R.drawable.isk, R.drawable.jpy, R.drawable.krw, R.drawable.mxn,
            R.drawable.myr, R.drawable.nok, R.drawable.nzd, R.drawable.php, R.drawable.pln, R.drawable.ron, R.drawable.rub, R.drawable.sek, R.drawable.usd };

    public final static String EXCHANERATEURL = "https://api.exchangeratesapi.io/latest?symbols={currencies}";

    public final static String HISTORICALURL = "https://api.exchangeratesapi.io/history?start_at={start_at}&end_at={end_at}&symbols={currencies}";
}
