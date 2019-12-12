package com.sedi.viktor.learnAll.data

import androidx.room.Database
import com.sedi.viktor.learnAll.data.dao.WordItemDao
import com.sedi.viktor.learnAll.data.models.WordItem

@Database(entities = arrayOf(WordItem::class), version = 1)
abstract class AppDataBase {
    abstract fun wordItemDao(): WordItemDao
}