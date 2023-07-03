package com.soul.mvvmbase.data.db

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soul.mvvmbase.data.bean.WEATHER_LOCATION_ID
import com.soul.mvvmbase.data.bean.WeatherLocation

interface WeatherLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(weatherLocation: WeatherLocation)

    @Query(value = "select * from current_weather where id = $WEATHER_LOCATION_ID")
    fun getWeatherLocation(): LiveData<WeatherLocation>
}