package com.mkiran.homeassignment.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ServiceStationEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceStationDao(): ServiceStationDao
} 