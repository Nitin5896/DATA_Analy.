package com.beperfectsalon.app.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Firestore's default (non-kotlin-reflect) POJO mapper deserializes by calling a public
 * zero-arg constructor and then public setters - so, per Firebase's own Kotlin guidance,
 * every mapped property here is a `var`, and @JvmOverloads generates the zero-arg constructor
 * overload that a Kotlin data class with only default values doesn't produce on its own.
 * @IgnoreExtraProperties tolerates fields added in Firestore later without crashing older
 * app versions.
 */

@IgnoreExtraProperties
data class Service @JvmOverloads constructor(
    @DocumentId var id: String = "",
    var name: String = "",
    var category: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var durationMinutes: Int = 30,
    var imageUrl: String = "",
    var popular: Boolean = false,
    var active: Boolean = true,
)

@IgnoreExtraProperties
data class Stylist @JvmOverloads constructor(
    @DocumentId var id: String = "",
    var name: String = "",
    var specialization: String = "",
    var experienceYears: Int = 0,
    var imageUrl: String = "",
    var active: Boolean = true,
)

enum class BookingStatus {
    PENDING, CONFIRMED, COMPLETED, CANCELLED
}

@IgnoreExtraProperties
data class Booking @JvmOverloads constructor(
    @DocumentId var id: String = "",
    var userId: String = "",
    var customerName: String = "",
    var customerPhone: String = "",
    var serviceIds: List<String> = emptyList(),
    var serviceNames: List<String> = emptyList(),
    var stylistId: String? = null,
    var stylistName: String? = null,
    var date: String = "", // yyyy-MM-dd
    var timeSlot: String = "", // e.g. "10:30 AM"
    var totalPrice: Double = 0.0,
    var totalDurationMinutes: Int = 0,
    var status: String = BookingStatus.PENDING.name,
    var notes: String = "",
    @ServerTimestamp var createdAt: Date? = null,
) {
    @get:Exclude
    val statusEnum: BookingStatus
        get() = runCatching { BookingStatus.valueOf(status) }.getOrDefault(BookingStatus.PENDING)
}

@IgnoreExtraProperties
data class UserProfile @JvmOverloads constructor(
    @DocumentId var uid: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    @ServerTimestamp var createdAt: Date? = null,
)

@IgnoreExtraProperties
data class GalleryItem @JvmOverloads constructor(
    @DocumentId var id: String = "",
    var imageUrl: String = "",
    var caption: String = "",
    var category: String = "General",
)

@IgnoreExtraProperties
data class Offer @JvmOverloads constructor(
    @DocumentId var id: String = "",
    var title: String = "",
    var description: String = "",
    var discountPercent: Int = 0,
    var validTill: String = "", // yyyy-MM-dd
    var imageUrl: String = "",
    var active: Boolean = true,
)
