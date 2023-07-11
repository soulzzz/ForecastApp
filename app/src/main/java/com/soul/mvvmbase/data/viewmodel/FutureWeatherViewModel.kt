package com.soul.mvvmbase.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.soul.mvvmbase.data.network.repository.ForecastRepository
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.threeten.bp.LocalDate

class FutureWeatherViewModel (
    private val forecastRepository: ForecastRepository,
): ViewModel() {
    private val TAG = javaClass.simpleName
    var futureWeatherList =  GlobalScope.async(Dispatchers.IO,start = CoroutineStart.LAZY) {
        Log.d(TAG, "futureWeatherList : LAZY weather")
        forecastRepository.getFutureWeatherList(LocalDate.now())
    }
    var weatherLocation = GlobalScope.async(Dispatchers.IO,start = CoroutineStart.LAZY) {
        Log.d(TAG, "future weatherLocation: async")
        forecastRepository.getWeatherLocation()
    }
    fun reInitData(){
        futureWeatherList =  GlobalScope.async(Dispatchers.IO,start = CoroutineStart.LAZY) {
            Log.d(TAG, ":reInitData LAZY weather")
            forecastRepository.getFutureWeatherList(LocalDate.now())
        }
        weatherLocation = GlobalScope.async(Dispatchers.IO,start = CoroutineStart.LAZY) {
            Log.d(TAG, "reInitData weatherLocation: async")
            forecastRepository.getWeatherLocation()
        }
    }
}