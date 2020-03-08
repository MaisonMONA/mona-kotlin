package com.example.mona.entity

import java.io.Serializable


data class Artist(
    var id: Int,
    var name: String,
    var alias: String,
    var collective: Boolean

) : Serializable