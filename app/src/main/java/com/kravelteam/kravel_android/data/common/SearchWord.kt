package com.kravelteam.kravel_android.data.common

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searchWord")
data class SearchWord(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "word")
    val word: String
)