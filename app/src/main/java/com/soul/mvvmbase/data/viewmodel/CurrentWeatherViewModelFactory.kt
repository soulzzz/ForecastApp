package com.soul.mvvmbase.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.soul.mvvmbase.data.network.repository.ForecastRepository
import com.soul.mvvmbase.data.provider.LocationProvider


class CurrentWeatherViewModelFactory(
    private val forecastRepository: ForecastRepository,
    private val locationProvider: LocationProvider
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CurrentWeatherViewModel(forecastRepository,locationProvider) as T
    }
}