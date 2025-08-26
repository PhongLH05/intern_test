package com.example.mobile_intern_test.data.repository

import com.example.mobile_intern_test.data.api.HereApiService
import com.example.mobile_intern_test.data.model.AddressItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddressRepository(private val apiService: HereApiService) {
    
    suspend fun searchAddresses(query: String, apiKey: String): Result<List<AddressItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchAddress(query, apiKey)
                Result.success(response.items)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
