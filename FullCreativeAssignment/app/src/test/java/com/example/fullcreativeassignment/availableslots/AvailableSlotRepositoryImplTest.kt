package com.example.fullcreativeassignment.availableslots

import app.cash.turbine.test
import com.example.fullcreativeassignment.availableslots.repository.AvailableSlotRepositoryImpl
import com.example.fullcreativeassignment.data.dao.SlotDao
import com.example.fullcreativeassignment.data.entity.SlotsEntity
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
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class AvailableSlotRepositoryImplTest {
    private val api: AvailableSlotApi = mock()
    private val dao: SlotDao = mock()
    private val dispatcher = StandardTestDispatcher()

    private lateinit var repository: AvailableSlotRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repository =
            AvailableSlotRepositoryImpl(
                availableSlotApi = api,
                slotDao = dao,
                ioDispatcher = dispatcher,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAvailableSlots emits Success when api returns success`() =
        runTest {
            whenever(dao.getCount()).thenReturn(1)

            val responseBody =
                SlotResponse(
                    status = "success",
                    message = "ok",
                    data =
                        listOf(
                            Slot("SLOT001", "Morning", "09:00", "10:00"),
                        ),
                )

            whenever(api.getAvailableSlots()).thenReturn(Response.success(responseBody))

            repository.getAvailableSlots().test {
                assertEquals(Status.Loading, awaitItem())

                val success = awaitItem()
                assertTrue(success is Status.Success)
                assertEquals(1, (success as Status.Success).data.size)

                awaitComplete()
            }
        }

    @Test
    fun `getAvailableSlots emits Error when api returns empty list`() =
        runTest {
            whenever(dao.getCount()).thenReturn(1)

            val responseBody =
                SlotResponse(
                    status = "success",
                    message = "ok",
                    data = emptyList(),
                )

            whenever(api.getAvailableSlots()).thenReturn(Response.success(responseBody))

            repository.getAvailableSlots().test {
                assertEquals(Status.Loading, awaitItem())

                val error = awaitItem()
                assertTrue(error is Status.Error)

                awaitComplete()
            }
        }

    @Test
    fun `getAvailableSlots emits cached data when ConnectivityException occurs`() =
        runTest {
            whenever(dao.getCount()).thenReturn(1)

            whenever(api.getAvailableSlots()).thenAnswer {
                throw ConnectivityException("No Internet")
            }

            whenever(dao.getAllAvailableSlots()).thenReturn(
                flowOf(
                    listOf(
                        SlotsEntity("SLOT001", "Morning", "09:00", "10:00"),
                    ),
                ),
            )

            repository.getAvailableSlots().test {
                assertEquals(Status.Loading, awaitItem())

                val success = awaitItem()
                assertTrue(success is Status.Success)
                assertEquals(1, (success as Status.Success).data.size)

                awaitComplete()
            }
        }

    @Test
    fun `getAvailableSlots emits NoInternet when UnknownHostException occurs`() =
        runTest {
            whenever(dao.getCount()).thenReturn(1)

            whenever(api.getAvailableSlots()).thenAnswer {
                throw UnknownHostException()
            }

            repository.getAvailableSlots().test {
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
    fun `getAvailableSlots calls seedSlots when db is empty`() =
        runTest {
            whenever(dao.getCount()).thenReturn(0)

            whenever(api.getAvailableSlots()).thenAnswer {
                throw ConnectivityException("No Internet")
            }
            whenever(dao.getAllAvailableSlots()).thenReturn(flowOf(emptyList()))

            repository.getAvailableSlots().test {
                awaitItem() // Loading
                awaitItem() // fallback

                awaitComplete()
            }

            verify(dao).insertSlots(any())
        }
}
