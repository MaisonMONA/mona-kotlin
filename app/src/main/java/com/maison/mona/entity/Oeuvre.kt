package com.maison.mona.entity

// All data properties of an artwork
// Bult in Moshi Adapter translates them directly into its instance
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.maison.mona.converter.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@Entity(tableName = "artwork_table")
@JsonClass(generateAdapter = true)
data class Oeuvre(

    @PrimaryKey(autoGenerate = false)
    var id: Int,
    var idServer: Int?,
    var title: String?,

    var produced_at: String?,
    var produced_end: String?,
    var source: String?,
    var borough: String?,
    var territory: String?,
    //var area: String?,
    var synthesis: String?,
    var description: String?,
    var status: String?,
    var id_rpcq: Int?,


    @TypeConverters(AdressesConverter::class)
    @field:Json(name = "addresses") var addresses: List<Any>?,

    @TypeConverters(SousUsagesConverter::class)
    @field:Json(name = "sous-usages") var sousUsages: List<Any>?,

    @TypeConverters(BilingualConverter::class)
    @field:Json(name = "category") var category: Bilingual?,

    @TypeConverters(BilingualConverter::class)
    @field:Json(name = "subcategory") var subcategory: Bilingual?,

    @TypeConverters(DimensionConverter::class)
    @field:Json(name = "dimensions") var dimension: List<Any>?,

    @TypeConverters(BilingualListConverter::class)
    @field:Json(name = "materials") var materials: List<Bilingual>?,

    @TypeConverters(BilingualListConverter::class)
    @field:Json(name = "techniques") var techniques: List<Bilingual>?,

    @TypeConverters(ArtistConverter::class)
    @field:Json(name = "artists") var artists: List<Artist>?,



    @TypeConverters(LocationConverter::class)
    @field:Json(name = "location") var location: Location?,

    var collection: String?,

    var details: String?,

    var type: String?,

    //Collection
    var state: Int?,

    var rating: Float?,

    var comment: String?,

    var photo_path: String?,

    var date_photo: String?,

) : Serializable{
    @Transient
    var distance: Double? = null

    @Transient
    var isSent: Boolean = false
}