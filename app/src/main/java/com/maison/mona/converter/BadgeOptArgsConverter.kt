package com.maison.mona.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.maison.mona.entity.BadgeOptArgs

class BadgeOptArgsConverter {
    @TypeConverter
    fun toOptArgs(json: String?): BadgeOptArgs?{
        val type = object : TypeToken<BadgeOptArgs?>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(badgeOptArgs: BadgeOptArgs?): String?{
        val type = object: TypeToken<BadgeOptArgs?>() {}.type
        return Gson().toJson(badgeOptArgs, type)
    }
}