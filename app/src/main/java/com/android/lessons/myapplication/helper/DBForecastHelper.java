package com.android.lessons.myapplication.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.lessons.myapplication.BuildConfig;
import com.android.lessons.myapplication.models.Forecast;

public class DBForecastHelper extends SQLiteOpenHelper {
    private final static String dbName = "MyDbOne";
    private final static String tableName = "Forecast";
    private static final int dbVersion = 1;
    private String TAG = "DBForecastHelper";

    public DBForecastHelper(Context context) {
        super(context, DBForecastHelper.dbName,
                null, DBForecastHelper.dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: " + db.getPath());

        String query = "CREATE TABLE " + tableName + "(" +
                "id integer not null primary key autoincrement, " +
                "description text, " +
                "temperature text, " +
                "windSpeed text, " +
                "city text, " +
                "country text)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void saveForecast(Forecast forecast) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues row = new ContentValues();

        row.put("description", forecast.description);
        row.put("city", forecast.city);
        row.put("country", forecast.country);
        row.put("temperature", forecast.temperature);
        row.put("windSpeed", forecast.windSpeed);

        long result = db.insert(tableName, null, row);

        if (BuildConfig.DEBUG && result <= 0) {
            throw new AssertionError("Assertion failed");
        }
    }

    public Forecast getLastForecast() {
        SQLiteDatabase db = getReadableDatabase();
        //-- Считывание данных из таблицы tableName ---------
        Cursor cursor = db.query(tableName,
                new String[]{"description", "city", "country", "temperature", "windSpeed"},
                null,
                null,
                null,
                null,
                "id DESC");

        Forecast forecast = null;
        if (cursor.moveToFirst())
        {
            int description = cursor.getColumnIndex("description");
            int city = cursor.getColumnIndex("city");
            int country = cursor.getColumnIndex("country");
            int temperature = cursor.getColumnIndex("temperature");
            int windSpeed = cursor.getColumnIndex("windSpeed");
            forecast = new Forecast(
                    cursor.getString(description),
                    cursor.getString(temperature),
                    cursor.getString(windSpeed),
                    cursor.getString(city),
                    cursor.getString(country));

        }
        cursor.close();
        return forecast;
    }
}
