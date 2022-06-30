package com.e.treenovel.notification

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotifyDao {
    @Query("SELECT * FROM notify")
    fun getAll(): List<Notify>

    @Insert
    fun insert(vararg notifies: Notify)

    @Delete
    fun delete(notifies: Notify)

    @Query("DELETE from notify")
    fun deleteAll()
}