import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.mona.Entity.Artist
import com.example.mona.Entity.Bilingual
import com.example.mona.Entity.Location
import com.example.mona.Entity.Oeuvre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class OeuvreConverter {
    @TypeConverter
    fun toBilingual(value: String?): Bilingual? {
        return Gson().fromJson(value,Bilingual::class.java)
    }

    @TypeConverter
    fun fromBilingual(bilingual: Bilingual?): String? {
        return Gson().toJson(bilingual,Bilingual::class.java)
    }

    @TypeConverter
    fun toListAny(value: String?) : List<Any>?{
        val anyType = object : TypeToken<List<Any>?>() {}.type
        return Gson().fromJson(value, anyType)
    }

    @TypeConverter
    fun fromListAny(listAny: List<Any>?) : String?{
        val anyType = object : TypeToken<List<Any>?>() {}.type
        return Gson().toJson(listAny, anyType)
    }

    @TypeConverter
    fun toListBilingual(value: String?) : List<Bilingual>?{
        val bilingualList = object : TypeToken<List<Bilingual>?>() {}.type
        return Gson().fromJson(value,bilingualList)
    }

    @TypeConverter
    fun fromListBilingual(listBilingual: List<Bilingual>?) : String?{
        return Gson().toJson(listBilingual)
    }

    @TypeConverter
    fun toListArtists(value: String?) : List<Artist>?{
        val artistList = object : TypeToken<List<Artist>?>() {}.type
        return Gson().fromJson(value,artistList)
    }

    @TypeConverter
    fun fromListArtists(listArtists: List<Artist>?) : String?{
        return Gson().toJson(listArtists)
    }

    @TypeConverter
    fun toLocation(value: String?) : Location?{
        return Gson().fromJson(value,Location::class.java)
    }

    @TypeConverter
    fun fromLocation(location: Location?) : String?{
        return Gson().toJson(location)
    }
}