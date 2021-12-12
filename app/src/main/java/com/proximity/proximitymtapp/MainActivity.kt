package com.proximity.proximitymtapp


import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.android.proximityapp.view.AirQualityDisplayDataFragment
import com.android.proximityapp.view.AirQualityGraphFragment

class MainActivity : AppCompatActivity(), AirQualityDisplayDataFragment.DataForGraph {
    companion object {
        val displayFragment = AirQualityDisplayDataFragment::class.java.simpleName
        val graphFragment = AirQualityGraphFragment::class.java.simpleName
    }

    override fun onBackPressed() {
        val fm: FragmentManager = supportFragmentManager
        if (fm.backStackEntryCount > 0)
            fm.popBackStack()
        else
            super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null && isNetworkAvailable()) {
            loadDataFragment()
        } else {
            Toast.makeText(this, "No  Internet Connection", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun loadDataFragment() {
        val dataFragment = supportFragmentManager.findFragmentByTag(displayFragment)
        if (dataFragment == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.fragmentFrame,
                AirQualityDisplayDataFragment.newInstance("", "", this), displayFragment
            )
                .commit()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    //Final Data With All AQIValues For The Selected City
    override fun getDataForGraph(cityName: String, list: ArrayList<Int>) {
        loadMonitorFragment(cityName, list)
    }

    private fun loadMonitorFragment(cityName: String, list: ArrayList<Int>) {
        val monitorFragment = supportFragmentManager.findFragmentByTag(graphFragment)
        if (monitorFragment == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragmentFrame,
                AirQualityGraphFragment.newInstance(cityName, list), graphFragment
            ).addToBackStack(graphFragment).commit()
        }
    }
}