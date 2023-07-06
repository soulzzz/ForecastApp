package com.soul.mvvmbase.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.soul.mvvmbase.R
import com.soul.mvvmbase.data.bean.WeatherLocation
import com.soul.mvvmbase.data.viewmodel.CurrentWeatherViewModel
import com.soul.mvvmbase.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal

class MainActivity : BaseActivity() {
    private val TAG = javaClass.simpleName
    lateinit var mainBinding:ActivityMainBinding
    lateinit var navController:NavController
    private val currentWeatherViewModel:CurrentWeatherViewModel by viewModel()
    private val mLocationClient: AMapLocationClient by inject()
    private val option = AMapLocationClientOption()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(mainBinding.root)
        setSupportActionBar(mainBinding.toorbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph)
        mainBinding.bottomNav.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this,navController)
        askForPermissions(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION){ it ->
            if(it.isAllGranted()){
                Log.d(TAG, "AllGranted: ")
                bindLocationClient()
                //
            }else{
                askForPermissions(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION){it2 ->
                    if(it2.isAllGranted()){
                        Log.d(TAG, "AllGranted: ")
                        bindLocationClient()
                        //
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController,null)
    }
    private fun bindLocationClient(){
        option.isOnceLocation = true
        option.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mLocationClient.setLocationOption(option)
        mLocationClient.setLocationListener(AMapLocationListener {
            if(it!=null){
                if (it.errorCode == 0) {
                    Log.d(TAG, "getResult: $it")
                    //可在其中解析amapLocation获取相应内容。
                    currentWeatherViewModel.persistFetchedWeatherLocation(
                        WeatherLocation(it.latitude.roundTo2DecimalPlaces(),it.longitude.roundTo2DecimalPlaces(),it.province,it.coordType,it.city,it.district,it.cityCode,
                        it.adCode,it.address,it.country)
                    )

                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e(TAG+"AmapError","location Error ${it.locationDetail}, ErrCode:"
                            + it.errorCode + ", errInfo:"
                            + it.errorInfo
                    );
                }
            }
        })
        mLocationClient.startLocation()
    }
    fun restartLocation(){
        if(mLocationClient!=null){
            mLocationClient.stopLocation()
            mLocationClient.startLocation()
        }
    }
    fun Double.roundTo2DecimalPlaces() =
        BigDecimal(this).setScale(2, BigDecimal.ROUND_HALF_UP).toString()
}