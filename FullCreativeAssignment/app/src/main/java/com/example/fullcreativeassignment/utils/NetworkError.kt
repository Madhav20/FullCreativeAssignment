package com.example.fullcreativeassignment.utils

import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class NetworkError {
    data class HttpError(
        val code: Int,
        val message: String,
    ) : NetworkError()

    data class NetworkException(
        val exception: Throwable,
    ) : NetworkError()

    object NoInternetConnection : NetworkError()

    object UnknownError : NetworkError()

    object Timeout : NetworkError()

    fun toUserMessage(): String =
        when (this) {
            is HttpError -> {
                when (code) {
                    400 -> "Bad request. Please try again."
                    401 -> "Unauthorized. Please login again"
                    403 -> "Access forbidden"
                    404 -> "Resource not found"
                    500 -> "Server error. Please try again later"
                    else -> "Unknown error"
                }
            }

            is NetworkException -> {
                when (exception) {
                    is UnknownHostException -> "No internet connection, Please check your network"
                    is SocketTimeoutException -> "Request timed out, Please try again"
                    else -> "Network error: ${exception.localizedMessage ?: "Unknown error"}"
                }
            }

            is NoInternetConnection -> {
                "No internet connection available"
            }

            is UnknownError -> {
                "An unexpected error has occured"
            }

            is Timeout -> {
                "Request timed out. Please try again."
            }
        }
}
