package com.mkiran.homeassignment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_stations")
data class ServiceStationEntity(
    @PrimaryKey val id: String,
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