package com.example.mobile_intern_test.data.api

import com.example.mobile_intern_test.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HereApiService {
    @GET("v1/geocode")
    suspend fun searchAddress(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("limit") limit: Int = 20,
        @Query("lang") language: String = "en"
    ): SearchResponse
}
