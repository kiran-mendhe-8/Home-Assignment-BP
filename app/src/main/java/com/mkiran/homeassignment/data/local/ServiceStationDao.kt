package com.mkiran.homeassignment.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ServiceStationDao {
    @Query("SELECT * FROM service_stations")
    suspend fun getAllStations(): List<ServiceStationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<ServiceStationEntity>)

    @Query("DELETE FROM service_stations")
    suspend fun clearStations()
} 