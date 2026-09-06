package com.beperfectsalon.app.ui.screens.booking

import com.beperfectsalon.app.data.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds the customer's in-progress service selection while they move between the Services
 * list, a service's detail screen, and the Book Appointment screen. A plain singleton (like
 * RepositoryProvider) rather than a scoped ViewModel: Services/ServiceDetail/BookAppointment
 * are separate top-level Navigation-Compose destinations, so a back-stack-entry-scoped
 * ViewModel wouldn't actually be shared between them without extra plumbing this app doesn't
 * need. It's in-memory only and cleared once a booking is confirmed or the app process ends.
 */
object BookingCart {
    private val _selected = MutableStateFlow<List<Service>>(emptyList())
    val selected: StateFlow<List<Service>> = _selected.asStateFlow()

    fun toggle(service: Service) {
        _selected.value = if (_selected.value.any { it.id == service.id }) {
            _selected.value.filterNot { it.id == service.id }
        } else {
            _selected.value + service
        }
    }

    fun clear() {
        _selected.value = emptyList()
    }

    val totalPrice: Double get() = _selected.value.sumOf { it.price }
    val totalDurationMinutes: Int get() = _selected.value.sumOf { it.durationMinutes }
}
