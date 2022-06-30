package com.e.treenovel.notification

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Notify ADD COLUMN titleId INTEGER")
    }

}

@Database(
    entities = [Notify::class], version = 2,
)
abstract class NotifyDB : RoomDatabase() {
    abstract fun dao(): NotifyDao
}