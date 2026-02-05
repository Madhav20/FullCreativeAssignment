package com.example.fullcreativeassignment.bookslot

import app.cash.turbine.test
import com.example.fullcreativeassignment.bookslot.repository.BookingRepositoryImpl
import com.example.fullcreativeassignment.data.dao.SlotDao
import com.example.fullcreativeassignment.data.entity.BookedEventEntity
import com.example.fullcreativeassignment.data.entity.SlotsEntity
import com.example.fullcreativeassignment.home.model.BookedEvent
import com.example.fullcreativeassignment.home.repository.BookedSlotsRepositoryImpl
import com.example.fullcreativeassignment.utils.ConnectivityException
import com.example.fullcreativeassignment.utils.NetworkError
import com.example.fullcreativeassignment.utils.Status
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class BookingRepositoryImplTest {
    private val api: BookingApi = mock()
    private val dao: SlotDao = mock()
    private val dispatcher = StandardTestDispatcher()

    private lateinit var repository: BookingRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repository =
            BookingRepositoryImpl(
                bookingApi = api,
                slotDao = dao,
                ioDispatcher = dispatcher,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `bookSlots emits Success when api returns success`() =
        runTest {
            val responseBody =
                CreateSlotResponse(
                    status = "success",
                    message = "Booked",
                )

            whenever(api.bookSlot(any()))
                .thenReturn(Response.success(responseBody))

            repository.bookSlots("John", "1234567890", "SLOT001").test {
                assertEquals(Status.Loading, awaitItem())

                val success = awaitItem()
                assertTrue(success is Status.Success)

                awaitComplete()
            }
        }

    @Test
    fun `bookSlots emits Error when api returns failure status`() =
        runTest {
            val responseBody =
                CreateSlotResponse(
                    status = "failed",
                    message = "Already booked",
                )

            whenever(api.bookSlot(any()))
                .thenReturn(Response.success(responseBody))

            repository.bookSlots("John", "123", "SLOT001").test {
                assertEquals(Status.Loading, awaitItem())

                val error = awaitItem()
                assertTrue(error is Status.Error)

                awaitComplete()
            }
        }

    @Test
    fun `bookSlots emits HttpError when api response is not successful`() =
        runTest {
            whenever(api.bookSlot(any()))
                .thenReturn(Response.error(400, "".toResponseBody()))

            repository.bookSlots("John", "123", "SLOT001").test {
                assertEquals(Status.Loading, awaitItem())

                val error = awaitItem()
                assertTrue(error is Status.Error)
                assertTrue((error as Status.Error).error is NetworkError.HttpError)

                awaitComplete()
            }
        }

    @Test
    fun `bookSlots books locally when ConnectivityException occurs`() =
        runTest {
            whenever(api.bookSlot(any())).thenAnswer {
                throw ConnectivityException("No internet")
            }

            whenever(dao.getSlotById("SLOT001")).thenReturn(
                SlotsEntity(
                    slotId = "SLOT001",
                    name = "Morning",
                    startTime = "09:00",
                    endTime = "10:00",
                    isBooked = false,
                ),
            )

            repository.bookSlots("John", "123", "SLOT001").test {
                assertEquals(Status.Loading, awaitItem())

                val success = awaitItem()
                assertTrue(success is Status.Success)

                awaitComplete()
            }

            verify(dao).insertBookedEvent(any())
            verify(dao).markSlotAsBooked("SLOT001")
        }

    @Test
    fun `bookSlots emits error when slot already booked locally`() =
        runTest {
            whenever(api.bookSlot(any())).thenAnswer {
                throw ConnectivityException("No internet")
            }

            whenever(dao.getSlotById("SLOT001")).thenReturn(
                SlotsEntity(
                    slotId = "SLOT001",
                    name = "Morning",
                    startTime = "09:00",
                    endTime = "10:00",
                    isBooked = true,
                ),
            )

            repository.bookSlots("John", "123", "SLOT001").test {
                assertEquals(Status.Loading, awaitItem())

                val error = awaitItem()
                assertTrue(error is Status.Error)

                awaitComplete()
            }

            verify(dao, never()).insertBookedEvent(any())
        }

    @Test
    fun `bookSlots emits error when slot not found locally`() =
        runTest {
            whenever(api.bookSlot(any())).thenAnswer {
                throw ConnectivityException("No internet")
            }

            whenever(dao.getSlotById("SLOT001")).thenReturn(null)

            repository.bookSlots("John", "123", "SLOT001").test {
                assertEquals(Status.Loading, awaitItem())

                val error = awaitItem()
                assertTrue(error is Status.Error)

                awaitComplete()
            }
        }

    @Test
    fun `bookSlots emits NoInternet when UnknownHostException occurs`() =
        runTest {
            whenever(api.bookSlot(any())).thenAnswer {
                throw UnknownHostException()
            }

            repository.bookSlots("John", "123", "SLOT001").test {
                assertEquals(Status.Loading, awaitItem())

                val error = awaitItem()
                assertTrue(error is Status.Error)
                assertEquals(
                    NetworkError.NoInternetConnection,
                    (error as Status.Error).error,
                )

                awaitComplete()
            }
        }

    @Test
    fun `bookSlots emits Timeout when SocketTimeoutException occurs`() =
        runTest {
            whenever(api.bookSlot(any())).thenAnswer {
                throw SocketTimeoutException()
            }

            repository.bookSlots("John", "123", "SLOT001").test {
                assertEquals(Status.Loading, awaitItem())

                val error = awaitItem()
                assertTrue(error is Status.Error)
                assertEquals(
                    NetworkError.Timeout,
                    (error as Status.Error).error,
                )

                awaitComplete()
            }
        }
}
