package com.proximity.proximitymtapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.proximityapp.data.ResponseData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.lang.reflect.Type


class AirQualityClientRepository() {
    private val BASE_URL = "ws://city-ws.herokuapp.com/"
    private val TAG = "AirQualityClientRepository"
    private var client = OkHttpClient()
    private var gson = Gson()
    private var liveResponseData: MutableLiveData<ArrayList<ResponseData>> = MutableLiveData()
    var liveErrorData: MutableLiveData<String> = MutableLiveData()

    private inner class WebServerClientListener() : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.d(TAG, "on_open = " + response.body)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.d(TAG, "on_message = $text")
            setDataToModelClass(text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            Log.d(TAG, "on_closing = $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d(TAG, "on_closed = $reason")
            liveErrorData.postValue("WebSocket Closed for $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.d(TAG, "on_failure = " + t.localizedMessage)
            if (t.localizedMessage?.toString() != "executor rejected") {
                liveErrorData.postValue("WebSocket Failed for ${t.localizedMessage}")
            }
        }
    }

    private fun setDataToModelClass(text: String) {
        val collectionType: Type = object : TypeToken<ArrayList<ResponseData>>() {}.type
        val enums: ArrayList<ResponseData> = gson.fromJson(text, collectionType)
        liveResponseData.postValue(enums)
    }

    fun getLatestAQIData(): MutableLiveData<ArrayList<ResponseData>> {
        return liveResponseData
    }

    fun fetchDataFromSocket() {
        val request = Request.Builder().url(BASE_URL).build()
        val listener = WebServerClientListener()
        val ws: WebSocket = client.newWebSocket(request, listener)
        client.dispatcher.executorService.shutdown()
    }
}