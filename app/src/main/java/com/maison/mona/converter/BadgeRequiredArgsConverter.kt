package com.maison.mona.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.maison.mona.entity.BadgeRequiredArgs

class BadgeRequiredArgsConverter {

    @TypeConverter
    fun toRequiredArgs(json: String?): BadgeRequiredArgs?{
        val type = object : TypeToken<BadgeRequiredArgs?>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(badgeRequiredArgs: BadgeRequiredArgs?): String?{
        val type = object: TypeToken<BadgeRequiredArgs?>() {}.type
        return Gson().toJson(badgeRequiredArgs, type)
    }
}