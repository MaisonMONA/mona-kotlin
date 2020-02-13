package com.example.mona.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.mona.dao.OeuvreDAO
import com.example.mona.entity.Oeuvre

@Database(entities = [Oeuvre::class], version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun oeuvreDao(): OeuvreDAO
}









