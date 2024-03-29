package com.maison.mona.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AdressesConverter {
    @TypeConverter
    fun toAdresses(json: String?): List<Any>? {
        val type = object : TypeToken<List<Any>?>() {}.type ?: return null
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(dimensions: List<Any>?): String? {
        val type = object: TypeToken<List<Any>?>() {}.type ?: return null
        return Gson().toJson(dimensions, type)
    }
}