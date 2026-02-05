package com.example.fullcreativeassignment.bookslot.repository

import com.example.fullcreativeassignment.availableslots.Slot
import com.example.fullcreativeassignment.utils.Status
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    suspend fun bookSlots(
        customerName: String,
        phoneNumber: String,
        slotId: String,
    ): Flow<Status<Unit>>

    suspend fun getSlotById(slotId: String): Slot?
}
