package com.example.fullcreativeassignment.availableslots.repository

import com.example.fullcreativeassignment.availableslots.AvailableSlotApi
import com.example.fullcreativeassignment.availableslots.Slot
import com.example.fullcreativeassignment.data.dao.SlotDao
import com.example.fullcreativeassignment.data.entity.SlotsEntity
import com.example.fullcreativeassignment.data.entity.toEvent
import com.example.fullcreativeassignment.di.IoDispatcher
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

class AvailableSlotRepositoryImpl
    @Inject
    constructor(
        private val availableSlotApi: AvailableSlotApi,
        private val slotDao: SlotDao,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : AvailableSlotRepository {
        override suspend fun getAvailableSlots(): Flow<Status<List<Slot>>> =
            flow {
                emit(Status.Loading)
                if (slotDao.getCount() == 0) {
                    seedSlots()
                }
                try {
                    val response = availableSlotApi.getAvailableSlots()
                    if (response.isSuccessful) {
                        val events = response.body()
                        if (events != null && events.status == "success") {
                            if (events.data.isEmpty()) {
                                emit(
                                    Status.Error(
                                        error = NetworkError.UnknownError,
                                        errorMessage = "No events found",
                                    ),
                                )
                            } else {
                                emit(Status.Success(events.data))
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
                    var cachedEvents = slotDao.getAllAvailableSlots()
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

        suspend fun seedSlots() {
            slotDao.insertSlots(
                listOf(
                    SlotsEntity("SLOT001", "Early Morning", "07:00", "08:00"),
                    SlotsEntity("SLOT002", "Morning", "08:00", "09:00"),
                    SlotsEntity("SLOT003", "Late Morning", "09:00", "10:00"),
                    SlotsEntity("SLOT004", "Mid Morning", "10:00", "11:00"),
                    SlotsEntity("SLOT005", "Pre-Lunch", "11:00", "12:00"),
                    SlotsEntity("SLOT006", "Lunch", "12:00", "13:00"),
                    SlotsEntity("SLOT007", "Early Afternoon", "13:00", "14:00"),
                    SlotsEntity("SLOT008", "Afternoon", "14:00", "15:00"),
                    SlotsEntity("SLOT009", "Late Afternoon", "15:00", "16:00"),
                    SlotsEntity("SLOT010", "Evening", "16:00", "17:00"),
                    SlotsEntity("SLOT011", "Late Evening", "17:00", "18:00"),
                    SlotsEntity("SLOT012", "Prime Time", "18:00", "19:00"),
                    SlotsEntity("SLOT013", "Dinner", "19:00", "20:00"),
                    SlotsEntity("SLOT014", "Night", "20:00", "21:00"),
                ),
            )
        }
    }
