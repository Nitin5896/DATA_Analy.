package com.beperfectsalon.app.ui.screens.offers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beperfectsalon.app.data.model.Offer
import com.beperfectsalon.app.data.repository.ContentRepository
import com.beperfectsalon.app.data.repository.RepositoryProvider
import com.beperfectsalon.app.ui.components.ErrorMessage
import com.beperfectsalon.app.ui.components.FullScreenLoading
import com.beperfectsalon.app.util.Resource

@Composable
fun OffersScreen(contentRepository: ContentRepository = RepositoryProvider.content) {
    var state by remember { mutableStateOf<Resource<List<Offer>>>(Resource.Loading) }

    LaunchedEffect(Unit) {
        val result = contentRepository.getActiveOffers()
        state = result.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error(it.message ?: "Could not load offers.") },
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Offers") }) }) { padding ->
        when (val current = state) {
            is Resource.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is Resource.Error -> ErrorMessage(current.message, modifier = Modifier.padding(padding))
            is Resource.Success -> {
                if (current.data.isEmpty()) {
                    ErrorMessage("No active offers right now. Check back soon!", modifier = Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        itemsIndexed(current.data) { _, offer -> OfferRow(offer) }
                    }
                }
            }
        }
    }
}

@Composable
private fun OfferRow(offer: Offer) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(offer.title, style = MaterialTheme.typography.titleMedium)
            Text(offer.description, style = MaterialTheme.typography.bodyMedium)
            if (offer.discountPercent > 0) {
                Text(
                    "${offer.discountPercent}% OFF",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (offer.validTill.isNotBlank()) {
                Text("Valid till ${offer.validTill}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
