package com.example.fullcreativeassignment.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fullcreativeassignment.data.dao.SlotDao
import com.example.fullcreativeassignment.data.entity.BookedEventEntity
import com.example.fullcreativeassignment.data.entity.SlotsEntity

@Database(
    entities = [SlotsEntity::class, BookedEventEntity::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun slotDao(): SlotDao
}
