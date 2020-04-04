package com.kidist.bereket.weatherandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ForecastWeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_weather);
    }

    public void ShowTodayActivity(View button){
        Intent intent=new Intent(ForecastWeatherActivity.this,TodayWeatherActivity.class);
        startActivity(intent);
    }
}
