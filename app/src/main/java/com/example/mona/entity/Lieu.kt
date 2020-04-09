package com.example.mona.entity

// All data properties of an artwork
// Bult in Moshi Adapter translates them directly into its instance
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mona.converter.*
import com.example.mona.entity.Bilingual
import com.example.mona.entity.Location
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable


@Entity(tableName = "place_table")
@JsonClass(generateAdapter = true)
data class Lieu(

    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    var title: String?,

    @TypeConverters(BilingualConverter::class)
    @field:Json(name = "category") var category: Bilingual?,

    var borough: String?,

    @TypeConverters(LocationConverter::class)
    @field:Json(name = "location") var location: Location?


) : Serializable