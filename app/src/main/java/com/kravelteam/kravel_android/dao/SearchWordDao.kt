package com.kravelteam.kravel_android.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kravelteam.kravel_android.data.common.SearchWord
import timber.log.Timber

@Dao
interface SearchWordDao {

    @Query("SELECT * FROM searchWord")
    fun getAll(): List<SearchWord>

    @Query("SELECT * FROM searchWord WHERE word LIKE :word")
    fun findWord(word: String): SearchWord

    @Insert
    fun insertWord(word: SearchWord)

    @Delete
    fun deleteWord(word: SearchWord)
}