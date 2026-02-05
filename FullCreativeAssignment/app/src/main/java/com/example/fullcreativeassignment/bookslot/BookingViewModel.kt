package com.example.fullcreativeassignment.bookslot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcreativeassignment.availableslots.Slot
import com.example.fullcreativeassignment.bookslot.CustomerFormState
import com.example.fullcreativeassignment.bookslot.repository.BookingRepository
import com.example.fullcreativeassignment.home.repository.BookedSlotsRepository
import com.example.fullcreativeassignment.utils.NetworkError
import com.example.fullcreativeassignment.utils.Status
import com.example.fullcreativeassignment.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel
    @Inject
    constructor(
        private val repository: BookingRepository,
    ) : ViewModel() {
        private val _navigationEvent = MutableSharedFlow<Unit>()
        val navigationEvent = _navigationEvent.asSharedFlow()

        private val _formState = MutableStateFlow(CustomerFormState())
        val formState: StateFlow<CustomerFormState> = _formState.asStateFlow()

        private val _bookingState = MutableStateFlow<UIState<Unit>>(UIState.None)
        val bookingState: StateFlow<UIState<Unit>> = _bookingState.asStateFlow()

        private val _selectedSlot = MutableStateFlow<Slot?>(null)
        val selectedSlot: StateFlow<Slot?> = _selectedSlot.asStateFlow()

        fun bookEvent(slotId: String?) {
            viewModelScope.launch {
                val form = _formState.value

                if (!form.isValid || slotId == null) {
                    _bookingState.value =
                        UIState.Error(
                            message = "Invalid form data",
                            error = NetworkError.UnknownError,
                        )
                    return@launch
                }

                repository
                    .bookSlots(
                        form.name,
                        form.phone,
                        slotId,
                    ).collect { result ->

                        when (result) {
                            is Status.Loading -> {
                                _bookingState.value = UIState.Loading
                            }

                            is Status.Success -> {
                                _bookingState.value = UIState.Success(Unit)
                                _formState.value = CustomerFormState()
                                _bookingState.value = UIState.None
                                _navigationEvent.emit(Unit)
                            }

                            is Status.Error -> {
                                _bookingState.value =
                                    UIState.Error(
                                        message = result.errorMessage,
                                        error = result.error,
                                    )
                            }
                        }
                    }
            }
        }

        fun onNameChanged(value: String) {
            val error = if (value.isBlank()) "Name cannot be empty" else null

            _formState.value =
                _formState.value.copy(
                    name = value,
                    nameError = error,
                )

            validateForm()
        }

        fun onPhoneChanged(value: String) {
            val error =
                if (value.length < 10) {
                    "Phone must be 10 digits"
                } else {
                    null
                }

            _formState.value =
                _formState.value.copy(
                    phone = value,
                    phoneError = error,
                )

            validateForm()
        }

        private fun validateForm() {
            val state = _formState.value

            _formState.value =
                state.copy(
                    isValid =
                        state.name.isNotBlank() &&
                            state.phone.length == 10 &&
                            state.nameError == null &&
                            state.phoneError == null,
                )
        }

        fun loadSlotById(slotId: String) {
            viewModelScope.launch {
                _selectedSlot.value = repository.getSlotById(slotId)
            }
        }
    }
