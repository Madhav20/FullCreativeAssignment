package com.example.fullcreativeassignment.bookslot.repository

import com.example.fullcreativeassignment.availableslots.Slot
import com.example.fullcreativeassignment.bookslot.BookingApi
import com.example.fullcreativeassignment.data.dao.SlotDao
import com.example.fullcreativeassignment.data.entity.BookedEventEntity
import com.example.fullcreativeassignment.data.entity.toEvent
import com.example.fullcreativeassignment.di.IoDispatcher
import com.example.fullcreativeassignment.home.model.CreateSlotRequest
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

class BookingRepositoryImpl
    @Inject
    constructor(
        private val bookingApi: BookingApi,
        private val slotDao: SlotDao,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : BookingRepository {
        override suspend fun bookSlots(
            customerName: String,
            phoneNumber: String,
            slotId: String,
        ): Flow<Status<Unit>> =
            flow {
                emit(Status.Loading)
                try {
                    val response =
                        bookingApi.bookSlot(
                            CreateSlotRequest(
                                customerName = customerName,
                                phoneNumber = phoneNumber,
                                slotId = slotId,
                            ),
                        )

                    if (response.isSuccessful) {
                        val body = response.body()

                        if (body?.status == "success") {
                            emit(Status.Success(Unit))
                        } else {
                            emit(
                                Status.Error(
                                    error = NetworkError.UnknownError,
                                    errorMessage = body?.message ?: "Something went wrong",
                                ),
                            )
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
                } catch (e: ConnectivityException) {
                    val slotEntity = slotDao.getSlotById(slotId)

                    if (slotEntity?.isBooked == true) {
                        emit(Status.Error(NetworkError.UnknownError, "Slot already booked"))
                        return@flow
                    }

                    if (slotEntity != null) {
                        val bookedEntity =
                            BookedEventEntity(
                                slotId = slotEntity.slotId,
                                slotName = slotEntity.name,
                                startTime = slotEntity.startTime,
                                endTime = slotEntity.endTime,
                                customerName = customerName,
                                phoneNumber = phoneNumber,
                            )
                        slotDao.insertBookedEvent(bookedEntity)
                        slotDao.markSlotAsBooked(slotId)
                        emit(Status.Success(Unit))
                    } else {
                        emit(
                            Status.Error(
                                error = NetworkError.UnknownError,
                                errorMessage = "Slot not found locally",
                            ),
                        )
                    }
                } catch (e: UnknownHostException) {
                    emit(Status.Error(NetworkError.NoInternetConnection))
                } catch (e: SocketTimeoutException) {
                    emit(Status.Error(NetworkError.Timeout))
                } catch (e: Exception) {
                    emit(
                        Status.Error(
                            error = NetworkError.UnknownError,
                            errorMessage = e.localizedMessage ?: "Unexpected error",
                        ),
                    )
                }
            }.flowOn(ioDispatcher)

        override suspend fun getSlotById(slotId: String): Slot? = slotDao.getSlotById(slotId)?.toEvent()
    }
