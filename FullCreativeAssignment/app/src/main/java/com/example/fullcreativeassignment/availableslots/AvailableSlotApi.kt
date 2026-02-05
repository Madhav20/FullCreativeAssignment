package com.example.fullcreativeassignment.availableslots

import com.example.fullcreativeassignment.availableslots.SlotResponse
import retrofit2.Response
import retrofit2.http.GET

interface AvailableSlotApi {
    @GET("slots")
    suspend fun getAvailableSlots(): Response<SlotResponse>
}
