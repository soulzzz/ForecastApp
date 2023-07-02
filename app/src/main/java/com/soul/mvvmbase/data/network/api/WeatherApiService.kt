package com.soul.mvvmbase.data.network.api

import com.soul.mvvmbase.data.network.interceptor.ConnectivityInterceptor
import com.soul.mvvmbase.data.network.interceptor.ConnectivityInterceptorImpl
import com.soul.mvvmbase.data.network.response.CurrentWeatherResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "4dbcf5dec570446f8eca22a0e1756b57"

//https://devapi.qweather.com/v7/weather/now?location=beijing&key=4dbcf5dec570446f8eca22a0e1756b57
interface WeatherApiService {
    @GET("now")
    suspend fun getCurrentWeather(@Query("location")location:String): CurrentWeatherResponse
    companion object{
        operator fun invoke(connectivityInterceptor: ConnectivityInterceptor):WeatherApiService{
           val reqestIntercepter = Interceptor{chain ->
               val url = chain.request()
                   .url()
                   .newBuilder()
                   .addQueryParameter("key",API_KEY)
                   .build()
               val request = chain.request()
                   .newBuilder()
                   .url(url)
                   .build()
               return@Interceptor chain.proceed(request)
           }
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(reqestIntercepter)
                .addInterceptor(connectivityInterceptor)
                .addNetworkInterceptor(Interceptor {
                    chain ->
                    println(chain.request().url())
                    chain.proceed(chain.request())
                })
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://devapi.qweather.com/v7/weather/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}