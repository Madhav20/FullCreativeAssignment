package com.example.fullcreativeassignment.availableslots.repository

import com.example.fullcreativeassignment.availableslots.Slot
import com.example.fullcreativeassignment.utils.Status
import kotlinx.coroutines.flow.Flow

interface AvailableSlotRepository {
    suspend fun getAvailableSlots(): Flow<Status<List<Slot>>>
}
