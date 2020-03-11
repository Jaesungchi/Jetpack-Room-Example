package com.kotlin.jaesungchi.jetpack_room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//데이터베이스 객체는 싱글톤으로 구성해 한개만 갖고 있도록 함.
@Database(entities = [WriteDataEntity::class],version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun WriteDao() : WriteDataDao
    companion object{
        private var INSTANCE : AppDatabase? = null
        fun getInstance(context : Context): AppDatabase?{
            if(INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java,"room_example.db").build()
                }
            }
            return INSTANCE
        }
    }
}