package com.beperfectsalon.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beperfectsalon.app.data.model.Offer
import com.beperfectsalon.app.data.model.Service
import com.beperfectsalon.app.data.repository.ContentRepository
import com.beperfectsalon.app.data.repository.RepositoryProvider
import com.beperfectsalon.app.data.repository.ServiceRepository
import com.beperfectsalon.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiData(val popularServices: List<Service>, val offers: List<Offer>)

class HomeViewModel(
    private val serviceRepository: ServiceRepository = RepositoryProvider.services,
    private val contentRepository: ContentRepository = RepositoryProvider.content,
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<HomeUiData>>(Resource.Loading)
    val uiState: StateFlow<Resource<HomeUiData>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = Resource.Loading
        viewModelScope.launch {
            val servicesResult = serviceRepository.getActiveServices()
            val offersResult = contentRepository.getActiveOffers()

            val services = servicesResult.getOrNull()
            val offers = offersResult.getOrNull()

            if (services == null) {
                _uiState.value = Resource.Error(
                    servicesResult.exceptionOrNull()?.message ?: "Could not load services.",
                )
                return@launch
            }

            _uiState.value = Resource.Success(
                HomeUiData(
                    popularServices = services.filter { it.popular }.ifEmpty { services.take(6) },
                    offers = offers.orEmpty(),
                ),
            )
        }
    }
}
