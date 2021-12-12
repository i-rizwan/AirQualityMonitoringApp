package com.android.proximityapp.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.android.proximityapp.adapter.DataDisplayAdapter
import com.android.proximityapp.data.ResponseData
import com.android.proximityapp.viewmodel.AirQualityDisplayDataViewModel
import com.proximity.proximitymtapp.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AirQualityDisplayDataFragment : Fragment(), DataDisplayAdapter.SelectedCity {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var airQualityDisplayDataViewModel: AirQualityDisplayDataViewModel
    private lateinit var aqiRecyclerView: RecyclerView
    private lateinit var mContext: Context
    private lateinit var myAQIDisplayAdapter: DataDisplayAdapter
    private lateinit var rootLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var mapForGraph: HashMap<String, ArrayList<Int>>
    private lateinit var dataForGraph: DataForGraph

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, listener: DataForGraph) =
            AirQualityDisplayDataFragment().apply {
                dataForGraph = listener
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            airQualityDisplayDataViewModel =
                ViewModelProvider(this).get(AirQualityDisplayDataViewModel::class.java)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_displaydata, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapForGraph = HashMap()
        aqiRecyclerView = view.findViewById(R.id.recyclerView)
        rootLayout = view.findViewById(R.id.rootLayout)
        progressBar = ProgressBar(mContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(100, 100)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        rootLayout.addView(progressBar, params)
        progressBar.visibility = View.VISIBLE
        aqiRecyclerView.layoutManager = LinearLayoutManager(mContext)
        myAQIDisplayAdapter = DataDisplayAdapter(mContext, this)
        aqiRecyclerView.adapter = myAQIDisplayAdapter
        lookForData()
        lookForErrorIfAny()
    }

    private fun lookForErrorIfAny() {
        airQualityDisplayDataViewModel.getErrorMessageFromSocketNetwork()
            .observe(viewLifecycleOwner, {
                progressBar.visibility = View.GONE
                Toast.makeText(mContext, it, Toast.LENGTH_LONG).show()
            })
    }

    private fun lookForData() {
        airQualityDisplayDataViewModel.initConnection()
        airQualityDisplayDataViewModel.getLatestDataForAirQuality()
            .observe(viewLifecycleOwner, Observer { listOfAQIData ->
                processDataForGraph(listOfAQIData)
                progressBar.visibility = View.GONE
                myAQIDisplayAdapter.setData(listOfAQIData)
                myAQIDisplayAdapter.notifyDataSetChanged()
            })
    }

    private fun processDataForGraph(listOfResponseData: ArrayList<ResponseData>?) {
        listOfResponseData?.forEachIndexed { index, aqiData ->
            val listAQIValues = ArrayList<Int>()
            listAQIValues.add(aqiData.airQualityUnit.toInt())
            if (mapForGraph.size != 0) {
                if (!mapForGraph.containsKey(aqiData.cityName)) {
                    mapForGraph[aqiData.cityName] = listAQIValues
                } else {
                    for ((key, value) in mapForGraph) {
                        if (key == aqiData.cityName)
                            value.addAll(listAQIValues)
                    }
                }
            } else {
                //first time hashmap is empty, hence adding the city
                mapForGraph[aqiData.cityName] = listAQIValues
            }
        }
    }

    override fun getSelectedCity(cityName: String) {
        for ((key, value) in mapForGraph) {
            if (key == cityName) {
                dataForGraph.getDataForGraph(cityName, value)
                break
            }
        }
    }

    interface DataForGraph {
        fun getDataForGraph(cityName: String, list: ArrayList<Int>)
    }
}