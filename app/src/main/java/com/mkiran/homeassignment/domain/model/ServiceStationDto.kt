package com.mkiran.homeassignment.domain.model

data class ServiceStationDto(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMiles: Double,
    val isOpen24Hours: Boolean,
    val hasConvenienceStore: Boolean,
    val hasHotFood: Boolean,
    val acceptsBpFuelCards: Boolean,
    val imageUrl: String?
) 