package com.soul.mvvmbase.data.network.response

import com.google.gson.annotations.SerializedName
import com.soul.mvvmbase.data.bean.DailyWeather

data class FutureWeatherResponse(
    val code: String,
    @SerializedName("daily")
    val dailyWeather: List<DailyWeather>,
    val fxLink: String,
    val updateTime: String
)