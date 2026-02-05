package com.example.fullcreativeassignment.availableslots

import com.example.fullcreativeassignment.availableslots.repository.AvailableSlotRepository
import com.example.fullcreativeassignment.utils.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAvailableSlotRepository : AvailableSlotRepository {
    private var flow: Flow<Status<List<Slot>>> = flowOf()

    fun emitFlow(flow: Flow<Status<List<Slot>>>) {
        this.flow = flow
    }

    override suspend fun getAvailableSlots(): Flow<Status<List<Slot>>> = flow
}
