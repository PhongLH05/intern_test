package com.example.mobile_intern_test.data.api

import com.example.mobile_intern_test.data.model.LocationDetail
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApiService {
    @GET("search")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("extratags") extraTags: Int = 1,
        @Query("namedetails") nameDetails: Int = 1,
        @Query("countrycodes") countryCodes: String? = null,
        @Query("bounded") bounded: Int = 0,
        @Query("viewbox") viewBox: String? = null,
        @Query("exclude_place_ids") excludePlaceIds: String? = null
    ): List<LocationDetail>
    
    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("extratags") extraTags: Int = 1,
        @Query("namedetails") nameDetails: Int = 1,
        @Query("zoom") zoom: Int = 18
    ): LocationDetail
}
