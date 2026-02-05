package com.example.fullcreativeassignment.home.repository

import com.example.fullcreativeassignment.data.dao.SlotDao
import com.example.fullcreativeassignment.data.entity.toEvent
import com.example.fullcreativeassignment.di.IoDispatcher
import com.example.fullcreativeassignment.home.BookedSlotApi
import com.example.fullcreativeassignment.home.model.BookedEvent
import com.example.fullcreativeassignment.utils.ConnectivityException
import com.example.fullcreativeassignment.utils.NetworkError
import com.example.fullcreativeassignment.utils.Status
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class BookedSlotsRepositoryImpl
    @Inject
    constructor(
        private val bookedSlotApi: BookedSlotApi,
        private val slotDao: SlotDao,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : BookedSlotsRepository {
        override suspend fun getBookedSlots(): Flow<Status<List<BookedEvent>>> =
            flow {
                emit(Status.Loading)

                try {
                    val response = bookedSlotApi.getBookedSlots()
                    if (response.isSuccessful) {
                        val events = response.body()
                        if (events != null) {
                            if (events.isEmpty()) {
                                emit(
                                    Status.Error(
                                        error = NetworkError.UnknownError,
                                        errorMessage = "No events found",
                                    ),
                                )
                            } else {
                                emit(Status.Success(events))
                            }
                        } else {
                            emit(
                                Status.Error(
                                    error =
                                        NetworkError.HttpError(
                                            code = response.code(),
                                            message = response.message(),
                                        ),
                                ),
                            )
                        }
                    }
                } catch (e: ConnectivityException) {
                    var cachedEvents = slotDao.getBookedEvents()
                    cachedEvents.collect { entities ->
                        emit(Status.Success(entities.map { it.toEvent() }))
                        return@collect
                    }
                } catch (e: UnknownHostException) {
                    emit(Status.Error(NetworkError.NoInternetConnection))
                } catch (e: SocketTimeoutException) {
                    emit(Status.Error(NetworkError.Timeout))
                } catch (e: Exception) {
                    emit(
                        Status.Error(
                            error = NetworkError.UnknownError,
                            errorMessage = e.localizedMessage ?: "An unexpected error has occurred.",
                        ),
                    )
                }
            }.flowOn(ioDispatcher)
    }
