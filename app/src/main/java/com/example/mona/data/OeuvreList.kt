package com.example.mona.data

// All data properties of an artwork
// Bult in Moshi Adapter translates them directly into its instance
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable


@Entity(tableName = "oeuvre_list_table")
@JsonClass(generateAdapter = true)
data class OeuvreList(

    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @field:Json(name = "first") var first: String?,
    @field:Json(name = "next") var next: String?,
    @field:Json(name = "last") var last: String?,
    @field:Json(name = "previous") var previous: String?

    ): Serializable