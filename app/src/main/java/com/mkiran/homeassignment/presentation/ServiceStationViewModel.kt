package com.mkiran.homeassignment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkiran.homeassignment.domain.model.ServiceStationDto
import com.mkiran.homeassignment.domain.repository.ServiceStationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceStationViewModel @Inject constructor(
    private val repository: ServiceStationRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(ServiceStationFilter())
    val filter: StateFlow<ServiceStationFilter> = _filter.asStateFlow()

    private val _uiState = MutableStateFlow<ServiceStationUiState>(ServiceStationUiState.Loading)
    val uiState: StateFlow<ServiceStationUiState> = _uiState.asStateFlow()

    init {
        fetchStations()
    }

    fun updateFilter(newFilter: ServiceStationFilter) {
        _filter.value = newFilter
        filterStations()
    }

    fun setRadius(value: Double) {
        _filter.value = _filter.value.copy(radius = value)
        filterStations()
    }

    fun setIsOpen24Hours(value: Boolean) {
        _filter.value = _filter.value.copy(isOpen24Hours = value)
        filterStations()
    }

    fun setHasConvenienceStore(value: Boolean) {
        _filter.value = _filter.value.copy(hasConvenienceStore = value)
        filterStations()
    }

    fun setHasHotFood(value: Boolean) {
        _filter.value = _filter.value.copy(hasHotFood = value)
        filterStations()
    }

    fun setAcceptsBpFuelCards(value: Boolean) {
        _filter.value = _filter.value.copy(acceptsBpFuelCards = value)
        filterStations()
    }

    fun fetchStations() {
        viewModelScope.launch {
            // Step 1: Load data from DB first if exists
            val cachedStations = repository.getCachedServiceStations()
            if (cachedStations.isNotEmpty()) {
                filterStations(cachedStations)
            } else {
                _uiState.value = ServiceStationUiState.Loading
            }
            
            // Step 2: If no cached data, fetch from API and save to DB
            if (cachedStations.isEmpty()) {
                try {
                    val freshStations = repository.getServiceStations()
                    repository.cacheServiceStations(freshStations)
                    filterStations(freshStations)
                } catch (e: Exception) {
                    _uiState.value = ServiceStationUiState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    private fun filterStations(source: List<ServiceStationDto>? = null) {
        viewModelScope.launch {
            val allStations = source ?: repository.getCachedServiceStations()
            val f = _filter.value
            val filtered = allStations.filter {
                it.distanceMiles <= f.radius &&
                (!f.isOpen24Hours || it.isOpen24Hours) &&
                (!f.hasConvenienceStore || it.hasConvenienceStore) &&
                (!f.hasHotFood || it.hasHotFood) &&
                (!f.acceptsBpFuelCards || it.acceptsBpFuelCards)
            }
            _uiState.value = when {
                filtered.isEmpty() -> ServiceStationUiState.Empty
                else -> ServiceStationUiState.Success(filtered)
            }
        }
    }
}