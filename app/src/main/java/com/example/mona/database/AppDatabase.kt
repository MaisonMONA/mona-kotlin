package com.example.mona.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mona.entity.Oeuvre
import com.example.mona.dao.OeuvreDAO
import com.example.mona.converter.OeuvreConverter

@Database(entities = [Oeuvre::class], version = 1,exportSchema = false)
@TypeConverters(OeuvreConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun oeuvreDao(): OeuvreDAO
}









