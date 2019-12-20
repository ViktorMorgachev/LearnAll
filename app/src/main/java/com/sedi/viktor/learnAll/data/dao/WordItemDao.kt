package com.sedi.viktor.learnAll.data.dao

import androidx.room.*
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

    @Update
    fun update(wordItem: WordItemRoomModel)

}