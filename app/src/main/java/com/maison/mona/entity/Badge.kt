package com.maison.mona.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.maison.mona.converter.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@Entity(tableName = "badge_table")
@JsonClass(generateAdapter = true)
data class Badge(
    @PrimaryKey(autoGenerate = true)
    val id:Int,

    val title_fr: String?,
    val title_en: String?,

    val description_fr: String?,
    val description_en: String?,

    val notif_fr: String?,
    val notif_en: String?,

    val image: String?,

    val action: String?,

    val required_args: String?,

    val optional_args: String?,

//    @TypeConverters(BadgeRequiredArgsConverter::class)
//    @field:Json(name = "required_args") var required_args: BadgeRequiredArgs?,
//
//    @TypeConverters(BadgeOptArgsConverter::class)
//    @field:Json(name = "optional_args") var opt_args: BadgeOptArgs?,

    val produced_at: String?,

    val updated_at: String?,

    val visibility: String?,

    var isCollected: Boolean = false,

    var goal: Int? = 0

) : Serializable {
    @Transient
    var collected: Int = 0
}