package com.sedi.viktor.learnAll

import android.util.Log
import com.sedi.viktor.learnAll.data.WordItemDatabase
import com.sedi.viktor.learnAll.data.dao.WordItemDao
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EntityReadWriteWordItemTest {

    private lateinit var wordItemDao: WordItemDao
    private lateinit var db: WordItemDatabase

    @Before
    fun createDB() {
        Log.d("LearnALL Test", "createDB()")
    }

    @After
    fun closeDB() {
        Log.d("LearnALL Test", "closeDB()")
    }


    @Test
    @Throws(Exception::class)
    fun writeWordCardAndReadInList() {
        Log.d("LearnALL Test", "writeWordCardAndReadInList()")
    }

}