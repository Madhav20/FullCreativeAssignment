package com.example.fullcreativeassignment.bookslot

import com.example.fullcreativeassignment.home.model.CreateSlotRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BookingApi {
    @POST("book_slot")
    suspend fun bookSlot(
        @Body request: CreateSlotRequest,
    ): Response<CreateSlotResponse>
}
