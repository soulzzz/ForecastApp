package com.soul.mvvmbase.data.provider


import androidx.lifecycle.MutableLiveData

interface LocationProvider {
    fun getSelectedLocationName():String
    fun getSelectedLocationCode():String

    fun isUsingDeviceLocation():Boolean
}