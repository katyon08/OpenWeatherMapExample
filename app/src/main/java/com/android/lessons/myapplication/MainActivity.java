package com.android.lessons.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.ThreeHourForecastCallback;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.kwabenaberko.openweathermaplib.models.threehourforecast.ThreeHourForecast;

public class MainActivity extends AppCompatActivity {
    OpenWeatherMapHelper weatherHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareRadioListeners();
        weatherHelper = new OpenWeatherMapHelper(getString(R.string.OPEN_WEATHER_MAP_API_KEY));
        UpdateWeather();
    }

    private void prepareRadioListeners() {
        ((RadioGroup) findViewById(R.id.radios)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                UpdateWeather(GetSelectedWeatherType());
            }
        });
    }

    private void UpdateWeather() {
        GetSelectedWeatherType();
    }

    private WeatherType GetSelectedWeatherType()
    {
        int selectedId = ((RadioGroup) findViewById(R.id.radios)).getCheckedRadioButtonId();
        return GetSelectedWeatherType(selectedId);
    }

    private WeatherType GetSelectedWeatherType(int checkedRadioButtonId) {
        WeatherType selected;
        switch (checkedRadioButtonId) {
            case R.id.current:
                selected = WeatherType.Current;
                break;
            case R.id.hours:
                selected = WeatherType.Hourly;
                break;
            case R.id.days:
                selected = WeatherType.Daily;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(checkedRadioButtonId));
        }
        return selected;
    }

//    private RadioGroup GetSelectedWeatherType(RadioGroup viewById) {
//    }

    private void UpdateWeather(WeatherType selected) {

        String cityName = ((TextView)findViewById(R.id.cityName)).getText().toString();

        switch (selected) {
            case Current:
                getCurrentWeather(cityName);
                break;
            case Hourly:
                getHoursWeather(cityName);
                break;
            case Daily:
                getDaysWeather(cityName);
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(selected));
        }
    }

    private void getDaysWeather(String city) {
        setWeatherOnUi("16 day / daily forecast is not implemented yet");
    }

    private void getHoursWeather(String city) {
        weatherHelper.getThreeHourForecastByCityName(city, new ThreeHourForecastCallback() {
        @Override
        public void onSuccess(ThreeHourForecast threeHourForecast) {
            String weatherText = "City/Country: "+ threeHourForecast.getCity().getName() + "/" + threeHourForecast.getCity().getCountry() +"\n"
                    +"Forecast Array Count: " + threeHourForecast.getCnt() +"\n"
                    //For this example, we are logging details of only the first forecast object in the forecasts array
                    +"First Forecast Date Timestamp: " + threeHourForecast.getList().get(0).getDt() +"\n"
                    +"First Forecast Weather Description: " + threeHourForecast.getList().get(0).getWeatherArray().get(0).getDescription()+ "\n"
                    +"First Forecast Max Temperature: " + threeHourForecast.getList().get(0).getMain().getTempMax()+"\n"
                    +"First Forecast Wind Speed: " + threeHourForecast.getList().get(0).getWind().getSpeed() + "\n";

            setWeatherOnUi(weatherText);
        }

        @Override
        public void onFailure(Throwable throwable) {
            doOnWeatherFailure(throwable);
        }
    });
    }

    private void getCurrentWeather(String city) {
        weatherHelper.getCurrentWeatherByCityName(city, new CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                String weatherText = "Coordinates: " + currentWeather.getCoord().getLat() + ", "+currentWeather.getCoord().getLon() +"\n"
                        +"Weather Description: " + currentWeather.getWeather().get(0).getDescription() + "\n"
                        +"Temperature: " + currentWeather.getMain().getTempMax()+"\n"
                        +"Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
                        +"City, Country: " + currentWeather.getName() + ", " + currentWeather.getSys().getCountry();

                setWeatherOnUi(weatherText);
            }

            @Override
            public void onFailure(Throwable throwable) {
                doOnWeatherFailure(throwable);
            }
        });
    }

    private void doOnWeatherFailure(Throwable throwable) {
        ((TextView)findViewById(R.id.text)).setText(throwable.getMessage());
    }

    private void setWeatherOnUi(String weatherText) {
        ((TextView)findViewById(R.id.text)).setText(weatherText);
    }

    public void goWeather(View view) {
        UpdateWeather(GetSelectedWeatherType());
    }


    public enum WeatherType {
        Current,
        Hourly,
        Daily
    }

}