package com.example.fullcreativeassignment.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fullcreativeassignment.home.model.BookedEvent

@Entity(tableName = "booked_events")
data class BookedEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val slotId: String,
    val slotName: String,
    val startTime: String,
    val endTime: String,
    val customerName: String,
    val phoneNumber: String,
)

fun BookedEventEntity.toEvent() =
    BookedEvent(
        slotId = slotId,
        name = slotName,
        startTime = startTime,
        endTime = endTime,
        bookedBy = customerName,
        phoneNumber = phoneNumber,
    )
