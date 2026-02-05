package com.example.fullcreativeassignment.di

import com.example.fullcreativeassignment.availableslots.AvailableSlotApi
import com.example.fullcreativeassignment.availableslots.repository.AvailableSlotRepository
import com.example.fullcreativeassignment.availableslots.repository.AvailableSlotRepositoryImpl
import com.example.fullcreativeassignment.bookslot.BookingApi
import com.example.fullcreativeassignment.bookslot.repository.BookingRepository
import com.example.fullcreativeassignment.bookslot.repository.BookingRepositoryImpl
import com.example.fullcreativeassignment.data.dao.SlotDao
import com.example.fullcreativeassignment.home.BookedSlotApi
import com.example.fullcreativeassignment.home.repository.BookedSlotsRepository
import com.example.fullcreativeassignment.home.repository.BookedSlotsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideBookedSlotRepository(
        bookedSlotApi: BookedSlotApi,
        slotDao: SlotDao,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): BookedSlotsRepository = BookedSlotsRepositoryImpl(bookedSlotApi, slotDao, ioDispatcher)

    @Provides
    @ViewModelScoped
    fun provideAvailableSlotRepository(
        availableSlotApi: AvailableSlotApi,
        slotDao: SlotDao,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): AvailableSlotRepository = AvailableSlotRepositoryImpl(availableSlotApi, slotDao, ioDispatcher)

    @Provides
    @ViewModelScoped
    fun provideBookingRepository(
        bookingApi: BookingApi,
        slotDao: SlotDao,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): BookingRepository = BookingRepositoryImpl(bookingApi, slotDao, ioDispatcher)
}
