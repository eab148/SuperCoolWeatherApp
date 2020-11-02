package com.JanzEvie.supercoolweatherapp;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class GeoCoords {
    public Float lat;
    public Float lng;
    private static final String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";

    public GeoCoords(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }
    // Takes in an address in correct Google api format e.g.
    // 1600+Amphitheatre+Parkway,+Mountain+View,+CA
    // and uses Google's Geocoding API to turn it into a lat/long
    public GeoCoords(String address) {
        JSONObject responseJson = getGeocodeJson(address);
        if (responseJson.getJSONArray("results").length() == 0) {
            throw new RuntimeException("Address not found or invalid.");
        }
        JSONArray address_components = responseJson.getJSONArray("results").getJSONObject(0).
                getJSONArray("address_components");
        for (int i = 0; i < address_components.length(); i++) {
            JSONArray types = address_components.getJSONObject(i).getJSONArray("types");
            for (int j = 0; j < types.length(); j++) {
                if (types.getString(j) == "country") {
                    if (address_components.getJSONObject(i).getString("short_name") != "US") {
                        throw new IllegalArgumentException("Entered location nost in the United States.\nTry again, commie.\n" +
                                "You entered " + address_components.getJSONObject(i).getString("short_name"));
                    }
                }
            }
        }
        JSONObject coordsJson = responseJson.getJSONArray("results").getJSONObject(0).
                getJSONObject("geometry").getJSONObject("location");
        lat = coordsJson.getFloat("lat");
        lng = coordsJson.getFloat("lng");
    }

    public static String getFullLocation(String address) {
        JSONObject responseJson = getGeocodeJson(address);
        return responseJson.getJSONArray("results").getJSONObject(0).getString("formatted_address");
    }

    private static JSONObject getGeocodeJson(String address) {
        HttpResponse<JsonNode> response = Unirest.get(baseUrl).
                queryString("key", ApiKey.key).
                queryString("address", address).asJson();
        return response.getBody().getObject();
    }

    @Override
    public String toString() {
        return lat.toString() + "," + lng.toString();
    }
}
