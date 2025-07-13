package com.mkiran.homeassignment.presentation

import com.mkiran.homeassignment.domain.model.ServiceStationDto

sealed class ServiceStationUiState {
    object Loading : ServiceStationUiState()
    data class Success(val stations: List<ServiceStationDto>) : ServiceStationUiState()
    data class Error(val message: String) : ServiceStationUiState()
    object Empty : ServiceStationUiState()
}