package com.kidist.bereket.weatherandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

public class TodayWeatherActivity extends AppCompatActivity {

    private LocationManager locationManager = null;

    public static Context context;
    public String cityName = "";
    public Activity parent;

    public TextView tvwCurrentCityName;
    public boolean isLoadedOnce;

    public ProgressBar progressBarLoading;
    public ImageView imgvwCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_weather);

        tvwCurrentCityName = (TextView)findViewById(R.id.tvwCurrentCityName);
        progressBarLoading = (ProgressBar)findViewById(R.id.progressBarLoading);
        imgvwCondition = (ImageView)findViewById(R.id.imgvwCondition);

        context = this;
        parent = this;

        isLoadedOnce = false;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        cityName = preferences.getString("CurrentCity", "");

        progressBarLoading.setVisibility(View.INVISIBLE);
        imgvwCondition.setVisibility(View.INVISIBLE);

        GetGPSLocation();
    }

    public void ShowForecastActivity(View button){
        Intent intent=new Intent(TodayWeatherActivity.this,ForecastWeatherActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                        permissions, grantResults);

        if (requestCode == 100) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(this,
                        "Location Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();

                GetGPSLocation();
            }
            else {
                Toast.makeText(this,
                        "Location Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void GetGPSLocation() {
        try {
            boolean flag = IsGPSAvailable();

            if (flag) {
                progressBarLoading.setVisibility(View.VISIBLE);
                imgvwCondition.setVisibility(View.GONE);

                LocationListener locationListener = new MyLocationListener();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                                    this,
                                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION
                                    , Manifest.permission.ACCESS_COARSE_LOCATION},
                                    100);
                    return;
                }

                locationManager.requestLocationUpdates(LocationManager
                        .GPS_PROVIDER, 5000, 10, locationListener);
            } else {
                progressBarLoading.setVisibility(View.GONE);
                ShowMessage("GPS Status!!", "Your GPS is: OFF", "Turn GPS On", "Cancel");
                //Toast.makeText(this, "Your GPS is off, Make sure it is turned ON!", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e){
            progressBarLoading.setVisibility(View.GONE);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Boolean IsGPSAvailable() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();

        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,LocationManager.GPS_PROVIDER);

        if (gpsStatus) {
            return true;
        } else {
            return false;
        }
    }

    protected void ShowMessage(String title, String content, String positiveButtonText, String negativeButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(content)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton(positiveButtonText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);

                                dialog.cancel();
                            }
                        })
                .setNegativeButton(negativeButtonText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void ShowProgress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setView(R.layout.progress_layout);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void LoadTemperatureInformation(String givenText) {

        try {
            if(!isLoadedOnce){

                JSONObject object = (JSONObject) new JSONTokener(givenText).nextValue();

                JSONObject currentWeather = object.getJSONObject("current");

                TextView tvwTemperatureC = (TextView)findViewById(R.id.tvwTemperatureC);
                TextView tvwTemperatureF = (TextView)findViewById(R.id.tvwTemperatureF);
                TextView tvwFeelsLikeC = (TextView)findViewById(R.id.tvwFeelsLikeC);
                TextView tvwFeelsLikeF = (TextView)findViewById(R.id.tvwFeelsLikeF);

                TextView tvwIsDayOrNight = (TextView)findViewById(R.id.tvwIsDayOrNight);
                TextView tvwConditionText = (TextView)findViewById(R.id.tvwConditionText);
                TextView tvwHumidity = (TextView)findViewById(R.id.tvwHumidity);
                TextView tvwAlert = (TextView)findViewById(R.id.tvwAlert);

                tvwTemperatureC.setText(tvwTemperatureC.getText() + currentWeather.getString("temp_c").toString());
                tvwTemperatureF.setText(tvwTemperatureF.getText() + currentWeather.getString("temp_f").toString());
                tvwFeelsLikeC.setText(tvwFeelsLikeC.getText() + currentWeather.getString("feelslike_c").toString());
                tvwFeelsLikeF.setText(tvwFeelsLikeF.getText() + currentWeather.getString("feelslike_f").toString());

                tvwIsDayOrNight.setText(tvwIsDayOrNight.getText() + currentWeather.getString("is_day").toString());
                tvwConditionText.setText(tvwConditionText.getText() + currentWeather.getString("temp_c").toString());
                tvwHumidity.setText(tvwHumidity.getText() + currentWeather.getString("humidity").toString());

                String alert = object.getString("alert");
                if(!object.getString("alert").equals("{}")){
                    tvwAlert.setText(tvwAlert.getText() + object.getString("alert").toString());
                }
                else{
                    tvwAlert.setText(tvwAlert.getText() + "Nothing specific");
                }

                String condition = currentWeather.getString("condition").toString();
                JSONObject conditionWeather = (JSONObject) new JSONTokener(condition).nextValue();

                ImageView imgvwCondition = findViewById(R.id.imgvwCondition);

                String imageUrl = "http:" + conditionWeather.getString("icon").toString();


                Drawable drawable = new RetrieveWeatherImage().execute(imageUrl).get();
                imgvwCondition.setImageDrawable(drawable);

                isLoadedOnce = true;
                imgvwCondition.setVisibility(View.VISIBLE);
                progressBarLoading.setVisibility(View.GONE);
            }
        }
        catch(Exception ex){
            progressBarLoading.setVisibility(View.GONE);
            Log.d("error", ex.getMessage());
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc)
        {
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc
                        .getLongitude(), 1);

                if (addresses.size() > 0){
                    cityName = addresses.get(0).getLocality();

                    String buttonText = tvwCurrentCityName.getText() + " (" + cityName + ")";
                    tvwCurrentCityName.setText(buttonText);

                    SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);

                    if(!cityName.equals(preferences.getString("CurrentCity", ""))){
                        String result = new ReadAPI().execute().get();

//                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.putString("CurrentCity", cityName);
//                        editor.apply();
                    }
                    else{
                        LoadTemperatureInformation(preferences.getString("WeatherContent", ""));
                    }
                }
            } catch (IOException e) {
                progressBarLoading.setVisibility(View.GONE);
                //Toast.makeText(context, "GPS Reading Started but failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (InterruptedException e) {
                progressBarLoading.setVisibility(View.GONE);
                e.printStackTrace();
            } catch (ExecutionException e) {
                progressBarLoading.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }
    }

    private class RetrieveWeatherImage extends AsyncTask<String, Void, Drawable> {

        private Exception exception;

        protected Drawable doInBackground(String... urls) {
            try {
                try {
                    InputStream is = (InputStream) new URL(urls[0]).getContent();
                    Drawable drawable = Drawable.createFromStream(is, "src name");

                    return drawable;
                } catch (Exception e) {
                    Log.d("error", e.getMessage());
                    return null;
                }
            } catch (Exception e) {
                this.exception = e;

                return null;
            } finally {

            }
        }
    }

    private class ReadAPI extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            if(!cityName.equals("")){
                String query = "http://api.weatherapi.com/v1/forecast.json?key=bb98c750c15845f3b1e201038200803&q=" + cityName + "&days=3";

                try {
                    URL url = new URL(query);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }

                        bufferedReader.close();
                        return stringBuilder.toString();
                    }
                    catch(Exception e) {
                        Toast.makeText(context, "ReadAPI: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return null;
                    }
                    finally{
                        urlConnection.disconnect();
                    }
                }
                catch(Exception e) {
                    Toast.makeText(context, "ReadAPI: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return null;
                }
            }
            else{
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("WeatherContent", response);
            editor.apply();

            LoadTemperatureInformation(response);

            //progressBar.setVisibility(View.GONE);
            //Log.i("INFO", response);
            //responseView.setText(response);
        }
    }
}
