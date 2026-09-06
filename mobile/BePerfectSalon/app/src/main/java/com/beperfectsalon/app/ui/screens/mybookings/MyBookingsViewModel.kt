package com.beperfectsalon.app.ui.screens.mybookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beperfectsalon.app.data.model.Booking
import com.beperfectsalon.app.data.repository.AuthRepository
import com.beperfectsalon.app.data.repository.BookingRepository
import com.beperfectsalon.app.data.repository.RepositoryProvider
import com.beperfectsalon.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyBookingsViewModel(
    private val bookingRepository: BookingRepository = RepositoryProvider.bookings,
    private val authRepository: AuthRepository = RepositoryProvider.auth,
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<List<Booking>>>(Resource.Loading)
    val uiState: StateFlow<Resource<List<Booking>>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        val user = authRepository.currentUser
        if (user == null) {
            _uiState.value = Resource.Error("Please log in to see your bookings.")
            return
        }
        _uiState.value = Resource.Loading
        viewModelScope.launch {
            val result = bookingRepository.getBookingsForUser(user.uid)
            _uiState.value = result.fold(
                onSuccess = { Resource.Success(it) },
                onFailure = { Resource.Error(it.message ?: "Could not load your bookings.") },
            )
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.cancelBooking(bookingId)
            load()
        }
    }

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
}
