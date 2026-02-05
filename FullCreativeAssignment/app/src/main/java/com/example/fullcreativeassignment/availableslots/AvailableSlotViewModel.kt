package com.example.fullcreativeassignment.availableslots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcreativeassignment.availableslots.Slot
import com.example.fullcreativeassignment.availableslots.repository.AvailableSlotRepository
import com.example.fullcreativeassignment.utils.Status
import com.example.fullcreativeassignment.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvailableSlotViewModel
    @Inject
    constructor(
        private val repository: AvailableSlotRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<UIState<List<Slot>>>(UIState.None)
        val uiState: StateFlow<UIState<List<Slot>>> = _uiState.asStateFlow()

        init {
            getAllAvailableSlots()
        }

        private fun getAllAvailableSlots() {
            viewModelScope.launch {
                repository.getAvailableSlots().collect {
                    when (it) {
                        is Status.Error -> {
                            _uiState.value =
                                UIState.Error(
                                    message = it.errorMessage,
                                    error = it.error,
                                )
                        }

                        Status.Loading -> {
                            _uiState.value = UIState.Loading
                        }

                        is Status.Success -> {
                            _uiState.value = UIState.Success(it.data)
                        }
                    }
                }
            }
        }
    }
