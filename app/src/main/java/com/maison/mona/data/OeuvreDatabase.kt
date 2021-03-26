package com.maison.mona.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.maison.mona.activities.MyGlobals
import com.maison.mona.converter.*
import com.maison.mona.entity.Oeuvre
import com.maison.mona.task.ArtworksTask
import com.maison.mona.task.PlacesTask
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.IOException
import java.sql.Timestamp


@Database(entities = arrayOf(Oeuvre::class), version = 1, exportSchema = false)
@TypeConverters(
    ArtistConverter::class,
    BilingualConverter::class,
    BilingualListConverter::class,
    DimensionConverter::class,
    LocationConverter::class)
abstract class OeuvreDatabase : RoomDatabase() {

    abstract fun oeuvreDAO(): OeuvreDAO
    init{

    }
    private class OeuvreDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
           super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val oeuvreDao = database.oeuvreDAO()
                    //If not connected
                    Log.d("Save", "Online initial: " + SaveSharedPreference.isOnline(mContext).toString())
                    if( SaveSharedPreference.isOnline(mContext)//In online mode
                        && MyGlobals(mContext!!).isNetworkConnected()){//and actually connected
                        try{
                            Log.d("Save","accede database")
                            val oeuvreList = getOeuvreList()
                            oeuvreDao.insertAll(oeuvreList)
                        }catch (e: IOException){
                            Log.d("Save","erreur database")
                        }
                    }else{
                        SaveSharedPreference.setOnline(mContext,false)
                    }
                }
            }
        }
/*
fun getOeuvreList(): List<Oeuvre>?{

   val finalList : MutableList<Oeuvre> = mutableListOf()
   var id = 1
   var idOeuvre = 1
   var idPlace  = 1

   //Manually fill in the maximum number of pages
   for (index in 1..20){
       //API call to server to get all artworks and places
       //We combine the 2 in one lists
       val artworksJson = ArtworksTask(index).execute().get()
       val objectJson = JSONObject(artworksJson)
       val oeuvreArray = objectJson.getJSONArray("data")
       val dataArray = oeuvreArray.toString()
       val length = dataArray.length
       val nbArtworks = oeuvreArray.length()
       var data2Array = "[]"

       var placeArray = JSONArray("[]")
       if(index <= 9){
           val placeJson = PlacesTask(index).execute().get()
           val placeObjectJson = JSONObject(placeJson)
           placeArray = placeObjectJson.getJSONArray("data")
           data2Array = placeArray.toString()
       }

       var articleArray: String

       if(dataArray.length <= 2){//if we have a empty oeuvre array
           articleArray = data2Array
       }else if(data2Array.length <= 2){//if we have a empty place array
           articleArray = oeuvreArray.toString()
       }else{//Add the place array next to the oeuvre array
           articleArray =  oeuvreArray.put(placeArray).toString()
           articleArray = articleArray.substring(0,length) + articleArray.substring(length + 1,articleArray.length - 1)
       }

       //Moshi is a library with built in type adapters to ease data parsing such as our case.
       //For every artwork, it creates an artwork instance and copies the right keys from the json artwork into the instance artwork
       val moshi = Moshi.Builder()
           .add(KotlinJsonAdapterFactory())
           .build()

       //Since we have more than one artwork, we want to create a list of all objects of type artwork to which Moshi
       //efficiently loops through automatically with its adapter
       val type = Types.newParameterizedType(List::class.java, Oeuvre::class.java)
       val adapter: JsonAdapter<List<Oeuvre>> = moshi.adapter(type)
       val oeuvreList: List<Oeuvre>? = adapter.fromJson(articleArray)

       val changed_list = oeuvreList?.toMutableList()
       if (changed_list != null) {
           var index = 1
           for(oeuvre: Oeuvre in changed_list) {
               if(index++ <= nbArtworks){
                   oeuvre.type = "artwork"
                   oeuvre.idServer = idOeuvre++
               }else{
                   oeuvre.type = "place"
                   oeuvre.idServer = idPlace++
               }
               oeuvre.id = id++
           }
       }
       finalList.let{
           changed_list?.let(finalList::addAll)
       }
   }
   return finalList
}
*/

fun getOeuvreList(): List<Oeuvre>?{
    Log.d("Main","rentre ici")
    val finalList : MutableList<Oeuvre> = mutableListOf()
    var id = 1
    var idOeuvre = 1
    var idPlace  = 1
    //localhost:8000/api/lastUpdatedPlaces?date=2015-12-19 17:36:22.444
    var lastUpdate = SaveSharedPreference.getLastUpdate(mContext)
    Log.d("Database: ", "Last update: $lastUpdate")
    //Manually fill in the maximum number of pages
    //API call to server to get all artworks and places
    //We combine the 2 in one lists

    var artworksJson = ArtworksTask(lastUpdate).execute().get()
    if(artworksJson == null){
        return mutableListOf()
    }
    var oeuvreArray = JSONArray(artworksJson)
    //val oeuvreArray = objectJson.getJSONArray("data")
    var nbArtworks = oeuvreArray.length()//Stores the value for the amount of Artworks
    //var nbArtworks = 0
    Log.d("Database", "Nb Artworks: $nbArtworks")

    val placeJson = PlacesTask(lastUpdate).execute().get()
    //Log.d("Database", "Liste Places:\n$placeJson")
   // val placeObjectJson = JSONObject(placeJson)
    //var placeArray = placeObjectJson.getJSONArray("data")
    var placeArray = JSONArray(placeJson)
    var articleArray = JSONArray()
    Log.d("Database", "Nb Lieu: ${placeArray.length()}")
    for(i in 0 until nbArtworks){
        articleArray.put(oeuvreArray.get(i))
    }
    for(i in 0 until placeArray.length()){
        articleArray.put(placeArray.get(i))
    }
    Log.d("Database", "Total: ${articleArray.length()}")
    //Moshi is a library with built in type adapters to ease data parsing such as our case.
    //For every artwork, it creates an artwork instance and copies the right keys from the json artwork into the instance artwork
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    //Since we have more than one artwork, we want to create a list of all objects of type artwork to which Moshi
    //efficiently loops through automatically with its adapter
    val type = Types.newParameterizedType(List::class.java, Oeuvre::class.java)
    val adapter: JsonAdapter<List<Oeuvre>> = moshi.adapter(type)
    val oeuvreList: List<Oeuvre>? = adapter.fromJson(articleArray.toString())

    val changed_list = oeuvreList?.toMutableList()
    if (changed_list != null) {
        var index = 1
        for(oeuvre: Oeuvre in changed_list) {
            if(index++ <= nbArtworks){
                oeuvre.type = "artwork"
                oeuvre.idServer = idOeuvre++
            }else{
                oeuvre.type = "place"
                oeuvre.idServer = idPlace++
            }
            oeuvre.id = id++
        }
    }
    finalList.let{
        changed_list?.let(finalList::addAll)
    }
    var currentTime = Timestamp(System.currentTimeMillis())
    Log.d("Database",currentTime.toString())
    SaveSharedPreference.setLastUpdate(mContext, currentTime.toString())
    return finalList
}
}

//getDatabase returns the singleton. It'll create the database the first
// time it's accessed, using Room's database builder to create a RoomDatabase
// object in the application context from the WordRoomDatabase class and
// names it "word_database".
companion object {

@Volatile
private var INSTANCE: OeuvreDatabase? = null
private var mContext: Context? = null
fun getInstance(): OeuvreDatabase?{
   val instance = INSTANCE
   return instance
}

fun getDatabase(
   context: Context,
   scope: CoroutineScope
): OeuvreDatabase {
    mContext = context
   // if the INSTANCE is not null, then return it,
   // if it is, then create the database
   return INSTANCE ?: synchronized(this) {

       val instance = Room.databaseBuilder(
           context.applicationContext,
           OeuvreDatabase::class.java,
           "artwork-database"
       )
           .addCallback(OeuvreDatabaseCallback(scope))
           .build()

       INSTANCE = instance
       // return instance
       instance
   }
}
}
}