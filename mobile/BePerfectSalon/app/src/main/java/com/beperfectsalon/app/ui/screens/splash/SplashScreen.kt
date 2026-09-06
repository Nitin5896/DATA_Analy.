package com.beperfectsalon.app.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.beperfectsalon.app.R
import com.beperfectsalon.app.data.repository.AuthRepository
import com.beperfectsalon.app.data.repository.RepositoryProvider
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    authRepository: AuthRepository = RepositoryProvider.auth,
    onNavigate: (loggedIn: Boolean) -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(600) // brief brand moment, not a fake loading spinner
        onNavigate(authRepository.isLoggedIn())
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = stringResource(R.string.salon_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
