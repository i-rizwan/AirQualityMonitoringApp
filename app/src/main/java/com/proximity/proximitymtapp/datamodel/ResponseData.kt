package com.android.proximityapp.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseData {
    @SerializedName("city")
    @Expose
    var cityName: String = ""

    @SerializedName("aqi")
    @Expose
    var airQualityUnit: Double = 0.0

}