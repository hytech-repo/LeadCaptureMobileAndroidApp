package com.eva.lead.capture.data.local

import android.content.Context
import android.content.SharedPreferences
import com.eva.lead.capture.constants.AppConstants
import com.eva.lead.capture.domain.repository.PrefManager

class PrefManagerImpl(context: Context) : PrefManager {
    private val mSharedPreferences: SharedPreferences =
        context.getSharedPreferences(AppConstants.APP_PREF, Context.MODE_PRIVATE)

    override fun <T> put(key: String, value: T) {
        with(mSharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is Set<*> -> putStringSet(key, value as Set<String>)
                else -> throw IllegalArgumentException("Unsupported type")
            }
            apply()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> mSharedPreferences.getString(key, defaultValue) as T
            is Int -> mSharedPreferences.getInt(key, defaultValue) as T
            is Boolean -> mSharedPreferences.getBoolean(key, defaultValue) as T
            is Float -> mSharedPreferences.getFloat(key, defaultValue) as T
            is Long -> mSharedPreferences.getLong(key, defaultValue) as T
            is Set<*> -> mSharedPreferences.getStringSet(key, defaultValue as Set<String>) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    override fun remove(key: String) {
        mSharedPreferences.edit().remove(key).apply()
    }

    override fun clear() {
        mSharedPreferences.edit().clear().apply()
    }
}