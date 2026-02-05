package com.example.fullcreativeassignment.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class ConnectivityCheckInterceptor
    @Inject
    constructor(
        private val context: Context,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response =
            when {
                context.isInternetAvailable() -> {
                    chain.proceed(chain.request())
                }

                else -> {
                    throw ConnectivityException(message = "No Internet Available")
                }
            }
    }

fun Context.isInternetAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

class ConnectivityException(
    override val message: String,
) : IOException(message)
