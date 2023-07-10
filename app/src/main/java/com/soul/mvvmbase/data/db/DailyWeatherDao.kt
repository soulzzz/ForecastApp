package com.soul.mvvmbase.data.db

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soul.mvvmbase.data.bean.DailyWeather
import org.threeten.bp.LocalDate

interface DailyWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(futureWeatherEntries: List<DailyWeather>)

    @Query("select * from future_weather where date(date) >= date(:startDate)")
    fun getWeatherForecasts(startDate: LocalDate): LiveData<List<DailyWeather>>


    @Query("select count(id) from future_weather where date(date) >= date(:startDate)")
    fun countFutureWeather(startDate: LocalDate): Int

    @Query("delete from future_weather where date(date) < date(:firstDateToKeep)")
    fun deleteOldEntries(firstDateToKeep: LocalDate)
}