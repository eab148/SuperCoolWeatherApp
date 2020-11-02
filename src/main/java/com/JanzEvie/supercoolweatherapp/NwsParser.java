package com.JanzEvie.supercoolweatherapp;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;

public class NwsParser {
    private static final String baseUrl = "https://api.weather.gov/points/";

    public static void main(String[] args) {
        HttpResponse<JsonNode> response = Unirest.get("https://www.ncdc.noaa.gov/cdo-web/api/v2/datasets").header("token",
                "GTqWAAnltwSVYYFVRhvliewPLELyPnMQ").asJson();
        JsonNode test = response.getBody();
        System.out.println(test.toPrettyString());
    }

    public static Forecast[] getSevenDayForecast(String address) {
        JSONArray forecastsJson = getForecastJsonArray(address, "forecast");
        Forecast[] forecasts = new Forecast[forecastsJson.length()];
        for (int i = 0; i < forecastsJson.length(); i++) {
            Forecast forecast = new Forecast(forecastsJson.getJSONObject(i));
            forecasts[ i ] = forecast;
        }
        return forecasts;
    }

    public static Forecast getCurrentWeather(String address) {
        JSONArray forecastsJson = getForecastJsonArray(address, "forecastHourly");
        return new Forecast(forecastsJson.getJSONObject(0));
    }

    private static JSONArray getForecastJsonArray(String address, String forecastType) {
        GeoCoords coords = new GeoCoords(address);
        HttpResponse<JsonNode> response = Unirest.get(baseUrl + coords.toString()).asJson();
        if (response.getStatus() != 200) {
            throw new RuntimeException("HTTP Code " + response.getStatus() + ": " + response.getStatusText());
        }
        response = Unirest.get(response.getBody().getObject().getJSONObject("properties").getString(forecastType)).asJson();
        JSONObject responseJson = response.getBody().getObject();
        return responseJson.getJSONObject("properties").getJSONArray("periods");
    }
}