package com.example.mona

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// All data properties of an artwork
// Bult in Moshi Adapter translates them directly into its instance
@JsonClass(generateAdapter = true)
data class Oeuvre(
    val id: Int?,
    val title: String?,
    val date: String?,
    @field:Json(name = "category") val category: Bilingual?,
    @field:Json(name = "subcategory") val subcategory: Bilingual?,
    @field:Json(name = "dimensions") val dimension: List<Any>?,
    @field:Json(name = "materials") val materials: List<Bilingual>?,
    @field:Json(name = "techniques") val techniques: List<Bilingual>?,
    @field:Json(name = "artists") val artists: List<Artist>?,
    val borough: String?,
    @field:Json(name = "location") val location: Location?,
    val collection: String?,
    val details: String?
)

data class Bilingual(
    val fr: String?,
    val en: String?
)

data class Location(
    val lat: Double?,
    val lng: Double?
)

data class Artist(
    val id: Int?,
    val name: String?,
    val alias: String?,
    val collective: Boolean?

)
