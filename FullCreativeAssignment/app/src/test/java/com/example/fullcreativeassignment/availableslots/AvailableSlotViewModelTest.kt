package com.example.fullcreativeassignment.availableslots

import app.cash.turbine.test
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
class AvailableSlotViewModelTest {
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
            val fakeRepository = FakeAvailableSlotRepository()

            val slots =
                listOf(
                    Slot("SLOT001", "Morning", "09:00", "10:00"),
                )

            fakeRepository.emitFlow(
                flow {
                    emit(Status.Loading)
                    emit(Status.Success(slots))
                },
            )

            val viewModel = AvailableSlotViewModel(fakeRepository)

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state is UIState.Success)
                assertEquals(1, (state as UIState.Success).data.size)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `uiState emits Error when repository emits error`() =
        runTest {
            val fakeRepository = FakeAvailableSlotRepository()

            fakeRepository.emitFlow(
                flow {
                    emit(Status.Loading)
                    emit(
                        Status.Error(
                            error = NetworkError.UnknownError,
                            errorMessage = "Something went wrong",
                        ),
                    )
                },
            )

            val viewModel = AvailableSlotViewModel(fakeRepository)

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state is UIState.Error)
                assertEquals("Something went wrong", (state as UIState.Error).message)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `uiState emits Success when repository emits success directly`() =
        runTest {
            val fakeRepository = FakeAvailableSlotRepository()

            val slots =
                listOf(
                    Slot("SLOT001", "Morning", "09:00", "10:00"),
                )

            fakeRepository.emitFlow(
                flowOf(Status.Success(slots)),
            )

            val viewModel = AvailableSlotViewModel(fakeRepository)

            viewModel.uiState.test {
                val success = awaitItem()
                assertTrue(success is UIState.Success)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
