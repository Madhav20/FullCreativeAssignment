package com.example.fullcreativeassignment.home

import app.cash.turbine.test
import com.example.fullcreativeassignment.data.dao.SlotDao
import com.example.fullcreativeassignment.data.entity.BookedEventEntity
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
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class BookedSlotsRepositoryImplTest {
    private val api: BookedSlotApi = mock()
    private val dao: SlotDao = mock()
    private val dispatcher = StandardTestDispatcher()

    private lateinit var repository: BookedSlotsRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repository =
            BookedSlotsRepositoryImpl(
                bookedSlotApi = api,
                slotDao = dao,
                ioDispatcher = dispatcher,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getBookedSlots emits Success when api returns non-empty list`() =
        runTest {
            val bookedEvents =
                listOf(
                    BookedEvent(
                        slotId = "SLOT001",
                        name = "Morning",
                        startTime = "09:00",
                        endTime = "10:00",
                        bookedBy = "John",
                        phoneNumber = "1234567890",
                    ),
                )

            whenever(api.getBookedSlots())
                .thenReturn(Response.success(bookedEvents))

            repository.getBookedSlots().test {
                assertEquals(Status.Loading, awaitItem())

                val success = awaitItem()
                assertTrue(success is Status.Success)
                assertEquals(1, (success as Status.Success).data.size)

                awaitComplete()
            }
        }

    @Test
    fun `getBookedSlots emits Error when api returns empty list`() =
        runTest {
            whenever(api.getBookedSlots())
                .thenReturn(Response.success(emptyList()))

            repository.getBookedSlots().test {
                assertEquals(Status.Loading, awaitItem())

                val error = awaitItem()
                assertTrue(error is Status.Error)

                awaitComplete()
            }
        }

    @Test
    fun `getBookedSlots emits cached data when ConnectivityException occurs`() =
        runTest {
            whenever(api.getBookedSlots()).thenAnswer {
                throw ConnectivityException("No Internet")
            }

            whenever(dao.getBookedEvents()).thenReturn(
                flowOf(
                    listOf(
                        BookedEventEntity(
                            id = 1,
                            slotId = "SLOT001",
                            slotName = "Morning",
                            startTime = "09:00",
                            endTime = "10:00",
                            customerName = "John",
                            phoneNumber = "1234567890",
                        ),
                    ),
                ),
            )

            repository.getBookedSlots().test {
                assertEquals(Status.Loading, awaitItem())

                val success = awaitItem()
                assertTrue(success is Status.Success)
                assertEquals(1, (success as Status.Success).data.size)

                awaitComplete()
            }
        }

    @Test
    fun `getBookedSlots emits NoInternet when UnknownHostException occurs`() =
        runTest {
            whenever(api.getBookedSlots()).thenAnswer {
                throw UnknownHostException()
            }

            repository.getBookedSlots().test {
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
    fun `getBookedSlots emits Timeout when SocketTimeoutException occurs`() =
        runTest {
            whenever(api.getBookedSlots()).thenAnswer {
                throw SocketTimeoutException()
            }

            repository.getBookedSlots().test {
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
