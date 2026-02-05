package com.example.fullcreativeassignment.availableslots

data class SlotResponse(
    val status: String,
    val message: String,
    val data: List<Slot>,
)