package com.soul.mvvmbase.data.provider


import androidx.lifecycle.MutableLiveData

interface LocationProvider {
    val currentLocationName: MutableLiveData<String>
    fun getLocationName():String
    fun getLocationCode():String
    fun WhetherUseDeviecLocation():Boolean
}