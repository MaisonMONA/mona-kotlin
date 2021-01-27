package com.maison.mona.converter

import androidx.room.TypeConverter
import com.maison.mona.entity.Bilingual
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class BilingualListConverter {

    @TypeConverter
    fun toBilingualList(json: String? = "[]"): List<Bilingual>? {
        val type = object : TypeToken<List<Bilingual>>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(bilingualList: List<Bilingual>? = listOf(Bilingual("",""))): String? {
        val type = object: TypeToken<List<Bilingual>>() {}.type
        return Gson().toJson(bilingualList, type)
    }

}