package com.soul.mvvmbase.data.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

const val WEATHER_LOCATION_ID = 0

@Entity(tableName = "weather_location")
data class WeatherLocation(
    val latitude: String,
    val longitude: String,
    val province: String,
    val coordType: String,
    val city: String,
    val district: String,
    val cityCode: String,
    val adCode: String,
    val address: String,
    val country: String,
){
    @PrimaryKey(autoGenerate = false)
    var id:Int =WEATHER_LOCATION_ID

    val location_time:ZonedDateTime
    get() {
        return ZonedDateTime.now()
    }


    constructor(city: String) : this("null","null","null","null",city,"null","null","null","null","null")
}