package com.kidist.bereket.weatherandroidapp;

public class WeatherModel {
    private String ForecastDate;
    private String TemperatureC;
    private String TemperatureF;
    private String FeelslikeC;
    private String FeelslikeF;

    private String Sunrise;
    private String Sunset;
    private String Moonrise;
    private String Moonset;

    private String Alert;

    public String getForecastDate() {
        return ForecastDate;
    }

    public void setForecastDate(String forecastDate) {
        ForecastDate = forecastDate;
    }

    public String getTemperatureC() {
        return TemperatureC;
    }

    public void setTemperatureC(String temperatureC) {
        TemperatureC = temperatureC;
    }

    public String getTemperatureF() {
        return TemperatureF;
    }

    public void setTemperatureF(String temperatureF) {
        TemperatureF = temperatureF;
    }

    public String getFeelslikeC() {
        return FeelslikeC;
    }

    public void setFeelslikeC(String feelslikeC) {
        FeelslikeC = feelslikeC;
    }

    public String getFeelslikeF() {
        return FeelslikeF;
    }

    public void setFeelslikeF(String feelslikeF) {
        FeelslikeF = feelslikeF;
    }

    public String getSunrise() {
        return Sunrise;
    }

    public void setSunrise(String sunrise) {
        Sunrise = sunrise;
    }

    public String getSunset() {
        return Sunset;
    }

    public void setSunset(String sunset) {
        Sunset = sunset;
    }

    public String getMoonrise() {
        return Moonrise;
    }

    public void setMoonrise(String moonrise) {
        Moonrise = moonrise;
    }

    public String getMoonset() {
        return Moonset;
    }

    public void setMoonset(String moonset) {
        Moonset = moonset;
    }

    public String getAlert() {
        return Alert;
    }

    public void setAlert(String alert) {
        Alert = alert;
    }
}
