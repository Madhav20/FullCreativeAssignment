package com.example.fullcreativeassignment.bookslot

import app.cash.turbine.test
import com.example.fullcreativeassignment.availableslots.Slot
import com.example.fullcreativeassignment.utils.NetworkError
import com.example.fullcreativeassignment.utils.Status
import com.example.fullcreativeassignment.utils.UIState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {
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
    fun `onNameChanged sets error when name is blank`() =
        runTest {
            val repo = FakeBookingRepository()
            val viewModel = BookingViewModel(repo)

            viewModel.onNameChanged("")

            val state = viewModel.formState.value

            assertEquals("Name cannot be empty", state.nameError)
            assertFalse(state.isValid)
        }

    @Test
    fun `onPhoneChanged sets error when phone is less than 10 digits`() =
        runTest {
            val repo = FakeBookingRepository()
            val viewModel = BookingViewModel(repo)

            viewModel.onPhoneChanged("123")

            val state = viewModel.formState.value

            assertEquals("Phone must be 10 digits", state.phoneError)
            assertFalse(state.isValid)
        }

    @Test
    fun `form becomes valid when name and phone are correct`() =
        runTest {
            val repo = FakeBookingRepository()
            val viewModel = BookingViewModel(repo)

            viewModel.onNameChanged("John")
            viewModel.onPhoneChanged("1234567890")

            val state = viewModel.formState.value

            assertTrue(state.isValid)
        }

    @Test
    fun `bookEvent emits error when form invalid`() =
        runTest {
            val repo = FakeBookingRepository()
            val viewModel = BookingViewModel(repo)

            viewModel.bookEvent("SLOT001")

            val state = viewModel.bookingState.value

            assertTrue(state is UIState.Error)
        }

    @Test
    fun `bookEvent success resets form and emits navigation event`() =
        runTest {
            val repo = FakeBookingRepository()

            repo.emitFlow(
                flow {
                    emit(Status.Loading)
                    emit(Status.Success(Unit))
                },
            )

            val viewModel = BookingViewModel(repo)

            viewModel.onNameChanged("John")
            viewModel.onPhoneChanged("1234567890")

            viewModel.navigationEvent.test {
                viewModel.bookEvent("SLOT001")

                // Navigation should be emitted
                awaitItem()

                cancelAndIgnoreRemainingEvents()
            }

            // Form should reset
            assertEquals("", viewModel.formState.value.name)
            assertEquals("", viewModel.formState.value.phone)

            // bookingState should end as None
            assertEquals(UIState.None, viewModel.bookingState.value)
        }

    @Test
    fun `bookEvent emits error when repository emits error`() =
        runTest {
            val repo = FakeBookingRepository()

            repo.emitFlow(
                flow {
                    emit(Status.Error(NetworkError.UnknownError, "Failed"))
                },
            )

            val viewModel = BookingViewModel(repo)

            viewModel.onNameChanged("John")
            viewModel.onPhoneChanged("1234567890")

            viewModel.bookEvent("SLOT001")

            val state = viewModel.bookingState.value

            assertTrue(state is UIState.Error)
            assertEquals("Failed", (state as UIState.Error).message)
        }

    @Test
    fun `loadSlotById updates selectedSlot`() =
        runTest {
            val repo = FakeBookingRepository()

            repo.slotToReturn =
                Slot(
                    slotId = "SLOT001",
                    name = "Morning",
                    startTime = "09:00",
                    endTime = "10:00",
                )

            val viewModel = BookingViewModel(repo)

            viewModel.loadSlotById("SLOT001")

            assertEquals("SLOT001", viewModel.selectedSlot.value?.slotId)
        }
}
