package com.sedi.viktor.learnAll.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sedi.viktor.learnAll.data.models.WordItem

@Dao
interface WordItemDao {


    @Query("SELECT * FROM worditem")
    fun getAll(): List<WordItem>

    @Insert
    fun insert(wordItem: WordItem)

    @Delete
    fun delete(wordItem: WordItem)

    @Query("SELECT * FROM worditem WHERE learned = 'true' ")
    fun getLearned(): List<WordItem>

    @Query("SELECT * FROM worditem WHERE learned = 'false' ")
    fun getUnlearned(): List<WordItem>

}