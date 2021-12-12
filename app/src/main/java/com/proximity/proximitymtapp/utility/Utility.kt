package com.android.proximityapp.utility

import com.proximity.proximitymtapp.R


class Utility {

    fun getMinuteFromCurrentTime(lastUpdatedValue: String): List<String> {
        return lastUpdatedValue.replace("(?s)^.*?:|:[^:]*$".toRegex(), "")
            .split("(?s):([^:]*\\s[^:]*:)?".toRegex())
    }

    fun getColorFromAQIValue(aqiValue: Int): Int {
        when (aqiValue) {
            in 1..50 -> {
                return R.color.good
            }
            in 51..100 -> {
                return R.color.satisfactory
            }
            in 101..200 -> {
                return R.color.moderate
            }
            in 201..300 -> {
                return R.color.poor
            }
            in 301..400 -> {
                return R.color.very_poor
            }
            else -> {
                return R.color.severe
            }
        }
    }

    fun getLastUpdatedValue(newValue: Int, oldValue: Int): String {
        return when (newValue - oldValue) {
            0 -> {
                AppConstants.HEADING_1
            }
            in 1..1 -> {
                AppConstants.HEADING_2
            }
            else -> {
                ""
            }
        }
    }
}