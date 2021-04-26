package com.maison.mona.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.maison.mona.activities.MyGlobals
import com.maison.mona.converter.BadgeOptArgsConverter
import com.maison.mona.converter.BadgeRequiredArgsConverter
import com.maison.mona.entity.BadgeOptArgs
import com.maison.mona.entity.Badge_2
import com.maison.mona.task.BadgeTask
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.IOException
import java.sql.Timestamp

@Database(entities = arrayOf(Badge_2::class), version = 1, exportSchema = false)
@TypeConverters(
    BadgeRequiredArgsConverter::class,
    BadgeOptArgsConverter::class
)
abstract class BadgeDatabase : RoomDatabase() {

    abstract fun badgesDAO(): BadgeDAO

    private class BadgeDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)

            Log.d("SAVE", "badge database on open called")

            INSTANCE?.let { database ->
                scope.launch {
                    val badgesDao = database.badgesDAO()
                    //If not connected
                    if( SaveSharedPreference.isOnline(mContext)//In online mode
                        && MyGlobals(mContext!!).isNetworkConnected()){//and actually connected
                        try{
                            Log.d("Save","badge accede database")
                            
                            val badgesList = getBadgesList()

                            badgesDao.insertAll(badgesList)

                            Log.d("SAVE", "fin onOpen" + badgesList.toString())
                        }catch (e: IOException){
                            Log.d("Save","erreur database")
                        }
                    }else{
                        SaveSharedPreference.setOnline(mContext,false)
                    }
                }
            }
        }

        fun getBadgesList(): List<Badge_2>?{
            var lastUpdate = SaveSharedPreference.getLastUpdate(mContext)

//            Log.d("SAVE", "inside getBadgesList : lastUpdate")

            val badgesJson = BadgeTask(lastUpdate).execute().get().subSequence(8, 3031).toString()

//            Log.d("SAVE", "inside getBadgesList : badgesJson : " + badgesJson)
            //stringtoJSON

            if(badgesJson == null){
                return mutableListOf()
            }

            val badgesArray = JSONArray(badgesJson)
            
//            Log.d("SAVE", "inside getBadgesList : badgesArray : " + badgesArray.toString())

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val type = Types.newParameterizedType(List::class.java, Badge_2::class.java)
            val adapter: JsonAdapter<List<Badge_2>> = moshi.adapter(type)
            val badgesList: List<Badge_2>? = adapter.fromJson(badgesArray.toString())

            var currentTime = Timestamp(System.currentTimeMillis())
            SaveSharedPreference.setLastUpdate(mContext, currentTime.toString())

            return badgesList
        }
    }

    //getDatabase returns the singleton. It'll create the database the first

    // time it's accessed, using Room's database builder to create a RoomDatabase
// object in the application context from the WordRoomDatabase class and
// names it "word_database".
    companion object {

        @Volatile
        private var INSTANCE: BadgeDatabase? = null
        private var mContext: Context? = null

//        fun getInstance(): BadgeDatabase?{
//            val instance = INSTANCE
//            return instance
//        }

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): BadgeDatabase {
            mContext = context
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                Log.d("SAVE","badge get database ici")

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BadgeDatabase::class.java,
                    "badges-database"
                )
                    .addCallback(BadgeDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}