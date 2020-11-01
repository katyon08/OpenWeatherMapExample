package com.android.lessons.myapplication.models;

import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.kwabenaberko.openweathermaplib.models.threehourforecast.ThreeHourForecast;

public class Forecast {
    public String description;
    public String temperature;
    public String windSpeed;
    public String city;
    public String country;

    public static Forecast FromCurrentWeather(CurrentWeather currentWeather) {
        Forecast f = new Forecast();
        f.city = currentWeather.getName();
        f.country = currentWeather.getSys().getCountry();
        f.description = currentWeather.getWeather().get(0).getDescription();
        f.windSpeed = String.valueOf(currentWeather.getWind().getSpeed());
        f.temperature = currentWeather.getMain().getTempMin()
                + "/" + currentWeather.getMain().getTempMax();
        return f;
    }

    private Forecast() {
    }

    public Forecast(String description, String temperature, String windSpeed, String city, String country) {
        this.description = description;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.city = city;
        this.country = country;
    }

    public static Forecast FromHoursForecast(ThreeHourForecast threeHourForecast) {
        Forecast f = new Forecast();
        f.city = threeHourForecast.getCity().getName();
        f.country = threeHourForecast.getCity().getCountry();
        f.description = threeHourForecast.getList().get(0).getWeatherArray().get(0).getDescription();
        f.windSpeed = String.valueOf(threeHourForecast.getList().get(0).getWind().getSpeed());
        f.temperature = threeHourForecast.getList().get(0).getMain().getTempMin()
                + "/" + threeHourForecast.getList().get(0).getMain().getTempMax();
        return f;
    }
}
