package com.example.fullcreativeassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fullcreativeassignment.availableslots.AvailableSlotViewModel
import com.example.fullcreativeassignment.availableslots.SlotSelectionScreen
import com.example.fullcreativeassignment.bookslot.BookingViewModel
import com.example.fullcreativeassignment.bookslot.CustomerDetailsScreen
import com.example.fullcreativeassignment.home.BookedSlotViewModel
import com.example.fullcreativeassignment.home.HomeScreen
import com.example.fullcreativeassignment.ui.theme.FullCreativeAssignmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FullCreativeAssignmentTheme {
                val navController = rememberNavController()
                val bookedSlotViewModel: BookedSlotViewModel = hiltViewModel()
                val availableSlotViewModel: AvailableSlotViewModel = hiltViewModel()
                val bookingViewModel: BookingViewModel = hiltViewModel()
                NavHost(
                    navController = navController,
                    startDestination = "home",
                ) {
                    composable("home") {
                        HomeScreen(
                            uiState = bookedSlotViewModel.uiState.collectAsState().value,
                            onFabClick = {
                                navController.navigate("slots")
                            },
                        )
                    }

                    composable("slots") {
                        SlotSelectionScreen(
                            uiState = availableSlotViewModel.uiState.collectAsState().value,
                            onSlotSelected = { slot ->
                                navController.navigate("customer/${slot.slotId}")
                            },
                        )
                    }

                    composable(
                        route = "customer/{slotId}",
                        arguments =
                            listOf(
                                navArgument("slotId") { type = NavType.StringType },
                            ),
                    ) { backStackEntry ->
                        val slotId = backStackEntry.arguments?.getString("slotId") ?: ""
                        val formState by bookingViewModel.formState.collectAsState()
                        val bookingState by bookingViewModel.bookingState.collectAsState()
                        val selectedSlot by bookingViewModel.selectedSlot.collectAsState()

                        LaunchedEffect(slotId) {
                            bookingViewModel.loadSlotById(slotId)
                        }

                        LaunchedEffect(Unit) {
                            bookingViewModel.navigationEvent.collect {
                                navController.popBackStack("home", false)
                            }
                        }

                        CustomerDetailsScreen(
                            selectedSlot = selectedSlot,
                            formState = formState,
                            bookingState = bookingState,
                            onNameChanged = bookingViewModel::onNameChanged,
                            onPhoneChanged = bookingViewModel::onPhoneChanged,
                            onBookClick = {
                                bookingViewModel.bookEvent(slotId)
                            },
                        )
                    }
                }
            }
        }
    }
}
