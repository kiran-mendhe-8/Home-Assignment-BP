package com.mkiran.homeassignment.data.repository

import com.mkiran.homeassignment.data.local.ServiceStationDao
import com.mkiran.homeassignment.data.mapper.toDto
import com.mkiran.homeassignment.data.mapper.toEntity
import com.mkiran.homeassignment.data.remote.ServiceStationApi
import com.mkiran.homeassignment.domain.model.ServiceStationDto
import com.mkiran.homeassignment.domain.repository.ServiceStationRepository
import javax.inject.Inject

class ServiceStationRepositoryImpl @Inject constructor(
    private val api: ServiceStationApi,
    private val dao: ServiceStationDao
) : ServiceStationRepository {
    override suspend fun getServiceStations(): List<ServiceStationDto> {
        return api.getServiceStations().serviceStations
    }

    override suspend fun cacheServiceStations(stations: List<ServiceStationDto>) {
        dao.clearStations()
        dao.insertStations(stations.map { it.toEntity() })
    }

    override suspend fun getCachedServiceStations(): List<ServiceStationDto> {
        return dao.getAllStations().map { it.toDto() }
    }

    override suspend fun clearCache() {
        dao.clearStations()
    }
} 