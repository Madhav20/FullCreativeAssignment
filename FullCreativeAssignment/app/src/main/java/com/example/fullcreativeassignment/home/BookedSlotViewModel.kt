package com.example.fullcreativeassignment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcreativeassignment.home.model.BookedEvent
import com.example.fullcreativeassignment.home.repository.BookedSlotsRepository
import com.example.fullcreativeassignment.utils.Status
import com.example.fullcreativeassignment.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookedSlotViewModel
    @Inject
    constructor(
        private val repository: BookedSlotsRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<UIState<List<BookedEvent>>>(UIState.None)
        val uiState: StateFlow<UIState<List<BookedEvent>>> = _uiState.asStateFlow()

        init {
            loadBookedSlots()
        }

        private fun loadBookedSlots() {
            viewModelScope.launch {
                repository.getBookedSlots().collect { result ->
                    when (result) {
                        is Status.Loading -> {
                            _uiState.value = UIState.Loading
                        }

                        is Status.Success -> {
                            _uiState.value = UIState.Success(result.data)
                        }

                        is Status.Error -> {
                            _uiState.value =
                                UIState.Error(
                                    message = result.errorMessage,
                                    error = result.error,
                                )
                        }
                    }
                }
            }
        }
    }
