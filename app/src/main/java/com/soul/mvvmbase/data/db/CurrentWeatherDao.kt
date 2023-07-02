package com.soul.mvvmbase.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soul.mvvmbase.data.bean.CURRENT_WEATHER_ID
import com.soul.mvvmbase.data.bean.CurrentWeather

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(currentWeather: CurrentWeather)

    @Query(value = "select * from current_weather where id = $CURRENT_WEATHER_ID")
    fun getCurrentWeather():LiveData<CurrentWeather>
}