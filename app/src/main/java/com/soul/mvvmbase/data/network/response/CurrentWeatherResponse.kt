package com.soul.mvvmbase.data.network.response

import com.google.gson.annotations.SerializedName
import com.soul.mvvmbase.data.bean.CurrentWeather


data class CurrentWeatherResponse(
    val code: String,
    val fxLink: String,
    @SerializedName("now")
    val currentWeather: CurrentWeather,
    val updateTime: String
)