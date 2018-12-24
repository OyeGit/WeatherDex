package com.weatherdex.oye.weatherdex.Data.SQLLiteDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.Date;
// Class is used to access database

@Dao
public interface WeatherDAO {

    // Gets the weather for a single day
    @Query("SELECT * FROM weather WHERE date = :date")
    WeatherEntry getWeatherByDate(Date date);

    // Insert a list of weather Entries into the Weather table
    @Insert(onConflict = OnConflictStrategy.REPLACE)

    // Inserts any number of weather entry objects
    void bulkInsert(WeatherEntry... weather);
}
