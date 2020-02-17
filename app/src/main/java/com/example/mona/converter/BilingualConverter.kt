package com.example.mona.converter

import androidx.room.TypeConverter
import com.example.mona.entity.Bilingual
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class BilingualConverter {

    @TypeConverter
    fun toBilingual(json: String?): Bilingual? {
        val type = object : TypeToken<Bilingual?>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(bilingual: Bilingual?): String? {
        val type = object: TypeToken<Bilingual?>() {}.type
        return Gson().toJson(bilingual, type)
    }

}