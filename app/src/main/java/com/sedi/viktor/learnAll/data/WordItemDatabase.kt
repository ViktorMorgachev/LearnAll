package com.sedi.viktor.learnAll.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sedi.viktor.learnAll.data.dao.WordItemDao
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.models.WordItemRoomModel

@Database(entities = arrayOf(WordItemRoomModel::class), exportSchema = false, version = 1)
abstract class WordItemDatabase : RoomDatabase() {

    abstract fun wordItemDao(): WordItemDao

    companion object {
        private val DB_NAME = "word_item_db"
        @Volatile
        private var instance: WordItemDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, WordItemDatabase::class.java, DB_NAME).build()
    }

}