package com.soul.mvvmbase.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.soul.mvvmbase.R
import com.soul.mvvmbase.data.viewmodel.FutureWeatherViewModel
import com.soul.mvvmbase.databinding.FragmentFutureWeatherBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

// TODO: Rename parameter arguments, choose names that match



class FutureWeatherFragment : Fragment() {
    private val TAG = javaClass.simpleName
    private val futureWeatherViewModel:FutureWeatherViewModel by inject()
    private lateinit var futureWeatherBinding:FragmentFutureWeatherBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        futureWeatherBinding = FragmentFutureWeatherBinding.inflate(layoutInflater)
        return futureWeatherBinding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        GlobalScope.launch(Dispatchers.Main) {
            val futureWeather = futureWeatherViewModel.futureWeatherList.await()
            val weatherLocation = futureWeatherViewModel.weatherLocation.await()
            futureWeather.observe(viewLifecycleOwner){
                if (it == null) return@observe
                Log.d(TAG, "bindUI: $it")
                futureWeatherBinding.groupLoading.visibility = View.GONE
            }
            weatherLocation.observe(viewLifecycleOwner){
                if (it == null) return@observe
                updateLocation(it.city)
            }
        }

    }
    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    companion object {
    }
}