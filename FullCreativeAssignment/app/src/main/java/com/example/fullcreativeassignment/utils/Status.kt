package com.example.fullcreativeassignment.utils

sealed class Status<out DTO : Any> {
    object Loading : Status<Nothing>()

    data class Success<out DTO : Any>(
        val data: DTO,
    ) : Status<DTO>()

    data class Error(
        val error: NetworkError,
        val errorMessage: String = error.toUserMessage(),
    ) : Status<Nothing>()
}
