package com.example.mona.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "bilingual")
data class Bilingual(
    @ColumnInfo(name = "fr") val fr: String?,
    @ColumnInfo(name = "en") val en: String?
)