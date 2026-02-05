package com.example.fullcreativeassignment.home.repository

import com.example.fullcreativeassignment.home.model.BookedEvent
import com.example.fullcreativeassignment.utils.Status
import kotlinx.coroutines.flow.Flow

interface BookedSlotsRepository {
    suspend fun getBookedSlots(): Flow<Status<List<BookedEvent>>>
}
