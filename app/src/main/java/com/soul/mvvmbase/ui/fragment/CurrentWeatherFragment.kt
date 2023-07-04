package com.soul.mvvmbase.ui.fragment

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.afollestad.assent.Permission
import com.afollestad.assent.runWithPermissions

import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.soul.mvvmbase.data.bean.WeatherLocation
import com.soul.mvvmbase.data.viewmodel.CurrentWeatherViewModel
import com.soul.mvvmbase.databinding.FragmentCurrentWeatherBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import java.security.MessageDigest
import java.util.*


class CurrentWeatherFragment : Fragment() {
    private val TAG = javaClass.simpleName
    private val mLocationClient:AMapLocationClient by inject()
    private val  currentWeatherViewModel: CurrentWeatherViewModel by viewModel()
    private lateinit var currentWeatherBinding: FragmentCurrentWeatherBinding
    private val option =AMapLocationClientOption()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentWeatherBinding = FragmentCurrentWeatherBinding.inflate(layoutInflater)
        return currentWeatherBinding.root

    }

    companion object {
        fun newInstance() = CurrentWeatherFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        currentWeatherViewModel = ViewModelProvider(this,currentWeatherViewModelFactory)[CurrentWeatherViewModel::class.java]
        bindUI()

    }
    fun bindUI(){
        option.isOnceLocation = true
        option.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mLocationClient.setLocationListener(AMapLocationListener {
            if(it!=null){
                if (it.errorCode == 0) {
                    Log.d("TAG", "getResult: $it")
                    //可在其中解析amapLocation获取相应内容。
                    currentWeatherViewModel.persistFethedWeatherLocation(WeatherLocation(it.latitude.roundTo2DecimalPlaces(),it.longitude.roundTo2DecimalPlaces(),it.province,it.coordType,it.city,it.district,it.cityCode,
                    it.adCode,it.address,it.country))
                    lifecycleScope.launch {
                        runBlocking {
                            val weatherLocation = currentWeatherViewModel.weatherLocation.await()
                            val currentWeather = currentWeatherViewModel.weather.await()
                            weatherLocation.observe(viewLifecycleOwner, Observer {
                                Log.d(TAG, "bindUI: ${weatherLocation.value.toString()} ${weatherLocation.value?.location_time}")
                                lifecycleScope.launch(Dispatchers.Main) {
                                        currentWeatherBinding.textview.visibility = android.view.View.GONE
                                        currentWeatherBinding.groupLoading.visibility = View.GONE
                                        updateActionBar(weatherLocation.value?.city!!)
                                }

                            })
                            currentWeather.observe(viewLifecycleOwner, Observer {
                                lifecycleScope.launch(Dispatchers.Main){
                                    updateTemperatures(it.temp, it.feelsLike)
                                }
                            })
                        }

                    }

                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error ${it.locationDetail}, ErrCode:"
                            + it.errorCode + ", errInfo:"
                            + it.errorInfo
                    );
                }
            }
        })


        if(currentWeatherViewModel.locationProvider.isUsingDeviceLocation()){
            Log.d("TAG", "bindUI:startLocation ")
            runWithPermissions(Permission.ACCESS_COARSE_LOCATION,Permission.ACCESS_FINE_LOCATION){
                if( null != mLocationClient){
                    mLocationClient.setLocationOption(option);
                    //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
                    mLocationClient.stopLocation();
                    mLocationClient.startLocation();
                }
            }
        }else{
            lifecycleScope.launch {
                val currentWeather = currentWeatherViewModel.weather.await()
                currentWeather.observe(viewLifecycleOwner, Observer {
                    Log.d("TAG", "bindUI123: ${it.toString()}")
                    if (it == null) return@Observer
                    updateActionBar(currentWeatherViewModel.locationProvider.getSelectedLocationName())
                    updateTemperatures(it.temp, it.feelsLike)
                    currentWeatherBinding.groupLoading.visibility = View.GONE
//                    currentWeatherBinding.textview.text = it.toString()
                    currentWeatherBinding.textview.visibility = View.GONE
                })
            }
        }


    }
    fun Double.roundTo2DecimalPlaces() =
        BigDecimal(this).setScale(2, BigDecimal.ROUND_HALF_UP).toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate: ")

//        currentWeatherViewModel = ViewModelProvider(this,currentWeatherViewModelFactory)[CurrentWeatherViewModel::class.java]

    }
    private fun updateActionBar(location:String){
        (activity as AppCompatActivity)?.supportActionBar?.title = location
        (activity as AppCompatActivity)?.supportActionBar?.subtitle = "Today"
    }
    private fun updateTemperatures(tmp:String,feelsTmp:String){
        currentWeatherBinding.textviewTmp.text = "$tmp°C"
        currentWeatherBinding.textviewFeelsLikeTmp.text = "Feels like $feelsTmp°C"
    }

}