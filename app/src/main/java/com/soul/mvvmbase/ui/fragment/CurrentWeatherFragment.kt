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
            lifecycleScope.launch {
                val currentWeather = currentWeatherViewModel.weather.await()
                val weatherLocation =  currentWeatherViewModel.weatherLocation.await()

                weatherLocation.observe(viewLifecycleOwner, Observer {
                    updateActionBar(weatherLocation.value!!.city)
                })
                currentWeather.observe(viewLifecycleOwner, Observer {
                    Log.d("TAG", "bindUI123: ${it.toString()}")
                    if (it == null) return@Observer
//                    updateActionBar(currentWeatherViewModel.locationProvider.getSelectedLocationName())
                    updateTemperatures(it.temp, it.feelsLike)
                    currentWeatherBinding.groupLoading.visibility = View.GONE
//                    currentWeatherBinding.textview.text = it.toString()
                    currentWeatherBinding.textview.visibility = View.GONE
                })
            }
        }


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