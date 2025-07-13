package com.mkiran.homeassignment.domain.repository

import com.mkiran.homeassignment.domain.model.ServiceStationDto

interface ServiceStationRepository {
    suspend fun getServiceStations(): List<ServiceStationDto>
    suspend fun cacheServiceStations(stations: List<ServiceStationDto>)
    suspend fun getCachedServiceStations(): List<ServiceStationDto>
    suspend fun clearCache()
} 