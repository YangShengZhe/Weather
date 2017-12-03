package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Padge on 2017/11/29.
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
