package com.soul.mvvmbase.data.network.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.soul.mvvmbase.data.bean.CurrentWeather
import com.soul.mvvmbase.data.bean.DailyWeather
import com.soul.mvvmbase.data.bean.WeatherLocation
import com.soul.mvvmbase.data.db.CurrentWeatherDao
import com.soul.mvvmbase.data.db.DailyWeatherDao
import com.soul.mvvmbase.data.db.WeatherLocationDao
import com.soul.mvvmbase.data.network.WeatherNetworkDataSource
import com.soul.mvvmbase.data.network.api.FUTURE_DAYS
import com.soul.mvvmbase.data.network.response.CurrentWeatherResponse
import com.soul.mvvmbase.data.network.response.FutureWeatherResponse
import com.soul.mvvmbase.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import java.util.*

class ForecastRepositoryImpl(private val currentWeatherDao: CurrentWeatherDao,
                             private val dailyWeatherDao: DailyWeatherDao,
                             private val weatherLocationDao: WeatherLocationDao,
                             private val locationProvider: LocationProvider,
                             private val weatherNetworkDataSource: WeatherNetworkDataSource
) : ForecastRepository {
    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever(Observer {
            Log.d(TAG, ":persistFetchedCurrentWeather $it ")
            persistFetchedCurrentWeather(it)
        })
        weatherNetworkDataSource.downloadedFutureWeather.observeForever{
            persistFetchedFutureWeather(it)
        }
        weatherLocationDao.getWeatherLocation().observeForever {
            Log.d(TAG, ": getWeatherLocation observeForever ${it}" )
            if(it !=null && !it.latitude.equals("null")){
                GlobalScope.launch {
                    reFetchWeatherData()
                }
            }
        }
    }


    private val TAG = javaClass.simpleName
    override suspend fun getCurrentWeather(): LiveData<CurrentWeather> {
       return withContext(Dispatchers.IO){
           initWeatherData()
           return@withContext currentWeatherDao.getCurrentWeather()
       }
    }
    override suspend fun getFutureWeatherList(
        startDate: LocalDate,
    ): LiveData<out List<DailyWeather>> {
        return withContext(Dispatchers.IO){
            initWeatherData()
            return@withContext dailyWeatherDao.getWeatherForecasts(startDate)
        }
    }
    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO){
            return@withContext weatherLocationDao.getWeatherLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeatherResponse: CurrentWeatherResponse){
        if(fetchedWeatherResponse.code == "400") return
        GlobalScope.launch(Dispatchers.IO){
            currentWeatherDao.upsert(fetchedWeatherResponse.currentWeather)
        }
    }
    override fun persistFetchedWeatherLocation(weatherLocation: WeatherLocation){
        GlobalScope.launch(Dispatchers.IO){
            var tmpWeatherLocation = weatherLocationDao.getLocationNonLive()
            if(tmpWeatherLocation!=null){
                Log.d(TAG, "persistFetchedWeatherLocation: 1 ")
                if(weatherLocation.city!= tmpWeatherLocation.city || isFetchCurrentNeeded(tmpWeatherLocation.location_time)){
                    weatherLocationDao.upsert(weatherLocation)
                    Log.d(TAG, "persistFetchedWeatherLocation:2 ")
                }
            }else{
                Log.d(TAG, "persistFetchedWeatherLocation: 3 ")
                weatherLocationDao.upsert(weatherLocation)
            }



        }
    }
    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {
        if(fetchedWeather.code == "400") return
        fun deleteOldForecastData() {
            val today = LocalDate.now()
            dailyWeatherDao.deleteOldEntries(today)
        }

        GlobalScope.launch(Dispatchers.IO) {
            deleteOldForecastData()
            val futureWeatherList = fetchedWeather.dailyWeather
            dailyWeatherDao.insert(futureWeatherList)
        }
    }

    override fun isUsingDeviceLocation(): Boolean = locationProvider.isUsingDeviceLocation()


    override suspend fun getFutureWeatherByDate(
        date: LocalDate,
    ): LiveData<out DailyWeather> {
        TODO("Not yet implemented")
    }


    private suspend fun initWeatherData(){
        if(locationProvider.isUsingDeviceLocation()){
            Log.d(TAG, "initWeatherData: isUsingDeviceLocation")
            val lastWeatherLocation = weatherLocationDao.getLocationNonLive()
            Log.d(TAG, "initWeatherData from db: ${lastWeatherLocation?.location_time}")
            if(lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation)){
                Log.d(TAG, "initWeatherData db==null or location changed: ")
                fetchCurrentWeather()
                fetchFutureWeather()
                return
            }
            if (isFetchCurrentNeeded(lastWeatherLocation.location_time)){
                fetchCurrentWeather()
            }
            if(isFetchFutureNeeded()){
                fetchFutureWeather()
            }
        }else{
            Log.d(TAG, "initWeatherData: NotUsingDeviceLocation ")
            fetchCurrentWeather()
            fetchFutureWeather()
            persistFetchedWeatherLocation(WeatherLocation(city = locationProvider.getSelectedLocationName()))
        }

    }
    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            locationProvider.getPreferredLocationString()
        )
    }

    private suspend fun fetchFutureWeather() {
        weatherNetworkDataSource.fetchFutureWeather(
            locationProvider.getPreferredLocationString()
        )
    }
    private suspend fun reFetchWeatherData(){
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()
        Log.d(TAG, "reFetchWeatherData from db: ${lastWeatherLocation?.location_time}")
        fetchCurrentWeather()
        fetchFutureWeather()
    }
    private fun isFetchCurrentNeeded(lastFetchedTime:ZonedDateTime):Boolean{

        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchedTime.isBefore(thirtyMinutesAgo)
    }
    private fun isFetchFutureNeeded(): Boolean {
        val today = LocalDate.now()
        val futureWeatherCount = dailyWeatherDao.countFutureWeather(today)
        return futureWeatherCount < FUTURE_DAYS
    }
}