package com.eva.lead.capture.data.local.converter

import androidx.annotation.Keep
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Keep
class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String>? {
        return Gson().fromJson(value, Array<String>::class.java)?.toList()
    }
}