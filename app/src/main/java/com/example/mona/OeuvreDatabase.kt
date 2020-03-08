package com.example.mona

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mona.converter.*
import com.example.mona.dao.OeuvreDAO
import com.example.mona.entity.Oeuvre
import com.example.mona.task.ArtworksTask
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject


@Database(entities = arrayOf(Oeuvre::class), version = 1, exportSchema = false)
@TypeConverters(
    ArtistConverter::class,
    BilingualConverter::class,
    BilingualListConverter::class,
    DimensionConverter::class,
    LocationConverter::class)
abstract class OeuvreDatabase : RoomDatabase() {

    abstract fun oeuvreDAO(): OeuvreDAO

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {

                    var oeuvreDao = database.oeuvreDAO()

                    var oeuvreList = getOeuvreList()

                    oeuvreDao.insertAll(oeuvreList)
                }
            }
        }

        fun getOeuvreList(): List<Oeuvre>?{

            val finalList : MutableList<Oeuvre> = mutableListOf()

            for (index in 1..15){
                //API call to server to get all artworks. We extract solely the artworks
                val artworksJson = ArtworksTask(index).execute().get()
                val objectJson = JSONObject(artworksJson)
                val dataArray = objectJson.getJSONArray("data").toString()

                //Moshi is a library with built in type adapters to ease data parsing such as our case.
                //For every artwork, it creates an artwork instance and copies the right keys from the json artwork into the instance artwork
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                //Since we have more than one artwork, we want to create a list of all objects of type artwork to which Moshi
                //efficiently loops through automatically with its adapter
                val type = Types.newParameterizedType(List::class.java, Oeuvre::class.java)
                val adapter: JsonAdapter<List<Oeuvre>> = moshi.adapter(type)
                val oeuvreList: List<Oeuvre>? = adapter.fromJson(dataArray)

                val changed_list = oeuvreList?.toMutableList()

                finalList?.let{ finalList ->
                    changed_list?.let(finalList::addAll)
                }
            }

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

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): OeuvreDatabase {

            // if the INSTANCE is not null, then return it,
            // if it is, then create the database

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OeuvreDatabase::class.java,
                    "artwork-database"
                )
                    .addCallback(WordDatabaseCallback(scope))
                    .allowMainThreadQueries()
                    .build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}