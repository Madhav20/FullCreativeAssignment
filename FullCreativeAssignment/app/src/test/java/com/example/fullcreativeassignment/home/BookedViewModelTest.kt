package com.example.fullcreativeassignment.home

import com.example.fullcreativeassignment.home.model.BookedEvent
import com.example.fullcreativeassignment.utils.NetworkError
import com.example.fullcreativeassignment.utils.Status
import com.example.fullcreativeassignment.utils.UIState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookedSlotViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits Success when repository emits loading then success`() =
        runTest {
            val repo = FakeBookedSlotsRepository()

            val events =
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

            repo.emitFlow(
                flow {
                    emit(Status.Loading)
                    emit(Status.Success(events))
                },
            )

            val viewModel = BookedSlotViewModel(repo)

            val state = viewModel.uiState.value

            assertTrue(state is UIState.Success)
            assertEquals(1, (state as UIState.Success).data.size)
        }

    @Test
    fun `uiState emits Error when repository emits error`() =
        runTest {
            val repo = FakeBookedSlotsRepository()

            repo.emitFlow(
                flow {
                    emit(Status.Loading)
                    emit(
                        Status.Error(
                            error = NetworkError.UnknownError,
                            errorMessage = "Failed",
                        ),
                    )
                },
            )

            val viewModel = BookedSlotViewModel(repo)

            val state = viewModel.uiState.value

            assertTrue(state is UIState.Error)
            assertEquals("Failed", (state as UIState.Error).message)
        }

    @Test
    fun `uiState emits Success when repository emits success directly`() =
        runTest {
            val repo = FakeBookedSlotsRepository()

            val events = emptyList<BookedEvent>()

            repo.emitFlow(
                flowOf(Status.Success(events)),
            )

            val viewModel = BookedSlotViewModel(repo)

            val state = viewModel.uiState.value

            assertTrue(state is UIState.Success)
        }

    @Test
    fun `repository is called on init`() =
        runTest {
            val repo = FakeBookedSlotsRepository()

            var called = false

            repo.emitFlow(
                flow {
                    called = true
                    emit(Status.Success(emptyList()))
                },
            )

            BookedSlotViewModel(repo)

            assertTrue(called)
        }
}
