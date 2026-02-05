package com.example.fullcreativeassignment.di

import com.example.fullcreativeassignment.availableslots.AvailableSlotApi
import com.example.fullcreativeassignment.bookslot.BookingApi
import com.example.fullcreativeassignment.home.BookedSlotApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideBookedSlotApi(retrofit: Retrofit): BookedSlotApi = retrofit.create(BookedSlotApi::class.java)

    @Provides
    @Singleton
    fun provideAvailableSlotApi(retrofit: Retrofit): AvailableSlotApi = retrofit.create(AvailableSlotApi::class.java)

    @Provides
    @Singleton
    fun provideBookingApiApi(retrofit: Retrofit): BookingApi = retrofit.create(BookingApi::class.java)
}
