package com.beperfectsalon.app.ui.screens.services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beperfectsalon.app.ui.components.ErrorMessage
import com.beperfectsalon.app.ui.components.FullScreenLoading
import com.beperfectsalon.app.ui.components.ServiceListRow
import com.beperfectsalon.app.ui.screens.booking.BookingCart
import com.beperfectsalon.app.util.Resource

@Composable
fun ServicesScreen(
    viewModel: ServicesViewModel = viewModel(),
    onOpenService: (String) -> Unit,
    onProceedToBooking: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val cartSelected by BookingCart.selected.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Services") }) },
        floatingActionButton = {
            if (cartSelected.isNotEmpty()) {
                FloatingActionButton(onClick = onProceedToBooking) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Proceed to booking (${cartSelected.size} selected)")
                }
            }
        },
    ) { padding ->
        when (val current = state) {
            is Resource.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is Resource.Error -> ErrorMessage(current.message, modifier = Modifier.padding(padding))
            is Resource.Success -> {
                val categories = viewModel.categoriesOf(current.data)
                val filtered = viewModel.filtered(current.data)
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    if (categories.size > 1) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            itemsIndexed(listOf<String?>(null) + categories) { _, category ->
                                FilterChip(
                                    selected = selectedCategory == category,
                                    onClick = { viewModel.selectCategory(category) },
                                    label = { Text(category ?: "All") },
                                )
                            }
                        }
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            itemsIndexed(filtered) { _, service ->
                                ServiceListRow(
                                    service = service,
                                    selectable = true,
                                    selected = cartSelected.any { it.id == service.id },
                                    onClick = { onOpenService(service.id) },
                                    onToggleSelected = { BookingCart.toggle(service) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
