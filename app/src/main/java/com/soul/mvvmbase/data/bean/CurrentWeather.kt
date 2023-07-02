package com.soul.mvvmbase.data.bean


import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class CurrentWeather(
    val cloud: String,
    val dew: String,
    val feelsLike: String,
    val humidity: String,
    val icon: String,
    val obsTime: String,
    val precip: String,
    val pressure: String,
    val temp: String,
    val text: String,
    val vis: String,
    val wind360: String,
    val windDir: String,
    val windScale: String,
    val windSpeed: String
){
    @PrimaryKey(autoGenerate = false)
    var id:Int =CURRENT_WEATHER_ID

}