package com.example.fullcreativeassignment.bookslot

data class CustomerFormState(
    val name: String = "",
    val phone: String = "",
    val nameError: String? = null,
    val phoneError: String? = null,
    val isValid: Boolean = false,
)
