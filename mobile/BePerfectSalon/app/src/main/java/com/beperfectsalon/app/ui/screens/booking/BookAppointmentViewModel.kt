package com.beperfectsalon.app.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beperfectsalon.app.data.model.Booking
import com.beperfectsalon.app.data.model.Service
import com.beperfectsalon.app.data.model.Stylist
import com.beperfectsalon.app.data.model.UserProfile
import com.beperfectsalon.app.data.repository.AuthRepository
import com.beperfectsalon.app.data.repository.BookingRepository
import com.beperfectsalon.app.data.repository.RepositoryProvider
import com.beperfectsalon.app.data.repository.ServiceRepository
import com.beperfectsalon.app.util.Resource
import com.beperfectsalon.app.util.SalonHours
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class BookAppointmentUiState(
    val stylists: List<Stylist> = emptyList(),
    val selectedStylist: Stylist? = null, // null = "Any stylist"
    val selectedDate: String = SalonHours.dateFormat.format(Date()),
    val availableSlots: List<String> = SalonHours.allSlotsForDay(),
    val selectedSlot: String? = null,
    val loadingSlots: Boolean = false,
    val submission: Resource<String>? = null,
)

class BookAppointmentViewModel(
    private val serviceRepository: ServiceRepository = RepositoryProvider.services,
    private val bookingRepository: BookingRepository = RepositoryProvider.bookings,
    private val authRepository: AuthRepository = RepositoryProvider.auth,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookAppointmentUiState())
    val uiState: StateFlow<BookAppointmentUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val stylists = serviceRepository.getActiveStylists().getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(stylists = stylists)
            refreshAvailability()
        }
    }

    fun selectDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date, selectedSlot = null)
        refreshAvailability()
    }

    fun selectStylist(stylist: Stylist?) {
        _uiState.value = _uiState.value.copy(selectedStylist = stylist, selectedSlot = null)
        refreshAvailability()
    }

    fun selectSlot(slot: String) {
        _uiState.value = _uiState.value.copy(selectedSlot = slot)
    }

    private fun refreshAvailability() {
        val stylist = _uiState.value.selectedStylist ?: run {
            // No specific stylist chosen: salon assigns staff, so every future slot is bookable.
            _uiState.value = _uiState.value.copy(
                availableSlots = futureSlotsOnly(_uiState.value.selectedDate),
            )
            return
        }
        _uiState.value = _uiState.value.copy(loadingSlots = true)
        viewModelScope.launch {
            val bookedSlots = bookingRepository
                .getBookedSlotsForStylist(_uiState.value.selectedDate, stylist.id)
                .getOrDefault(emptySet())
            val available = futureSlotsOnly(_uiState.value.selectedDate).filterNot { it in bookedSlots }
            _uiState.value = _uiState.value.copy(availableSlots = available, loadingSlots = false)
        }
    }

    private fun futureSlotsOnly(date: String): List<String> =
        SalonHours.allSlotsForDay().filter { SalonHours.isSlotInFuture(date, it) }

    fun confirmBooking(selectedServices: List<Service>, notes: String) {
        val state = _uiState.value
        val slot = state.selectedSlot
        val user = authRepository.currentUser
        if (slot == null) {
            _uiState.value = state.copy(submission = Resource.Error("Please choose a time slot."))
            return
        }
        if (selectedServices.isEmpty()) {
            _uiState.value = state.copy(submission = Resource.Error("Please select at least one service."))
            return
        }
        if (user == null) {
            _uiState.value = state.copy(submission = Resource.Error("Please log in to book an appointment."))
            return
        }

        _uiState.value = state.copy(submission = Resource.Loading)
        viewModelScope.launch {
            val profile: UserProfile? = authRepository.getProfile(user.uid).getOrNull()
            val booking = Booking(
                userId = user.uid,
                customerName = profile?.name.orEmpty().ifBlank { user.email.orEmpty() },
                customerPhone = profile?.phone.orEmpty(),
                serviceIds = selectedServices.map { it.id },
                serviceNames = selectedServices.map { it.name },
                stylistId = state.selectedStylist?.id,
                stylistName = state.selectedStylist?.name,
                date = state.selectedDate,
                timeSlot = slot,
                totalPrice = selectedServices.sumOf { it.price },
                totalDurationMinutes = selectedServices.sumOf { it.durationMinutes },
                notes = notes,
            )
            val result = bookingRepository.createBooking(booking)
            _uiState.value = _uiState.value.copy(
                submission = result.fold(
                    onSuccess = { Resource.Success(it) },
                    onFailure = { Resource.Error(it.message ?: "Could not create the booking. Please try again.") },
                ),
            )
        }
    }

    fun consumeSubmission() {
        _uiState.value = _uiState.value.copy(submission = null)
    }
}
