package com.beperfectsalon.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beperfectsalon.app.data.model.UserProfile
import com.beperfectsalon.app.data.repository.AuthRepository
import com.beperfectsalon.app.data.repository.RepositoryProvider
import com.beperfectsalon.app.util.Resource
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository = RepositoryProvider.auth,
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<UserProfile>>(Resource.Loading)
    val uiState: StateFlow<Resource<UserProfile>> = _uiState.asStateFlow()

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState: StateFlow<Resource<Unit>?> = _saveState.asStateFlow()

    private val _deleteAccountState = MutableStateFlow<Resource<Unit>?>(null)
    val deleteAccountState: StateFlow<Resource<Unit>?> = _deleteAccountState.asStateFlow()

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    fun load() {
        val user = authRepository.currentUser ?: run {
            _uiState.value = Resource.Error("Not logged in.")
            return
        }
        _uiState.value = Resource.Loading
        viewModelScope.launch {
            val result = authRepository.getProfile(user.uid)
            _uiState.value = result.fold(
                onSuccess = { profile -> profile?.let { Resource.Success(it) } ?: Resource.Error("Profile not found.") },
                onFailure = { Resource.Error(it.message ?: "Could not load your profile.") },
            )
        }
    }

    fun save(name: String, phone: String) {
        val user = authRepository.currentUser ?: return
        _saveState.value = Resource.Loading
        viewModelScope.launch {
            val result = authRepository.updateProfile(user.uid, name, phone)
            _saveState.value = result.fold(
                onSuccess = { Resource.Success(Unit) },
                onFailure = { Resource.Error(it.message ?: "Could not save your profile.") },
            )
            if (result.isSuccess) load()
        }
    }

    fun logout() = authRepository.logout()

    fun deleteAccount() {
        val user = authRepository.currentUser ?: return
        _deleteAccountState.value = Resource.Loading
        viewModelScope.launch {
            val result = authRepository.deleteAccount(user.uid)
            _deleteAccountState.value = result.fold(
                onSuccess = { Resource.Success(Unit) },
                onFailure = {
                    val message = if (it is FirebaseAuthRecentLoginRequiredException) {
                        "For your security, please log out and log back in before deleting your account."
                    } else {
                        it.message ?: "Could not delete your account. Please try again."
                    }
                    Resource.Error(message)
                },
            )
        }
    }

    fun consumeDeleteAccountState() {
        _deleteAccountState.value = null
    }
}
