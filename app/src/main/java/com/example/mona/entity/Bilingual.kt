package com.example.mona.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "bilingual")
data class Bilingual(
    @ColumnInfo(name = "fr") val fr: String?,
    @ColumnInfo(name = "en") val en: String?
)