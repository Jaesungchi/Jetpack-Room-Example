package com.kotlin.jaesungchi.jetpack_room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "writeData")
data class WriteDataEntity(@PrimaryKey(autoGenerate = true) val id:Long,
                           var title: String,
                           var content: String,
                           var date : String)