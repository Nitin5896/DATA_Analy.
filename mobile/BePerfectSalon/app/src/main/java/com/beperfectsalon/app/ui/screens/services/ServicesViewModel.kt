package com.beperfectsalon.app.ui.screens.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beperfectsalon.app.data.model.Service
import com.beperfectsalon.app.data.repository.RepositoryProvider
import com.beperfectsalon.app.data.repository.ServiceRepository
import com.beperfectsalon.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServicesViewModel(
    private val repository: ServiceRepository = RepositoryProvider.services,
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<List<Service>>>(Resource.Loading)
    val uiState: StateFlow<Resource<List<Service>>> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = Resource.Loading
        viewModelScope.launch {
            val result = repository.getActiveServices()
            _uiState.value = result.fold(
                onSuccess = { Resource.Success(it) },
                onFailure = { Resource.Error(it.message ?: "Could not load services.") },
            )
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun categoriesOf(services: List<Service>): List<String> =
        services.map { it.category }.distinct().sorted()

    fun filtered(services: List<Service>): List<Service> {
        val category = _selectedCategory.value ?: return services
        return services.filter { it.category == category }
    }
}
