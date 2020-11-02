package com.JanzEvie.supercoolweatherapp;

import kong.unirest.json.JSONObject;

public class Forecast {
    public String name;
    public String startTime;
    public String endTime;
    public boolean isDayTime;
    public int temperature;
    public String temperatureUnit;
    public String temperatureTrend;
    public String windSpeed;
    public String windDirection;
    public String icon;
    public String shortForecast;
    public String detailedForecast;

    public Forecast(JSONObject forecast) {
        name = forecast.getString("name");
        startTime = forecast.getString("startTime");
        endTime = forecast.getString("endTime");
        isDayTime = forecast.getBoolean("isDaytime");
        temperature = forecast.getInt("temperature");
        temperatureUnit = forecast.getString("temperatureUnit");
        windSpeed = forecast.getString("windSpeed");
        windDirection = forecast.getString("windDirection");
        icon = forecast.getString("icon");
        shortForecast = forecast.getString("shortForecast");
        detailedForecast = forecast.getString("detailedForecast");
    }

    @Override
    public String toString() {
        return name + ": " + temperature + "°" + temperatureUnit;
    }
}
