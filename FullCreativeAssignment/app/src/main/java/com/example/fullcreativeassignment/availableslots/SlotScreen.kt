package com.example.fullcreativeassignment.availableslots

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fullcreativeassignment.availableslots.Slot
import com.example.fullcreativeassignment.utils.UIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotSelectionScreen(
    uiState: UIState<List<Slot>>,
    onSlotSelected: (Slot) -> Unit,
) {
    when (uiState) {
        is UIState.Error -> {
            TODO()
        }

        UIState.Loading -> {
            TODO()
        }

        UIState.None -> {
            TODO()
        }

        is UIState.Success -> {
            val slots = uiState.data

            if (slots.isEmpty()) {
                Text("No event booked yet")
            }
            var selectedSlot by remember { mutableStateOf<Slot?>(null) }

            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Select Time Slot") })
                },
                bottomBar = {
                    Button(
                        onClick = {
                            selectedSlot?.let { onSlotSelected(it) }
                        },
                        enabled = selectedSlot != null,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                    ) {
                        Text("Continue")
                    }
                },
            ) { padding ->

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(slots) { slot ->
                        SlotCard(
                            slot = slot,
                            isSelected = selectedSlot == slot,
                            onClick = { selectedSlot = slot },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotCard(
    slot: Slot,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        },
    )

    val elevation by animateDpAsState(
        if (isSelected) 10.dp else 4.dp,
    )

    val containerColor by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
    )

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(elevation),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                slot.name,
                style = MaterialTheme.typography.titleMedium,
                color =
                    if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "${slot.startTime} - ${slot.endTime}",
                style = MaterialTheme.typography.bodyMedium,
                color =
                    if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
            )
        }
    }
}
