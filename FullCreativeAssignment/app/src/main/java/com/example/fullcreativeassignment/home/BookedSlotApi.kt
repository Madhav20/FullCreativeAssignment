package com.example.fullcreativeassignment.home

import com.example.fullcreativeassignment.home.model.BookedEvent
import retrofit2.Response
import retrofit2.http.GET

interface BookedSlotApi {
    @GET("booked_slots")
    suspend fun getBookedSlots(): Response<List<BookedEvent>>
}
