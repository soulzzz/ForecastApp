package com.soul.mvvmbase.data.provider


import androidx.lifecycle.MutableLiveData

interface LocationProvider {
    val use_device_location: MutableLiveData<Boolean>
    fun getSelectedLocationName():String
    fun getSelectedLocationCode():String


}