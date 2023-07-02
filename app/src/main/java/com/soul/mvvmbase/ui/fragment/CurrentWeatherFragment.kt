package com.soul.mvvmbase.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.soul.mvvmbase.data.viewmodel.CurrentWeatherViewModel
import com.soul.mvvmbase.data.viewmodel.CurrentWeatherViewModelFactory
import com.soul.mvvmbase.databinding.FragmentCurrentWeatherBinding
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class CurrentWeatherFragment : Fragment() {
    private var binded = false
//    private val weatherNetworkDataSourceImpl:WeatherNetworkDataSource by inject()
//    private val currentWeatherViewModelFactory:CurrentWeatherViewModelFactory by inject()
    private val  currentWeatherViewModel: CurrentWeatherViewModel by viewModel()
//    private lateinit var currentWeatherViewModel: CurrentWeatherViewModel
    private lateinit var currentWeatherBinding: FragmentCurrentWeatherBinding
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
            currentWeatherViewModel.locationProvider.currentLocationName.observe(viewLifecycleOwner, Observer {
                currentWeatherViewModel.fetchNewWeatherWhenLocationChanged()
            })
            currentWeatherViewModel.locationProvider.currentLocationName.observe(viewLifecycleOwner, Observer {
                currentWeatherViewModel.fetchNewWeatherWhenLocationChanged()
            })
            val currentWeather = currentWeatherViewModel.weather.await()
            currentWeather.observe(viewLifecycleOwner
                , Observer {
                    Log.d("TAG", "bindUI123: ${it.toString()}")
                    if(it ==null) return@Observer
                    updateActionBar(currentWeatherViewModel.locationProvider.getLocationName())
                    updateTemperatures(it.temp,it.feelsLike)
                    currentWeatherBinding.groupLoading.visibility = View.GONE
//                    currentWeatherBinding.textview.text = it.toString()
                    currentWeatherBinding.textview.visibility = View.GONE
                })
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate: ")
//        lifecycleScope.launch {
//            currentWeatherViewModel.currentLocationName.observe(viewLifecycleOwner, Observer {
//
//                Log.d("TAG", "bindUI123: ${currentWeatherViewModel.currentLocationName.toString()}")
//            })
//        }
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