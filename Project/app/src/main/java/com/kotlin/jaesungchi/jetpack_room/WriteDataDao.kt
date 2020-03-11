package com.kotlin.jaesungchi.jetpack_room

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Maybe

@Dao
interface WriteDataDao : BaseDao<WriteDataEntity>{
    @Query("SELECT * FROM writeData WHERE id = :id")
    fun selectById(id : Int) : Array<WriteDataEntity>

    @Query("SELECT * FROM writeData")
    fun selectAll() : Array<WriteDataEntity>

    @Query("SELECT * FROM writeData WHERE date = :date")
    fun selectByDate(date : String ) : WriteDataEntity

    @Query("DELETE FROM writeData WHERE date = :date")
    fun deleteByDate(date : String)
}