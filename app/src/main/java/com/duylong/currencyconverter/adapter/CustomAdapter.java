package com.duylong.currencyconverter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.duylong.currencyconverter.R;

import java.lang.reflect.Array;

public class CustomAdapter extends ArrayAdapter {
    Context context;
    int flags[];
    String[] countryNames;
    LayoutInflater inflter;


    public CustomAdapter(@NonNull Context context, int resource, @NonNull String[] countries, @NonNull int[] flags) {
        super(context, resource, countries);
        this.context = context;
        this.countryNames = countries;
        this.flags = flags;
        this.inflter = LayoutInflater.from(context);
    }

    public View getCustomView(int position, View convertView,  ViewGroup parent) {
        // Inflating the layout for the custom Spinner
        View layout = this.inflter.inflate(R.layout.custom_spinner_item, parent, false);

        // Declaring and Typecasting the textview in the inflated layout
        TextView tvLanguage = (TextView) layout.findViewById(R.id.textView);

        tvLanguage.setText(this.countryNames[position]);


        ImageView img = (ImageView) layout.findViewById(R.id.imageView);

        img.setImageResource(this.flags[position]);

        return layout;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getCustomView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return this.getCustomView(position, convertView, parent);
    }

}
