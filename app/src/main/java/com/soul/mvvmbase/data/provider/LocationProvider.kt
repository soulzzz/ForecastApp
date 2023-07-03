package com.soul.mvvmbase.data.provider


import androidx.lifecycle.MutableLiveData

interface LocationProvider {
    val currentLocationName: MutableLiveData<String>
    fun getSelectedLocationName():String
    fun getSelectedLocationCode():String
    fun WhetherUseDeviecLocation():Boolean

    fun getAutoLocationName():String
    fun getAutoLocationCode():String

    fun setAutoLocationName(name:String):Unit
    fun setAutoLocationCode(code:String):Unit
}