package com.kidist.bereket.weatherandroidapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.provider.Settings;
import android.view.View;
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
import java.util.List;
import java.util.Locale;

public class TodayWeatherActivity extends AppCompatActivity {

    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;

    public static Context context;
    public String cityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_weather);

        context = this;

        locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Toast.makeText(this, "started", Toast.LENGTH_LONG).show();
        GetGPSLocation();

        //String imageUrl = "http://cdn.weatherapi.com/weather/64x64/day/296.png";
        //new RetrieveWeatherImage().execute(imageUrl);
    }

    public void GetGPSLocation() {
        boolean flag = IsGPSAvailable();
        if (flag) {
            locationListener = new MyLocationListener();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationMangaer.requestLocationUpdates(LocationManager
                    .GPS_PROVIDER, 5000, 10, locationListener);
        } else {
            ShowMessage("GPS Status!!", "Your GPS is: OFF", "Turn GPS On", "Cancel");
            //Toast.makeText(this, "Your GPS is off, Make sure it is turned ON!", Toast.LENGTH_LONG).show();
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
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
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

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();

            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc
                        .getLongitude(), 1);

                if (addresses.size() > 0)
                    cityName = addresses.get(0).getLocality();

                new ReadAPI().execute();
                Toast.makeText(getBaseContext(), cityName, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
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
                    Toast.makeText(context, urls[0], Toast.LENGTH_LONG).show();
                    InputStream is = (InputStream) new URL(urls[0]).getContent();
                    Drawable drawable = Drawable.createFromStream(is, "src name");

                    ImageView imgvwCondition = findViewById(R.id.imgvwCondition);

                    if(drawable == null){
                        Toast.makeText(context, "image is null", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(context, "image set", Toast.LENGTH_LONG).show();
                    }

                    imgvwCondition.setImageDrawable(drawable);

                    return drawable;
                } catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    return null;
                }
            } catch (Exception e) {
                this.exception = e;
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
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
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                //Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            JSONObject object = null;
            try {
                object = (JSONObject) new JSONTokener(response).nextValue();

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
                tvwAlert.setText(tvwAlert.getText() + currentWeather.getString("alert").toString());

                Toast.makeText(context, currentWeather.getString("temp_c").toString(), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                //Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
            //progressBar.setVisibility(View.GONE);
            //Log.i("INFO", response);
            //responseView.setText(response);
        }
    }
}
