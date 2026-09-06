package com.beperfectsalon.app.ui.screens.services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beperfectsalon.app.ui.components.ErrorMessage
import com.beperfectsalon.app.ui.components.FullScreenLoading
import com.beperfectsalon.app.ui.screens.booking.BookingCart
import com.beperfectsalon.app.util.Resource

@Composable
fun ServiceDetailScreen(
    serviceId: String,
    viewModel: ServicesViewModel = viewModel(),
    onProceedToBooking: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val cartSelected by BookingCart.selected.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        when (val current = state) {
            is Resource.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is Resource.Error -> ErrorMessage(current.message, modifier = Modifier.padding(padding))
            is Resource.Success -> {
                val service = current.data.firstOrNull { it.id == serviceId }
                if (service == null) {
                    ErrorMessage("This service is no longer available.", modifier = Modifier.padding(padding))
                } else {
                    val selected = cartSelected.any { it.id == service.id }
                    Column(
                        modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(service.name, style = MaterialTheme.typography.headlineMedium)
                        Text(service.category, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text("${service.durationMinutes} minutes", style = MaterialTheme.typography.bodyLarge)
                        Text("₹${service.price}", style = MaterialTheme.typography.titleLarge)
                        if (service.description.isNotBlank()) {
                            Text(service.description, style = MaterialTheme.typography.bodyLarge)
                        }
                        Button(
                            onClick = {
                                if (!selected) BookingCart.toggle(service)
                                onProceedToBooking()
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        ) {
                            Text(if (selected) "Continue to Booking" else "Add & Continue to Booking")
                        }
                    }
                }
            }
        }
    }
}
