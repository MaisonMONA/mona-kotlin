package com.maison.mona.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.maison.mona.activities.MyGlobals
import com.maison.mona.converter.*
import com.maison.mona.entity.Oeuvre
import com.maison.mona.task.PlacesTask
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.sql.Timestamp

@Database(entities = [Oeuvre::class], version = 1, exportSchema = false)
@TypeConverters(
    ArtistConverter::class,
    BilingualConverter::class,
    BilingualListConverter::class,
    DimensionConverter::class,
    LocationConverter::class)
abstract class OeuvreDatabase : RoomDatabase() {

    abstract fun oeuvreDAO(): OeuvreDAO

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
                            Log.d("Save","oeuvre accede database")



                            val oeuvreList = getOeuvreList()
                            oeuvreDao.insertAll(oeuvreList)
                        }catch (e: IOException){
                            e.printStackTrace()
                            Log.d("Save","erreur database")
                        }
                    }else{
                        SaveSharedPreference.setOnline(mContext,false)
                    }
                }
            }
        }

        fun getOeuvreList(): List<Oeuvre> {
            val finalList : MutableList<Oeuvre> = mutableListOf()
            var id = 1
            var idOeuvre = 1
            var idPlace  = 1
            //localhost:8000/api/lastUpdatedPlaces?date=2015-12-19 17:36:22.444

            val lastUpdate = SaveSharedPreference.getLastUpdate(mContext)
            Log.d("Database: ", "Last update: $lastUpdate")
            //Manually fill in the maximum number of pages
            //API call to server to get all artworks and places
            //We combine the 2 in one lists

            //val artworksJson: String = ArtworksTask(lastUpdate).execute().get() ?: return mutableListOf()

           // Log.d("Database", "Nb Artworks: $nbArtworks")

            //Temporary fix to be able to import the json without problem
            val artworksJson = getJsonDataFromAsset(mContext!!, "artworks.json")

            val oeuvreArray = JSONArray(artworksJson)
            val nbArtworks = oeuvreArray.length()//Stores the value for the amount of Artworks

            //val placeJson = PlacesTask(lastUpdate).execute().get()
           // val placeArray = JSONArray(placeJson)
            val placeJson = getJsonDataFromAsset(mContext!!, "places.json")
            val placeArray = JSONArray(placeJson)
           // val temp = JSONObject(placeJson)
            //val tempArray = temp.toJSONArray(temp.names())


            //val heritageJson = HeritagesTask(lastUpdate).execute().get()
            //val heritagesArray = JSONArray(heritageJson)

            val articleArray = JSONArray()

            Log.d("Database", "Nb Arts: ${nbArtworks}")

            Log.d("Database", "Nb Lieu: ${placeArray.length()}")


           for(i in 0 until nbArtworks){
                articleArray.put(oeuvreArray.get(i))

           }

            for(i in 0 until placeArray.length()){
                articleArray.put(placeArray.get(i))

            }
/*
            for(i in 0 until tempArray.length()){
                articleArray.put(tempArray.get(i))
          }
*/
            Log.d("Database", "Total: ${articleArray.length()}")
            //Log.d("patrimoine", "patrimoine: ${heritagesArray.length()}")
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

            val changedList = oeuvreList?.toMutableList()
            if (changedList != null) {
                var index = 1

                for(oeuvre: Oeuvre in changedList) {

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
                changedList?.let(finalList::addAll)
            }

            val currentTime = Timestamp(System.currentTimeMillis())

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

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: OeuvreDatabase? = null
        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null

        fun getInstance(): OeuvreDatabase? {
            return INSTANCE
        }

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): OeuvreDatabase {
            mContext = context
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                Log.d("SAVE", "oeuvre get database")

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OeuvreDatabase::class.java,
                    "artwork-database"
                )
                    .addMigrations(MIGRATION_0_1)
                    .addCallback(OeuvreDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
//Migration Manuel
val MIGRATION_0_1 = object : Migration(0,1){
    override fun migrate(database: SupportSQLiteDatabase){
        database.execSQL("ALTER TABLE artwork_table ADD COLUMN idServer INT")
        database.execSQL("ALTER TABLE artwork_table ADD COLUMN type TEXT")
    }
}

fun getJsonDataFromAsset(context: Context, fileName: String): String? {
    val jsonString: String?
    try {
        jsonString = context.assets?.open(fileName)
            ?.bufferedReader().use { it?.readText() }
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
    return jsonString
}