package com.mkiran.homeassignment.presentation

data class ServiceStationFilter(
    val radius: Double = 5.0,
    val isOpen24Hours: Boolean = false,
    val hasConvenienceStore: Boolean = false,
    val hasHotFood: Boolean = false,
    val acceptsBpFuelCards: Boolean = false
)
