package com.example.fullcreativeassignment.home

import com.example.fullcreativeassignment.home.model.BookedEvent
import com.example.fullcreativeassignment.home.repository.BookedSlotsRepository
import com.example.fullcreativeassignment.utils.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeBookedSlotsRepository : BookedSlotsRepository {
    private var flow: Flow<Status<List<BookedEvent>>> = flowOf()

    fun emitFlow(flow: Flow<Status<List<BookedEvent>>>) {
        this.flow = flow
    }

    override suspend fun getBookedSlots(): Flow<Status<List<BookedEvent>>> = flow
}
