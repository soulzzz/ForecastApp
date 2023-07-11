package com.soul.mvvmbase.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soul.mvvmbase.data.bean.DailyWeather
import org.threeten.bp.LocalDate
@Dao
interface DailyWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(futureWeatherEntries: List<DailyWeather>)

    @Query("select * from daily_weather where date(fxDate) >= date(:startDate)")
    fun getWeatherForecasts(startDate: LocalDate): LiveData<List<DailyWeather>>


    @Query("select count(id) from daily_weather where date(fxDate) >= date(:startDate)")
    fun countFutureWeather(startDate: LocalDate): Int

    @Query("delete from daily_weather where date(fxDate) < date(:firstDateToKeep)")
    fun deleteOldEntries(firstDateToKeep: LocalDate)
}