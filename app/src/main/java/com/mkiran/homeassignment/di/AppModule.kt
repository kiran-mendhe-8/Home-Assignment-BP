package com.mkiran.homeassignment.di

import android.app.Application
import androidx.room.Room
import com.mkiran.homeassignment.data.local.AppDatabase
import com.mkiran.homeassignment.data.local.ServiceStationDao
import com.mkiran.homeassignment.data.remote.ServiceStationApi
import com.mkiran.homeassignment.data.repository.ServiceStationRepositoryImpl
import com.mkiran.homeassignment.domain.repository.ServiceStationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.jsonbin.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideServiceStationApi(retrofit: Retrofit): ServiceStationApi =
        retrofit.create(ServiceStationApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "service_station_db").build()

    @Provides
    fun provideServiceStationDao(db: AppDatabase): ServiceStationDao = db.serviceStationDao()

    @Provides
    @Singleton
    fun provideServiceStationRepository(
        api: ServiceStationApi,
        dao: ServiceStationDao
    ): ServiceStationRepository = ServiceStationRepositoryImpl(api, dao)
} 