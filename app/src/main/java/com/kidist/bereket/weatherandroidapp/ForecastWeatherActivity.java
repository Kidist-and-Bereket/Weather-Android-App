package com.kidist.bereket.weatherandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class ForecastWeatherActivity extends AppCompatActivity {

    ListView lstvwWeatherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_weather);

        lstvwWeatherList = (ListView) findViewById(R.id.lstvwWeatherList);

        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String content = preferences.getString("WeatherContent", "");

        ArrayList<WeatherModel> weatherModels = new ArrayList<WeatherModel>();
        weatherModels = GetWeatherForecasts(content);

        // Create the adapter to convert the array to views
        WeatherForecastAdapter adapter = new WeatherForecastAdapter(this, weatherModels);

        lstvwWeatherList.setAdapter(adapter);
    }

    public static ArrayList<WeatherModel> ConvertJSONArrayToArrayList(JSONArray jArr)
    {
        ArrayList<WeatherModel> list = new ArrayList<WeatherModel>();
        try {
            for (int i=0, l=jArr.length(); i<l; i++){
                WeatherModel weatherModel = new WeatherModel();
                //weatherModel.setTemperatureC(jArr.get(i));

                list.add((WeatherModel)jArr.get(i));
            }
        } catch (JSONException e) {}

        return list;
    }

    private ArrayList<WeatherModel> GetWeatherForecasts(String forecastContent){
        try {
            JSONObject object = (JSONObject) new JSONTokener(forecastContent).nextValue();
            JSONObject forecastWeather = object.getJSONObject("forecast");

            JSONArray forecastArray = forecastWeather.toJSONArray(forecastWeather.names());

            ArrayList<WeatherModel> weatherModelArrayList = new ArrayList<>();

            JSONArray array = forecastArray.getJSONArray(0);
            for (int i = 0; i < array.length(); i++){
                JSONObject singleForecast = array.getJSONObject(i);
                WeatherModel weatherModel = new WeatherModel();

                weatherModel.setForecastDate("Date: " + singleForecast.getString("date"));

                JSONObject singleForecastDay = singleForecast.getJSONObject("day");

                weatherModel.setTemperatureC("Max in C: " + singleForecastDay.getString("maxtemp_c"));
                weatherModel.setTemperatureF("Max in F: " + singleForecastDay.getString("maxtemp_f"));
                weatherModel.setFeelslikeC("Min in C: " + singleForecastDay.getString("mintemp_c"));
                weatherModel.setFeelslikeF("Min in F: " + singleForecastDay.getString("mintemp_f"));

                JSONObject singleForecastDayCondition = singleForecastDay.getJSONObject("condition");
                weatherModel.setAlert("Condition: " + singleForecastDayCondition.getString("text"));

                JSONObject singleForecastAstro = singleForecast.getJSONObject("astro");

                weatherModel.setSunrise("Sunrise at: " + singleForecastAstro.getString("sunrise"));
                weatherModel.setSunset("Sunset at: " + singleForecastAstro.getString("sunset"));
                weatherModel.setMoonrise("Moonrise at: " + singleForecastAstro.getString("moonrise"));
                weatherModel.setMoonset("Sunset at: " + singleForecastAstro.getString("moonset"));

                weatherModelArrayList.add(weatherModel);
            }

            //ArrayList<WeatherModel> weatherModelArrayList = ConvertJSONArrayToArrayList(forecastArray);
            return  weatherModelArrayList;
        }
        catch (Exception ex){
            return null;
        }
    }

    public void ShowTodayActivity(View button){
        Intent intent=new Intent(ForecastWeatherActivity.this,TodayWeatherActivity.class);
        startActivity(intent);
    }
}
