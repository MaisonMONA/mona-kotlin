package com.example.mona.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mona.converter.*
import com.example.mona.entity.Lieu
import com.example.mona.task.PlacesTask
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject


@Database(entities = arrayOf(Lieu::class), version = 1, exportSchema = false)
@TypeConverters(
    BilingualConverter::class,
    LocationConverter::class)
abstract class LieuDatabase : RoomDatabase() {

    abstract fun lieuDAO(): LieuDAO

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {

                    val lieuDAO = database.lieuDAO()

                    var lieuList = getLieuList()

                    lieuDAO.insertAll(lieuList)
                }
            }
        }

        fun getLieuList(): List<Lieu>?{
            val finalList : MutableList<Lieu> = mutableListOf()

            for (index in 1..9) {
                //API call to server to get all artworks. We extract solely the artworks
                val artworksJson = PlacesTask(index).execute().get()
                val objectJson = JSONObject(artworksJson)
                val dataArray = objectJson.getJSONArray("data").toString()

                //Moshi is a library with built in type adapters to ease data parsing such as our case.
                //For every artwork, it creates an artwork instance and copies the right keys from the json artwork into the instance artwork
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                //Since we have more than one artwork, we want to create a list of all objects of type artwork to which Moshi
                //efficiently loops through automatically with its adapter
                val type = Types.newParameterizedType(List::class.java, Lieu::class.java)
                val adapter: JsonAdapter<List<Lieu>> = moshi.adapter(type)
                val lieuList: List<Lieu>? = adapter.fromJson(dataArray)

                val changed_list = lieuList?.toMutableList()

                finalList.let{
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
        private var INSTANCE: LieuDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): LieuDatabase {

            // if the INSTANCE is not null, then return it,
            // if it is, then create the database

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        LieuDatabase::class.java,
                        "places-database"
                    )
                    .addCallback(
                        WordDatabaseCallback(
                            scope
                        )
                    )
                    .build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}