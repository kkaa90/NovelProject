package com.e.myapplication.notification

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Notify::class], version = 1)
abstract class NotifyDB : RoomDatabase() {
    abstract fun dao() : NotifyDao
}