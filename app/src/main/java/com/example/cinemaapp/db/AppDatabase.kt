package com.example.cinemaapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cinemaapp.domain.FilmItem

@Database(entities = [FilmItem::class], version = 2)
@TypeConverters(Converter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun filmItemDao(): FilmItemDao

    companion object {
        private lateinit var db: AppDatabase
        private lateinit var filmItemDao: FilmItemDao

        fun initDb(context: Context): AppDatabase {
            db = createDb(context)
            filmItemDao = db.filmItemDao()

            return db
        }

        private fun createDb(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "database.db"
            ).fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}