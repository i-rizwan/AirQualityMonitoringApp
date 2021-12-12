package com.android.proximityapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.proximityapp.data.ResponseData
import com.proximity.proximitymtapp.viewmodel.AirQualityClientRepository

class AirQualityDisplayDataViewModel:ViewModel() {

    private  var webServerClient = AirQualityClientRepository()
    var mutableLiveData: MutableLiveData<ArrayList<ResponseData>> = MutableLiveData()

    fun initConnection(){
        webServerClient.fetchDataFromSocket()
        mutableLiveData = webServerClient.getLatestAQIData()
    }

    fun getLatestDataForAirQuality(): MutableLiveData<ArrayList<ResponseData>> {
        return mutableLiveData
    }

    fun getErrorMessageFromSocketNetwork(): MutableLiveData<String>{
        return webServerClient.liveErrorData
    }

}