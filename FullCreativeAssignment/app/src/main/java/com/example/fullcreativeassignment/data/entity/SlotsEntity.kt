package com.example.fullcreativeassignment.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fullcreativeassignment.availableslots.Slot

@Entity(tableName = "slots")
data class SlotsEntity(
    @PrimaryKey val slotId: String,
    val name: String,
    val startTime: String,
    val endTime: String,
    val isBooked: Boolean = false,
)

fun SlotsEntity.toEvent() =
    Slot(
        slotId = slotId,
        name = name,
        startTime = startTime,
        endTime = endTime,
    )

fun Slot.toEntity() =
    SlotsEntity(
        slotId = slotId,
        name = name,
        startTime = startTime,
        endTime = endTime,
    )
