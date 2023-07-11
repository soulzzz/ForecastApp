package com.soul.mvvmbase.ui.fragment

import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.amap.api.location.AMapLocationClientOption
import com.caverock.androidsvg.SVG
import com.soul.mvvmbase.data.viewmodel.CurrentWeatherViewModel
import com.soul.mvvmbase.databinding.FragmentCurrentWeatherBinding
import com.soul.mvvmbase.util.SvgUtil
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.io.InputStream
import java.util.*


class CurrentWeatherFragment : Fragment() {
    private val TAG = javaClass.simpleName
    private val currentWeatherViewModel: CurrentWeatherViewModel by viewModel()
    private lateinit var currentWeatherBinding: FragmentCurrentWeatherBinding
    private val option = AMapLocationClientOption()
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

    fun bindUI() {
        lifecycleScope.launch {

            val weatherLocation = currentWeatherViewModel.weatherLocation.await()
            val currentWeather = currentWeatherViewModel.weather.await()
            weatherLocation.observe(viewLifecycleOwner, Observer {
                Log.d(TAG, "bindUI: weatherLocation${weatherLocation.value}")
                updateActionBar(weatherLocation.value?.city)
            })
            currentWeather.observe(viewLifecycleOwner, Observer {
                Log.d(TAG, "bindUI currentWeather: ${it.toString()}")
                if (it == null) return@Observer
                updateTemperatures(it.temp, it.feelsLike)
                currentWeatherBinding.groupLoading.visibility = View.GONE
//                    currentWeatherBinding.textview.text = it.toString()
                currentWeatherBinding.textview.visibility = View.GONE

                SvgUtil.updateIcon(requireContext(),it.icon,currentWeatherBinding.imageViewWeather)
                updateWeatherText(it.text)
                updateWeatherWindInfo(it.windDir,it.windScale)
                updateHumidity(it.humidity)
                updateVis(it.vis)
            })
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate: ")

//        currentWeatherViewModel = ViewModelProvider(this,currentWeatherViewModelFactory)[CurrentWeatherViewModel::class.java]

    }

    private fun updateActionBar(location: String?) {
        (activity as AppCompatActivity)?.supportActionBar?.title = location
        (activity as AppCompatActivity)?.supportActionBar?.subtitle = "Today"
    }

    private fun updateTemperatures(tmp: String, feelsTmp: String) {
        currentWeatherBinding.textviewTmp.text = "$tmp°C"
        currentWeatherBinding.textviewFeelsLikeTmp.text = "Feels like $feelsTmp°C"
    }
    private fun updateWeatherText(text: String) {
        currentWeatherBinding.weatherText.text = text
    }
    private fun updateWeatherWindInfo(windDir: String,windScale :String) {
        currentWeatherBinding.textviewWindDir.text = "风向:$windDir"
        var tmp = windScale.toInt()
        var scale = when(tmp){
            0 -> "无风"
            1 -> "软风"
            2 -> "轻风"
            3 -> "微风"
            4 -> "和风"
            5 -> "清风"
            6 -> "强风"
            7 -> "疾风"
            8 -> "大风"
            9 -> "烈风"
            10 -> "狂风"
            11 -> "暴风"
            12 -> "飓风"
            else -> "台风"
        }
        currentWeatherBinding.textviewWindScale.text = "风力:$scale"
    }
    private fun updateHumidity(humidity : String) {
        currentWeatherBinding.textviewHumidity.text = "相对湿度:$humidity%"
    }
    private fun updateVis(vis : String) {
        currentWeatherBinding.textviewVis.text = "可见度:${vis}公里"
    }
//    private fun updateIcon(img:String){
//        try {
//            // 从Assets目录下读取SVG文件
//            val inputStream: InputStream = requireContext().assets.open("${img}.svg")
//            // 使用AndroidSVG库解析SVG文件
//            val svg = SVG.getFromInputStream(inputStream)
//            // 创建一个PictureDrawable对象
//            val pictureDrawable = PictureDrawable(svg.renderToPicture())
//            // 将PictureDrawable对象设置给ImageView
//            currentWeatherBinding.imageViewWeather.setImageDrawable(pictureDrawable)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//
//    }

}