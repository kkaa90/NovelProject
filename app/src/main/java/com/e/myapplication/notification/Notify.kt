package com.e.myapplication.notification

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Notify(

    @ColumnInfo(name = "title") var title : String?,
    @ColumnInfo(name = "body") var body: String?
){
    @PrimaryKey(autoGenerate = true) var num: Int = 0
}
