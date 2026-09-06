package com.beperfectsalon.app.data.repository

import com.beperfectsalon.app.data.firebase.FirestorePaths
import com.beperfectsalon.app.data.model.Booking
import com.beperfectsalon.app.data.model.BookingStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BookingRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private fun bookings() = firestore.collection(FirestorePaths.BOOKINGS)

    /**
     * Time slots on [date] that a specific stylist is already booked for (excludes cancelled
     * bookings). Used to grey out unavailable slots when the customer picks that stylist.
     * When no stylist is chosen ("Any stylist") the salon assigns staff manually, so every
     * slot stays selectable.
     */
    suspend fun getBookedSlotsForStylist(date: String, stylistId: String): Result<Set<String>> = runCatching {
        bookings()
            .whereEqualTo("date", date)
            .whereEqualTo("stylistId", stylistId)
            .get().await()
            .toObjects(Booking::class.java)
            .filter { it.statusEnum != BookingStatus.CANCELLED }
            .map { it.timeSlot }
            .toSet()
    }

    suspend fun createBooking(booking: Booking): Result<String> = runCatching {
        val ref = bookings().document()
        ref.set(booking.copy(id = ref.id)).await()
        ref.id
    }

    suspend fun getBookingsForUser(userId: String): Result<List<Booking>> = runCatching {
        bookings()
            .whereEqualTo("userId", userId)
            .get().await()
            .toObjects(Booking::class.java)
            .sortedWith(compareByDescending<Booking> { it.date }.thenByDescending { it.timeSlot })
    }

    suspend fun cancelBooking(bookingId: String): Result<Unit> = runCatching {
        bookings().document(bookingId).update("status", BookingStatus.CANCELLED.name).await()
        Unit
    }
}
