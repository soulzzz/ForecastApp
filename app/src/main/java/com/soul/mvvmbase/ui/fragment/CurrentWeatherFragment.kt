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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.afollestad.assent.Permission
import com.afollestad.assent.runWithPermissions

import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.soul.mvvmbase.data.viewmodel.CurrentWeatherViewModel
import com.soul.mvvmbase.databinding.FragmentCurrentWeatherBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import java.security.MessageDigest
import java.util.*


class CurrentWeatherFragment : Fragment() {
    //声明AMapLocationClient类对象


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
        var mLocationClient:AMapLocationClient = AMapLocationClient(activity?.applicationContext)

        var option = AMapLocationClientOption()
        option.isOnceLocation = true
        option.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mLocationClient.setLocationListener(AMapLocationListener {
            if(it!=null){
                if (it.errorCode == 0) {
                    Log.d("TAG", "getResult: $it")
                    //可在其中解析amapLocation获取相应内容。
                    currentWeatherViewModel.locationProvider.setAutoLocationCode(it.longitude.roundTo2DecimalPlaces()  +","+ it.latitude.roundTo2DecimalPlaces() )
                    currentWeatherViewModel.locationProvider.setAutoLocationName(it.city)
                    currentWeatherViewModel.locationProvider.currentLocationName.postValue("null")
                }else {

                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error ${it.locationDetail}, ErrCode:"
                            + it.errorCode + ", errInfo:"
                            + it.errorInfo
                    );
                }
            }
        })


        if(currentWeatherViewModel.locationProvider.WhetherUseDeviecLocation()){
            Log.d("TAG", "bindUI:startLocation ")
            runWithPermissions(Permission.ACCESS_COARSE_LOCATION,Permission.ACCESS_FINE_LOCATION){
                val info: PackageInfo = requireContext().packageManager.getPackageInfo(
                    requireContext().packageName, PackageManager.GET_SIGNATURES
                )
                val cert: ByteArray = info.signatures.get(0).toByteArray()
                val md: MessageDigest = MessageDigest.getInstance("SHA1")
                val publicKey: ByteArray = md.digest(cert)
                val hexString = StringBuffer()
                for (i in publicKey.indices) {
                    val appendString = Integer.toHexString(0xFF and publicKey[i].toInt())
                        .uppercase(Locale.US)
                    if (appendString.length == 1) hexString.append("0")
                    hexString.append(appendString)
                }
                Log.d("TAG", "SHA1: ${hexString.toString()}")
                if( null != mLocationClient){
                    mLocationClient.setLocationOption(option);
                    //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
                    mLocationClient.stopLocation();
                    mLocationClient.startLocation();
                }
            }
        }

        lifecycleScope.launch {
            currentWeatherViewModel.locationProvider.currentLocationName.observe(viewLifecycleOwner, Observer {
                currentWeatherViewModel.fetchNewWeatherWhenLocationChanged()
            })
            val currentWeather = currentWeatherViewModel.weather.await()
            currentWeather.observe(viewLifecycleOwner
                , Observer {
                    Log.d("TAG", "bindUI123: ${it.toString()}")
                    if(it ==null) return@Observer
                    updateActionBar(if(currentWeatherViewModel.locationProvider.WhetherUseDeviecLocation()){
                        Log.d("TAG", "bindUI 1: ")
                        currentWeatherViewModel.locationProvider.getAutoLocationName()
                    }else{
                        Log.d("TAG", "bindUI 2: ")
                        currentWeatherViewModel.locationProvider.getSelectedLocationName()
                    })
                    updateTemperatures(it.temp,it.feelsLike)
                    currentWeatherBinding.groupLoading.visibility = View.GONE
//                    currentWeatherBinding.textview.text = it.toString()
                    currentWeatherBinding.textview.visibility = View.GONE
                })
        }


    }
    fun Double.roundTo2DecimalPlaces() =
        BigDecimal(this).setScale(2, BigDecimal.ROUND_HALF_UP).toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate: ")

        AMapLocationClient.updatePrivacyShow(activity?.application, true, true);
        AMapLocationClient.updatePrivacyAgree(activity?.application, true);



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