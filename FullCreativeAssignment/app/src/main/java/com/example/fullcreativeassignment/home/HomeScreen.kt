package com.example.fullcreativeassignment.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fullcreativeassignment.home.model.BookedEvent
import com.example.fullcreativeassignment.utils.UIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: UIState<List<BookedEvent>>,
    onFabClick: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onFabClick,
                icon = {
                    Icon(Icons.Default.Add, contentDescription = null)
                },
                text = { Text("Book Event") },
                containerColor = MaterialTheme.colorScheme.primary,
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Events",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
            )
        },
    ) { padding ->

        when (uiState) {
            is UIState.Error -> {
                Text("Something went wrong")
            }

            UIState.Loading -> {
                Text("Loading...")
            }

            UIState.None -> {
                // optional
            }

            is UIState.Success -> {
                val eventList = uiState.data
                if (eventList.isEmpty()) {
                    EmptyState(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding),
                    )
                } else {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(eventList) { event ->
                            EventCard(event)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "No events yet",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Tap the button below to book your first event",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )
        }
    }
}
