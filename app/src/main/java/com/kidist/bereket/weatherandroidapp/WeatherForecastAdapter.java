package com.kidist.bereket.weatherandroidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WeatherForecastAdapter extends ArrayAdapter<WeatherModel> {
    public WeatherForecastAdapter(Context context, ArrayList<WeatherModel> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        WeatherModel weatherModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.weather_forecast_list, parent, false);
        }

        // Lookup view for data population
        TextView tvwForecastDate = (TextView) convertView.findViewById(R.id.tvwForecastDate);
        TextView tvwTemperatureC = (TextView) convertView.findViewById(R.id.tvwTemperatureC);
        TextView tvwTemperatureF = (TextView) convertView.findViewById(R.id.tvwTemperatureF);
        TextView tvwFeelsLikeC = (TextView) convertView.findViewById(R.id.tvwFeelsLikeC);
        TextView tvwFeelsLikeF = (TextView) convertView.findViewById(R.id.tvwFeelsLikeF);

        TextView tvwSunrise = (TextView) convertView.findViewById(R.id.tvwSunrise);
        TextView tvwSunset = (TextView) convertView.findViewById(R.id.tvwSunset);
        TextView tvwMoonrise = (TextView) convertView.findViewById(R.id.tvwMoonrise);
        TextView tvwMoonset = (TextView) convertView.findViewById(R.id.tvwMoonset);

        TextView tvwAlert = (TextView) convertView.findViewById(R.id.tvwAlert);

        // Populate the data into the template view using the data object
        tvwForecastDate.setText(weatherModel.getForecastDate());
        tvwTemperatureC.setText(weatherModel.getTemperatureC());
        tvwTemperatureF.setText(weatherModel.getTemperatureF());
        tvwFeelsLikeC.setText(weatherModel.getFeelslikeC());
        tvwFeelsLikeF.setText(weatherModel.getFeelslikeF());

        tvwSunrise.setText(weatherModel.getSunrise());
        tvwSunset.setText(weatherModel.getSunset());
        tvwMoonrise.setText(weatherModel.getMoonrise());
        tvwMoonset.setText(weatherModel.getMoonset());

        tvwAlert.setText(weatherModel.getAlert());

        // Return the completed view to render on screen
        return convertView;
    }
}