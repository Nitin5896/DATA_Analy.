package com.beperfectsalon.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beperfectsalon.app.data.repository.AuthRepository
import com.beperfectsalon.app.data.repository.RepositoryProvider
import com.beperfectsalon.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = RepositoryProvider.auth,
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<Unit>?>(null)
    val authState: StateFlow<Resource<Unit>?> = _authState.asStateFlow()

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = Resource.Error("Enter your email and password.")
            return
        }
        _authState.value = Resource.Loading
        viewModelScope.launch {
            val result = authRepository.login(email.trim(), password)
            _authState.value = result.fold(
                onSuccess = { Resource.Success(Unit) },
                onFailure = { Resource.Error(it.message ?: "Login failed. Please try again.") },
            )
        }
    }

    fun signUp(name: String, phone: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.length < 6) {
            _authState.value = Resource.Error("Enter your name, a valid email, and a password of at least 6 characters.")
            return
        }
        _authState.value = Resource.Loading
        viewModelScope.launch {
            val result = authRepository.signUp(name.trim(), email.trim(), password, phone.trim())
            _authState.value = result.fold(
                onSuccess = { Resource.Success(Unit) },
                onFailure = { Resource.Error(it.message ?: "Sign up failed. Please try again.") },
            )
        }
    }

    fun consumeState() {
        _authState.value = null
    }
}
