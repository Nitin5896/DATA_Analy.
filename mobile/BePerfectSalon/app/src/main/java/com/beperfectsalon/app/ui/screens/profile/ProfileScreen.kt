package com.beperfectsalon.app.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beperfectsalon.app.ui.components.ErrorMessage
import com.beperfectsalon.app.ui.components.FullScreenLoading
import com.beperfectsalon.app.util.Resource

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onLoggedOut: () -> Unit,
    onOpenAbout: () -> Unit,
    onLoginRequired: () -> Unit,
    onAccountDeleted: () -> Unit,
) {
    val loggedIn = viewModel.isLoggedIn()

    Scaffold(topBar = { TopAppBar(title = { Text("Profile") }) }) { padding ->
        if (!loggedIn) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("You're browsing as a guest.")
                Button(onClick = onLoginRequired, modifier = Modifier.fillMaxWidth()) {
                    Text("Log in / Sign up")
                }
                OutlinedButton(onClick = onOpenAbout, modifier = Modifier.fillMaxWidth()) {
                    Text("About & Contact")
                }
            }
            return@Scaffold
        }

        LaunchedEffect(Unit) { viewModel.load() }
        val state by viewModel.uiState.collectAsState()
        val saveState by viewModel.saveState.collectAsState()
        val deleteAccountState by viewModel.deleteAccountState.collectAsState()
        var showDeleteConfirm by remember { mutableStateOf(false) }

        LaunchedEffect(deleteAccountState) {
            if (deleteAccountState is Resource.Success) {
                viewModel.consumeDeleteAccountState()
                onAccountDeleted()
            }
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete your account?") },
                text = { Text("This permanently deletes your profile and all your bookings. This cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirm = false
                            viewModel.deleteAccount()
                        },
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
                },
            )
        }

        when (val current = state) {
            is Resource.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is Resource.Error -> ErrorMessage(current.message, modifier = Modifier.padding(padding))
            is Resource.Success -> {
                var name by remember(current.data.name) { mutableStateOf(current.data.name) }
                var phone by remember(current.data.phone) { mutableStateOf(current.data.phone) }

                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(current.data.email, style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(name, { name = it }, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())

                    val currentSaveState = saveState
                    if (currentSaveState is Resource.Error) {
                        Text(currentSaveState.message, color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = { viewModel.save(name, phone) },
                        enabled = currentSaveState !is Resource.Loading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Save Changes")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(onClick = onOpenAbout, modifier = Modifier.fillMaxWidth()) {
                        Text("About & Contact")
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.logout()
                            onLoggedOut()
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Log Out")
                    }

                    val currentDeleteState = deleteAccountState
                    if (currentDeleteState is Resource.Error) {
                        Text(currentDeleteState.message, color = MaterialTheme.colorScheme.error)
                    }

                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        enabled = currentDeleteState !is Resource.Loading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            if (currentDeleteState is Resource.Loading) "Deleting account..." else "Delete My Account",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}
