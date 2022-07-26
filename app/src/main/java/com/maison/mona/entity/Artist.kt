package com.maison.mona.entity

import androidx.room.PrimaryKey
import java.io.Serializable

data class Artist(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var alias: String,
    var collective: Boolean,
) : Serializable