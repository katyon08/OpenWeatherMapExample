package com.android.lessons.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.lessons.myapplication.helper.DBForecastHelper;
import com.android.lessons.myapplication.models.Forecast;
import com.kwabenaberko.openweathermaplib.constants.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.ThreeHourForecastCallback;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.kwabenaberko.openweathermaplib.models.threehourforecast.ThreeHourForecast;

public class MainActivity extends AppCompatActivity {
    private OpenWeatherMapHelper weatherHelper;
    private DBForecastHelper dbForecastHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareRadioListeners();
        weatherHelper = new OpenWeatherMapHelper(getString(R.string.OPEN_WEATHER_MAP_API_KEY));
        weatherHelper.setUnits(Units.METRIC);
        dbForecastHelper =
                new DBForecastHelper(this);
        Forecast f = dbForecastHelper.getLastForecast();
        if (f != null) {
            setWeatherOnUi(f);
        }

    }

    private void prepareRadioListeners() {
        RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                WeatherType selectedType = GetSelectedWeatherType();
                UpdateWeather(selectedType);
            }
        };

        ((RadioGroup) findViewById(R.id.radios))
                .setOnCheckedChangeListener(listener);
    }

    private void UpdateWeather() {
        WeatherType type = GetSelectedWeatherType();
        UpdateWeather(type);
    }

    private WeatherType GetSelectedWeatherType() {
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

        String cityName = ((TextView) findViewById(R.id.cityName)).getText().toString();

        if (cityName.equals("")){
            Toast.makeText(getApplicationContext(), "No city provided", Toast.LENGTH_LONG).show();
            return;
        }

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
        setWeatherOnUi(new Forecast("Not Implemented Yet", "", "", "", ""));
    }

    private void getHoursWeather(String city) {
        weatherHelper.getThreeHourForecastByCityName(city, new ThreeHourForecastCallback() {
            @Override
            public void onSuccess(ThreeHourForecast threeHourForecast) {
                handleNewForecast(Forecast.FromHoursForecast(threeHourForecast));
            }

            @Override
            public void onFailure(Throwable throwable) {
                doOnWeatherFailure(throwable);
            }
        });
    }

    private void handleNewForecast(Forecast forecast) {
        setWeatherOnUi(forecast);
        dbForecastHelper.saveForecast(forecast);
    }

    private void getCurrentWeather(String city) {
        weatherHelper.getCurrentWeatherByCityName(city, new CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                handleNewForecast(Forecast.FromCurrentWeather(currentWeather));
            }

            @Override
            public void onFailure(Throwable throwable) {
                doOnWeatherFailure(throwable);
            }
        });
    }

    private void doOnWeatherFailure(Throwable throwable) {
        ((TextView) findViewById(R.id.text)).setText(throwable.getMessage());
    }

    private void setWeatherOnUi(Forecast f) {
        ((TextView) findViewById(R.id.text_city)).setText(f.city);
        ((TextView) findViewById(R.id.text_country)).setText(f.country);
        ((TextView) findViewById(R.id.text_wind)).setText(f.windSpeed);
        ((TextView) findViewById(R.id.text_temp)).setText(f.temperature);
        ((TextView) findViewById(R.id.text_description)).setText(f.description);
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