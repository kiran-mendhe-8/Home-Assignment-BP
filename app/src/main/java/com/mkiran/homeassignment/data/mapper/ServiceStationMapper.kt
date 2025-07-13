package com.mkiran.homeassignment.data.mapper

import com.mkiran.homeassignment.domain.model.ServiceStationDto
import com.mkiran.homeassignment.data.local.ServiceStationEntity

fun ServiceStationDto.toEntity(): ServiceStationEntity = ServiceStationEntity(
    id = id,
    name = name,
    address = address,
    latitude = latitude,
    longitude = longitude,
    distanceMiles = distanceMiles,
    isOpen24Hours = isOpen24Hours,
    hasConvenienceStore = hasConvenienceStore,
    hasHotFood = hasHotFood,
    acceptsBpFuelCards = acceptsBpFuelCards,
    imageUrl = imageUrl
)

fun ServiceStationEntity.toDto(): ServiceStationDto = ServiceStationDto(
    id = id,
    name = name,
    address = address,
    latitude = latitude,
    longitude = longitude,
    distanceMiles = distanceMiles,
    isOpen24Hours = isOpen24Hours,
    hasConvenienceStore = hasConvenienceStore,
    hasHotFood = hasHotFood,
    acceptsBpFuelCards = acceptsBpFuelCards,
    imageUrl = imageUrl
) 