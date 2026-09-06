package com.beperfectsalon.app.ui.screens.booking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beperfectsalon.app.data.model.Stylist
import com.beperfectsalon.app.util.Resource
import com.beperfectsalon.app.util.SalonHours
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookAppointmentScreen(
    viewModel: BookAppointmentViewModel = viewModel(),
    onBookingConfirmed: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val selectedServices by BookingCart.selected.collectAsState()
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(state.submission) {
        if (state.submission is Resource.Success) {
            BookingCart.clear()
            viewModel.consumeSubmission()
            onBookingConfirmed()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Book Appointment") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionLabel("Selected Services")
            if (selectedServices.isEmpty()) {
                Text("No services selected yet. Go back to Services to add some.")
            } else {
                selectedServices.forEach { service ->
                    Text("• ${service.name} — ₹${service.price} (${service.durationMinutes} min)")
                }
                Divider()
                Text(
                    "Total: ₹${BookingCart.totalPrice} · ${BookingCart.totalDurationMinutes} min",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            SectionLabel("Choose a Date")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(SalonHours.nextDays()) { _, date ->
                    FilterChip(
                        selected = state.selectedDate == date,
                        onClick = { viewModel.selectDate(date) },
                        label = { Text(formatDateShort(date)) },
                    )
                }
            }

            SectionLabel("Choose a Stylist")
            StylistDropdown(
                stylists = state.stylists,
                selected = state.selectedStylist,
                onSelect = viewModel::selectStylist,
            )

            SectionLabel("Choose a Time")
            if (state.loadingSlots) {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            } else if (state.availableSlots.isEmpty()) {
                Text("No slots available for this day. Please pick another date.")
            } else {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.availableSlots.forEach { slot ->
                        FilterChip(
                            selected = state.selectedSlot == slot,
                            onClick = { viewModel.selectSlot(slot) },
                            label = { Text(slot) },
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes for the salon (optional)") },
                modifier = Modifier.fillMaxWidth(),
            )

            val submission = state.submission
            if (submission is Resource.Error) {
                Text(submission.message, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.confirmBooking(selectedServices, notes) },
                enabled = submission !is Resource.Loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (submission is Resource.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Confirm Booking")
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Start)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StylistDropdown(stylists: List<Stylist>, selected: Stylist?, onSelect: (Stylist?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected?.name ?: "Any stylist",
            onValueChange = {},
            readOnly = true,
            label = { Text("Stylist") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Any stylist") }, onClick = { onSelect(null); expanded = false })
            stylists.forEach { stylist ->
                DropdownMenuItem(
                    text = { Text("${stylist.name} · ${stylist.specialization}") },
                    onClick = { onSelect(stylist); expanded = false },
                )
            }
        }
    }
}

private fun formatDateShort(isoDate: String): String {
    val parsed = SalonHours.dateFormat.parse(isoDate) ?: return isoDate
    return SimpleDateFormat("EEE d MMM", Locale.getDefault()).format(parsed)
}
