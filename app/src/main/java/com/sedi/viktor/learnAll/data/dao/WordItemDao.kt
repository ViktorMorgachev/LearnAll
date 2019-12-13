package com.sedi.viktor.learnAll.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.models.WordItemRoomModel

@Dao
interface WordItemDao {


    @Query("SELECT * FROM worditemroommodel")
    fun getAll(): List<WordItem>

    @Insert
    fun insert(wordItem: WordItemRoomModel)

    @Delete
    fun delete(wordItem: WordItem)

    @Query("SELECT * FROM worditemroommodel WHERE learned = 'true' ")
    fun getLearned(): List<WordItem>

    @Query("SELECT * FROM worditemroommodel WHERE learned = 'false' ")
    fun getUnlearned(): List<WordItem>

}