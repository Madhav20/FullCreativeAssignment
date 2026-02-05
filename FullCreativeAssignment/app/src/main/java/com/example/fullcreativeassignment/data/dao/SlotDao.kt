package com.example.fullcreativeassignment.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fullcreativeassignment.data.entity.BookedEventEntity
import com.example.fullcreativeassignment.data.entity.SlotsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SlotDao {
    @Query("SELECT * FROM slots WHERE isBooked = 0")
    fun getAllAvailableSlots(): Flow<List<SlotsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: SlotsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlots(slots: List<SlotsEntity>)

    @Update
    suspend fun updateSlot(slot: SlotsEntity)

    @Query("SELECT COUNT(*) FROM slots")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookedEvent(event: BookedEventEntity)

    @Query("SELECT * FROM booked_events ORDER BY id DESC")
    fun getBookedEvents(): Flow<List<BookedEventEntity>>

    @Query("SELECT * FROM slots WHERE slotId = :slotId LIMIT 1")
    suspend fun getSlotById(slotId: String): SlotsEntity?

    @Query("UPDATE slots SET isBooked = 1 WHERE slotId = :slotId")
    suspend fun markSlotAsBooked(slotId: String)
}
