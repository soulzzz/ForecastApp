package com.soul.mvvmbase

import android.app.Application
import androidx.preference.PreferenceManager
import com.soul.mvvmbase.data.db.CurrentWeatherDao
import com.soul.mvvmbase.data.db.ForecastDatabase
import com.soul.mvvmbase.data.network.WeatherNetworkDataSource
import com.soul.mvvmbase.data.network.WeatherNetworkDataSourceImpl
import com.soul.mvvmbase.data.network.api.WeatherApiService
import com.soul.mvvmbase.data.network.interceptor.ConnectivityInterceptor
import com.soul.mvvmbase.data.network.interceptor.ConnectivityInterceptorImpl
import com.soul.mvvmbase.data.network.repository.ForecastRepository
import com.soul.mvvmbase.data.network.repository.ForecastRepositoryImpl
import com.soul.mvvmbase.data.provider.LocationProvider
import com.soul.mvvmbase.data.provider.LocationProviderImpl
import com.soul.mvvmbase.data.viewmodel.CurrentWeatherViewModel
import com.soul.mvvmbase.data.viewmodel.CurrentWeatherViewModelFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {

    private val appModule = module {

        single<CurrentWeatherDao> { ForecastDatabase(applicationContext).currentWeatherDao() }
        single<ConnectivityInterceptor> {  ConnectivityInterceptorImpl(get())}
        single<WeatherApiService> { WeatherApiService(get()) }
        single<WeatherNetworkDataSource> { WeatherNetworkDataSourceImpl(get()) }
        single<ForecastRepository> { ForecastRepositoryImpl(get(),get()) }
        single<LocationProvider>{LocationProviderImpl(applicationContext)}
        single<CurrentWeatherViewModelFactory>{ CurrentWeatherViewModelFactory(get(),get()) }
        single<CurrentWeatherViewModel>{ CurrentWeatherViewModel(get(),get())}
//        viewModel {
//            CurrentWeatherViewModel(get(),get()) }
        }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
        PreferenceManager.setDefaultValues(this,R.xml.preference,false)
    }
}