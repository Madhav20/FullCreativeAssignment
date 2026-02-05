package com.example.fullcreativeassignment.bookslot

import com.example.fullcreativeassignment.availableslots.Slot
import com.example.fullcreativeassignment.bookslot.repository.BookingRepository
import com.example.fullcreativeassignment.utils.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeBookingRepository : BookingRepository {
    private var flow: Flow<Status<Unit>> = flowOf()

    var slotToReturn: Slot? = null

    fun emitFlow(flow: Flow<Status<Unit>>) {
        this.flow = flow
    }

    override suspend fun bookSlots(
        customerName: String,
        phoneNumber: String,
        slotId: String,
    ): Flow<Status<Unit>> = flow

    override suspend fun getSlotById(slotId: String): Slot? = slotToReturn
}
