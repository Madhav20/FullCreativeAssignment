package com.example.fullcreativeassignment.utils

import androidx.compose.runtime.Immutable

@Immutable
sealed class UIState<out T : Any> {
    object Loading : UIState<Nothing>()

    data class Success<out T : Any>(
        val data: T,
    ) : UIState<T>()

    data class Error(
        val message: String,
        val error: NetworkError? = null,
    ) : UIState<Nothing>()

    object None : UIState<Nothing>()
}
