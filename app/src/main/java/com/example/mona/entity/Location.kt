package com.example.mona.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "location")
data class Location(
    @ColumnInfo(name = "lat") val lat: Double?,
    @ColumnInfo(name = "lng") val lng: Double?
)