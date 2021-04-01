package com.maison.mona.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.maison.mona.activities.MyGlobals
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

abstract class BadgeDatabase : RoomDatabase() {
    abstract fun badgesDAO(): BadgeDAO
    init{

    }
    private class OeuvreDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val badgesDao = database.badgesDAO()
                    //If not connected
                    if( SaveSharedPreference.isOnline(mContext)//In online mode
                        && MyGlobals(mContext!!).isNetworkConnected()){//and actually connected
                        try{
                            Log.d("Save","accede database")
                            val badgesList = getBadgesList()
                            badgesDao.insertAll(badgesList)
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

            val badgesJson = BadgeTask(lastUpdate)
            val badgesArray = JSONArray(badgesJson)

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
        fun getInstance(): BadgeDatabase?{
            val instance = INSTANCE
            return instance
        }

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): BadgeDatabase {
            mContext = context
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BadgeDatabase::class.java,
                    "badges-database"
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