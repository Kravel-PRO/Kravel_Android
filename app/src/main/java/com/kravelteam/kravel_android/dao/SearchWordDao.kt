package com.kravelteam.kravel_android.dao

import androidx.room.*
import com.kravelteam.kravel_android.data.common.SearchWord
import org.jetbrains.annotations.NotNull

@Dao
interface SearchWordDao {

    @Query("SELECT * FROM searchWord")
    fun getAll(): MutableList<SearchWord>

    @Query("SELECT * FROM searchWord WHERE word LIKE :word")
    fun findWord(word: String): SearchWord

    @Insert
    fun insertWord(word: SearchWord)

    @Query("DELETE FROM searchWord WHERE word = :word")
    fun deleteWord(word: String)
}