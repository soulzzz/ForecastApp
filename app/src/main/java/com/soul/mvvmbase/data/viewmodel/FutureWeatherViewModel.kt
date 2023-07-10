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
    val futureWeatherList =  GlobalScope.async(Dispatchers.IO,start = CoroutineStart.LAZY) {
        Log.d(TAG, ": LAZY weather")
        forecastRepository.getFutureWeatherList(LocalDate.now())
    }
    var weatherLocation = GlobalScope.async(Dispatchers.IO,start = CoroutineStart.LAZY) {
        Log.d(TAG, "weatherLocation: async")
        forecastRepository.getWeatherLocation()
    }
}