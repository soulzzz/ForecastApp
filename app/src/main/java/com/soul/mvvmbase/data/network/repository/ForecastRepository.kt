package com.soul.mvvmbase.data.network.repository

import androidx.lifecycle.LiveData
import com.soul.mvvmbase.data.bean.CurrentWeather

interface ForecastRepository {
    suspend fun getCurrentWeather(location: String = "101010100"):LiveData<CurrentWeather>
}