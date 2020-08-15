package com.example.mona.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mona.converter.*
import com.example.mona.entity.Article
import com.example.mona.entity.Lieu
import com.example.mona.task.ArtworksTask
import com.example.mona.task.PlacesTask
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject


@Database(entities = arrayOf(Article::class), version = 1, exportSchema = false)
@TypeConverters(
    ArtistConverter::class,
    BilingualConverter::class,
    BilingualListConverter::class,
    DimensionConverter::class,
    LocationConverter::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun articleDAO(): ArticleDAO

    private class ArticleDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    Log.d("ARTICLE","DATABASE CALLBACK")
                    val articleDAO = database.articleDAO()

                    var articleList = getArticleList()
                    if (articleList != null) {
                        Log.d("ARTICLE", articleList.size.toString())
                    }
                    articleDAO.insertAll(articleList)
                }
            }

        }
        fun getArticleList(): List<Article>?{
            val finalList : MutableList<Article> = mutableListOf()
            Log.d("ARTICLE", "getLIST")
            for (index in 1..9) {
                //API call to server to get all artworks. We extract solely the artworks
                val artworksJson =  ArtworksTask(index).execute().get()
                val object1Json = JSONObject(artworksJson)
                //Lieu
                val placesJson = PlacesTask(index).execute().get()
                val object2Json = JSONObject(placesJson)
                //Article
                val articles = JSONObject()
                articles.put("artorks",object1Json)
                articles.put("places",object1Json)
                val dataArray = articles.getJSONArray("data").toString()
                //Moshi is a library with built in type adapters to ease data parsing such as our case.
                //For every artwork, it creates an artwork instance and copies the right keys from the json artwork into the instance artwork
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                //Since we have more than one artwork, we want to create a list of all objects of type artwork to which Moshi
                //efficiently loops through automatically with its adapter
                val type = Types.newParameterizedType(List::class.java, Article::class.java)
                val adapter: JsonAdapter<List<Article>> = moshi.adapter(type)
                val articleList: List<Article>? = adapter.fromJson(dataArray)

                val changed_list = articleList?.toMutableList()

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
        private var INSTANCE: ArticleDatabase? = null
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ArticleDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            Log.d("ARTICLE","FICHIER")
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "article-database"
                )
                    .addCallback(
                        ArticleDatabaseCallback(
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
