package com.example.mona

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ArtistConverter {

    @TypeConverter
    fun toArtists(json: String): List<Artist> {
        val type = object : TypeToken<List<Artist>>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(artistList: List<Artist>): String {
        val type = object: TypeToken<List<Artist>>() {}.type
        return Gson().toJson(artistList, type)
    }

}