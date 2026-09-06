package com.beperfectsalon.app.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beperfectsalon.app.R
import com.beperfectsalon.app.data.model.Offer
import com.beperfectsalon.app.ui.components.ErrorMessage
import com.beperfectsalon.app.ui.components.FullScreenLoading
import com.beperfectsalon.app.ui.components.ServiceFeaturedCard
import com.beperfectsalon.app.util.Resource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onBookNow: () -> Unit,
    onSeeAllServices: () -> Unit,
    onOpenService: (String) -> Unit,
    onOpenGallery: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
    ) { padding ->
        when (val current = state) {
            is Resource.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is Resource.Error -> ErrorMessage(current.message, modifier = Modifier.padding(padding))
            is Resource.Success -> HomeContent(
                data = current.data,
                padding = padding,
                onBookNow = onBookNow,
                onSeeAllServices = onSeeAllServices,
                onOpenService = onOpenService,
                onOpenGallery = onOpenGallery,
                onOpenAbout = onOpenAbout,
            )
        }
    }
}

@Composable
private fun HomeContent(
    data: HomeUiData,
    padding: PaddingValues,
    onBookNow: () -> Unit,
    onSeeAllServices: () -> Unit,
    onOpenService: (String) -> Unit,
    onOpenGallery: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Button(
            onClick = onBookNow,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        ) {
            Text("Book an Appointment")
        }

        if (data.offers.isNotEmpty()) {
            SectionTitle("Current Offers")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(data.offers) { _, offer -> OfferCard(offer) }
            }
        }

        SectionTitle("Popular Services", trailingLabel = "See all", onTrailingClick = onSeeAllServices)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(data.popularServices) { _, service ->
                ServiceFeaturedCard(service = service, onClick = { onOpenService(service.id) })
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(onClick = onOpenGallery, modifier = Modifier.width(160.dp)) { Text("Gallery") }
            Button(onClick = onOpenAbout, modifier = Modifier.width(160.dp)) { Text("About & Contact") }
        }
    }
}

@Composable
private fun SectionTitle(title: String, trailingLabel: String? = null, onTrailingClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        if (trailingLabel != null) {
            Text(
                trailingLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp).clickable(onClick = onTrailingClick),
            )
        }
    }
}

@Composable
private fun OfferCard(offer: Offer) {
    Card(
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(offer.title, style = MaterialTheme.typography.titleMedium)
            Text(offer.description, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
            if (offer.discountPercent > 0) {
                Text(
                    "${offer.discountPercent}% OFF",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}
