package com.srpark.myapp.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class AppPreference(context: Context) {

    companion object {
        private val lottoSharedPreference: AppPreference? = null
        fun getInstance(context: Context): AppPreference {
            return lottoSharedPreference ?: AppPreference(context)
        }
    }

    private val sharedPref: SharedPreferences = context.getSharedPreferences(context.packageName + "_preferences", MODE_PRIVATE)
    private val sharedPrefEditor: SharedPreferences.Editor = sharedPref.edit()

    var drwNo: Long
        get() = sharedPref.getLong("drwNo", 860L)
        set(value) {
            sharedPrefEditor.putLong("drwNo", value).apply()
        }
    var location: String
        get() = sharedPref.getString("location", "37.555107,126.970691").toString()
        set(value) {
            sharedPrefEditor.putString("location", value).apply()
        }
}