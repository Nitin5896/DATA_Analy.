package com.beperfectsalon.app.ui.screens.mybookings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beperfectsalon.app.data.model.Booking
import com.beperfectsalon.app.data.model.BookingStatus
import com.beperfectsalon.app.ui.components.ErrorMessage
import com.beperfectsalon.app.ui.components.FullScreenLoading
import com.beperfectsalon.app.ui.components.StatusBadge
import com.beperfectsalon.app.util.Resource

@Composable
fun MyBookingsScreen(
    viewModel: MyBookingsViewModel = viewModel(),
    onLoginRequired: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("My Bookings") }) }) { padding ->
        when (val current = state) {
            is Resource.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is Resource.Error -> Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                ErrorMessage(current.message)
                if (!viewModel.isLoggedIn()) {
                    Button(onClick = onLoginRequired, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                        Text("Log in")
                    }
                }
            }
            is Resource.Success -> {
                if (current.data.isEmpty()) {
                    ErrorMessage("You have no bookings yet.", modifier = Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        itemsIndexed(current.data) { _, booking ->
                            BookingCard(booking = booking, onCancel = { viewModel.cancelBooking(booking.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingCard(booking: Booking, onCancel: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(booking.serviceNames.joinToString(", "), style = MaterialTheme.typography.titleMedium)
                StatusBadge(booking.statusEnum)
            }
            Text("${booking.date} at ${booking.timeSlot}", style = MaterialTheme.typography.bodyMedium)
            if (!booking.stylistName.isNullOrBlank()) {
                Text("Stylist: ${booking.stylistName}", style = MaterialTheme.typography.bodyMedium)
            }
            Text("Total: ₹${booking.totalPrice} · ${booking.totalDurationMinutes} min", style = MaterialTheme.typography.bodyMedium)
            if (booking.statusEnum == BookingStatus.PENDING || booking.statusEnum == BookingStatus.CONFIRMED) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.padding(top = 4.dp)) {
                    Text("Cancel Booking")
                }
            }
        }
    }
}
