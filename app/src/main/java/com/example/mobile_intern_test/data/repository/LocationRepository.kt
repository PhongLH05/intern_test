package com.example.mobile_intern_test.data.repository

import com.example.mobile_intern_test.data.api.LocationApiService
import com.example.mobile_intern_test.data.model.LocationDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepository(private val apiService: LocationApiService) {
    
    suspend fun searchLocations(
        query: String,
        countryCodes: String? = null,
        limit: Int = 20
    ): Result<List<LocationDetail>> {
        return withContext(Dispatchers.IO) {
            try {
                val locations = apiService.searchLocation(
                    query = query,
                    limit = limit,
                    countryCodes = countryCodes
                )
                
                // Sort results by importance score for better accuracy
                val sortedLocations = locations.sortedByDescending { it.importance }
                
                Result.success(sortedLocations)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double
    ): Result<LocationDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val location = apiService.reverseGeocode(
                    latitude = latitude,
                    longitude = longitude
                )
                Result.success(location)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun searchLocationsWithFilters(
        query: String,
        filters: LocationSearchFilters
    ): Result<List<LocationDetail>> {
        return withContext(Dispatchers.IO) {
            try {
                val locations = apiService.searchLocation(
                    query = query,
                    limit = filters.limit,
                    countryCodes = filters.countryCodes,
                    bounded = if (filters.bounded) 1 else 0,
                    viewBox = filters.viewBox
                )
                
                // Apply additional filtering and sorting
                val filteredLocations = locations
                    .filter { location -> 
                        filters.minImportance == null || location.importance >= filters.minImportance 
                    }
                    .filter { location ->
                        filters.locationTypes.isEmpty() || filters.locationTypes.contains(location.type)
                    }
                    .sortedByDescending { it.importance }
                
                Result.success(filteredLocations)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

data class LocationSearchFilters(
    val limit: Int = 20,
    val countryCodes: String? = null,
    val bounded: Boolean = false,
    val viewBox: String? = null,
    val minImportance: Double? = null,
    val locationTypes: List<String> = emptyList()
)
