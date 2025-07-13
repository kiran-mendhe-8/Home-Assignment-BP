package com.mkiran.homeassignment.data.remote

import retrofit2.http.GET
import com.google.gson.annotations.SerializedName
import com.mkiran.homeassignment.domain.model.ServiceStationDto

data class ServiceStationResponse(
    @SerializedName("service-station")
    val serviceStations: List<ServiceStationDto>
)

interface ServiceStationApi {
    @GET("v3/b/6873fd525fdad557dbe11283?meta=false")
    suspend fun getServiceStations(): ServiceStationResponse
} 