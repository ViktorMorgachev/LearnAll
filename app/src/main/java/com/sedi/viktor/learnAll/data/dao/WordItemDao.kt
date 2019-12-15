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
    fun getAll(): List<WordItemRoomModel>

    @Insert
    fun insert(wordItem: WordItemRoomModel)

    @Delete
    fun delete(wordItem: WordItemRoomModel)

    @Query("SELECT * FROM worditemroommodel WHERE learned = 'true' ")
    fun getLearned(): List<WordItemRoomModel>

    @Query("SELECT * FROM worditemroommodel WHERE learned = 'false' ")
    fun getUnlearned(): List<WordItemRoomModel>

}