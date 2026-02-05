package com.example.fullcreativeassignment.bookslot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fullcreativeassignment.availableslots.Slot
import com.example.fullcreativeassignment.utils.UIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailsScreen(
    selectedSlot: Slot?,
    formState: CustomerFormState,
    bookingState: UIState<Unit>,
    onNameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onBookClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bookingState) {
        when (bookingState) {
            is UIState.Success -> {
                snackbarHostState.showSnackbar("Booking successful")
            }

            is UIState.Error -> {
                snackbarHostState.showSnackbar(
                    bookingState.message ?: "Booking failed",
                )
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Customer Details") })
        },
    ) { padding ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Selected Slot Card
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = selectedSlot?.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "${selectedSlot?.startTime} - ${selectedSlot?.endTime}",
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            OutlinedTextField(
                value = formState.name,
                onValueChange = onNameChanged,
                label = { Text("Customer Name") },
                isError = formState.nameError != null,
            )

            formState.nameError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = formState.phone,
                onValueChange = onPhoneChanged,
                label = { Text("Phone Number") },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                isError = formState.phoneError != null,
            )
            formState.phoneError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = onBookClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = formState.isValid && bookingState !is UIState.Loading,
            ) {
                if (bookingState is UIState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Book Event")
                }
            }
        }
    }
}
