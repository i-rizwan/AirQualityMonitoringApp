package com.android.proximityapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.proximityapp.adapter.DataDisplayAdapter.MyViewHolder
import com.android.proximityapp.data.ResponseData
import com.android.proximityapp.utility.AppConstants
import com.android.proximityapp.utility.Utility
import com.proximity.proximitymtapp.R
import java.util.*
import kotlin.collections.ArrayList

class DataDisplayAdapter(private val mContext: Context, private val listener: SelectedCity) :
    RecyclerView.Adapter<MyViewHolder>() {

    private var listOfResponseData: ArrayList<ResponseData> = ArrayList()
    private var oldValue = ""
    private var newValue = ""
    private val helperUtility = Utility()

    fun setData(list: List<ResponseData>) {
        listOfResponseData.clear()
        listOfResponseData.addAll(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.row_data_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val aqiData = listOfResponseData[position]
        val date = Calendar.getInstance()
        val hour = date.get(Calendar.HOUR_OF_DAY)
        val minutes = date.get(Calendar.MINUTE)
        val seconds = date.get(Calendar.SECOND)
        val AM_PM = if (date.get(Calendar.AM_PM) == 1) {
            "PM"
        } else {
            "AM"
        }
        with(holder) {
            cityName.text = aqiData.cityName
            aqiValue.text = aqiData.airQualityUnit.toString()
            val colorCode = helperUtility.getColorFromAQIValue(aqiData.airQualityUnit.toInt())
            aqiValue.setTextColor(mContext.resources.getColor(colorCode))
            val presentTime = "$hour:$minutes:$seconds $AM_PM"
            if (cityName.text.equals(aqiData.cityName) && aqiValue.text.equals(aqiData.airQualityUnit.toString())) {
                lastUpdatedValue.text = presentTime
            } else {
                if (lastUpdatedValue.text.isNotEmpty()) {
                    val tempPresentTime: String = presentTime
                    val tempLastUpdatedValue =
                        if (lastUpdatedValue.text.equals(AppConstants.HEADING_1) ||
                            lastUpdatedValue.text.equals(AppConstants.HEADING_2)
                        ) {
                            tempPresentTime
                        } else {
                            lastUpdatedValue.text as String
                        }
                    val finalValue = helperUtility.getMinuteFromCurrentTime(tempLastUpdatedValue)
                    oldValue = finalValue[0]
                    newValue = minutes.toString()
                    lastUpdatedValue.text =
                        if (helperUtility.getLastUpdatedValue(newValue.toInt(), oldValue.toInt())
                                .isEmpty()
                        ) {
                            presentTime
                        } else {
                            helperUtility.getLastUpdatedValue(newValue.toInt(), oldValue.toInt())
                        }
                } else {
                    lastUpdatedValue.text = presentTime
                }
            }
            rootLayout.setOnClickListener(View.OnClickListener {
                listener.getSelectedCity(aqiData.cityName)
            })
        }
    }

    override fun getItemCount(): Int {
        return listOfResponseData.size
    }

    interface SelectedCity {
        fun getSelectedCity(cityName: String)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rootLayout: TableLayout = itemView.findViewById(R.id.rootTableLayout)
        var cityName: TextView = itemView.findViewById(R.id.cityName)
        var aqiValue: TextView = itemView.findViewById(R.id.aqiValue)
        var lastUpdatedValue: TextView = itemView.findViewById(R.id.lastUpdated)
    }
}