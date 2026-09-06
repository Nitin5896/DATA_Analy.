package com.beperfectsalon.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beperfectsalon.app.data.model.BookingStatus
import com.beperfectsalon.app.ui.theme.ErrorRed
import com.beperfectsalon.app.ui.theme.PendingAmber
import com.beperfectsalon.app.ui.theme.SuccessGreen

@Composable
fun FullScreenLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun FullScreenMessage(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = message, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ErrorMessage(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "Something went wrong", style = MaterialTheme.typography.titleMedium, color = ErrorRed)
        Text(text = message, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun statusColor(status: BookingStatus): Color = when (status) {
    BookingStatus.PENDING -> PendingAmber
    BookingStatus.CONFIRMED -> SuccessGreen
    BookingStatus.COMPLETED -> Color.Gray
    BookingStatus.CANCELLED -> ErrorRed
}

@Composable
fun StatusBadge(status: BookingStatus, modifier: Modifier = Modifier) {
    val color = statusColor(status)
    Surface(
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
    ) {
        Text(
            text = status.name.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

val ScreenPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
