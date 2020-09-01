package com.kravelteam.kravel_android.common

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kravelteam.kravel_android.dao.SearchWordDao
import com.kravelteam.kravel_android.data.common.SearchWord

@Database(entities = [SearchWord::class],version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun searchWordDao() : SearchWordDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "search_db"
                    ).build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance(){
            INSTANCE = null
        }
    }
}