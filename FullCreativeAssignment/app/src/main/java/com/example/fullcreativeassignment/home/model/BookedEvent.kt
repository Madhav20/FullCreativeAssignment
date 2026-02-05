package com.example.fullcreativeassignment.home.model

data class BookedEvent(
    val slotId: String,
    val name: String,
    val startTime: String,
    val endTime: String,
    val bookedBy: String,
    val phoneNumber: String,
)
