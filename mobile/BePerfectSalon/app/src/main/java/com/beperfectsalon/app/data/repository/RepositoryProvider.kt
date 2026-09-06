package com.beperfectsalon.app.data.repository

/**
 * Simple manual dependency provider. A single-module app like this doesn't need a DI
 * framework (Hilt/Koin) - it would add build complexity without a real benefit here.
 */
object RepositoryProvider {
    val auth by lazy { AuthRepository() }
    val services by lazy { ServiceRepository() }
    val bookings by lazy { BookingRepository() }
    val content by lazy { ContentRepository() }
}
