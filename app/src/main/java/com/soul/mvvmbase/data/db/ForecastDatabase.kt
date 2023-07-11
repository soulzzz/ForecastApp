package com.soul.mvvmbase.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.soul.mvvmbase.data.bean.CurrentWeather
import com.soul.mvvmbase.data.bean.DailyWeather
import com.soul.mvvmbase.data.bean.WeatherLocation
import com.soul.mvvmbase.util.LocalDateConverter

@Database(entities = [CurrentWeather::class,WeatherLocation::class,DailyWeather::class],
    version = 1
)
@TypeConverters(LocalDateConverter::class)
abstract class ForecastDatabase : RoomDatabase() {
    abstract fun currentWeatherDao():CurrentWeatherDao
    abstract fun weatherLocationDao():WeatherLocationDao
    abstract fun dailyWeatherDao():DailyWeatherDao
    companion object{
        @Volatile
        private var instance:ForecastDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it }
        }
        private fun buildDatabase(context:Context) =
            Room.databaseBuilder(context.applicationContext,
                ForecastDatabase::class.java,"forecast.db")
                .build()
    }
}