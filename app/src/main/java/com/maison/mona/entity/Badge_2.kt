package com.maison.mona.entity

import androidx.room.Entity
import com.squareup.moshi.JsonClass
import java.io.Serializable

@Entity(tableName = "badge_table")
@JsonClass(generateAdapter = true)
data class Badge_2(
    val name: String?,

    //ars_oblig
    val collected_goal: Int,

    var already_collected: Int = 0,

    //args_opt
    val quartier: String? = null,

    val args_opt: List<String?> = listOf(quartier),

    //general
    var isCollected: Boolean?
) : Serializable