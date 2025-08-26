package com.example.mobile_intern_test.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_intern_test.data.model.LocationDetail
import com.example.mobile_intern_test.data.repository.LocationRepository
import com.example.mobile_intern_test.data.repository.LocationSearchFilters
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationSearchViewModel(private val repository: LocationRepository) : ViewModel() {
    
    private val _searchResults = MutableLiveData<List<LocationDetail>>()
    val searchResults: LiveData<List<LocationDetail>> = _searchResults
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private var searchJob: Job? = null
    
    fun searchLocations(query: String, countryCodes: String? = null) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        searchJob?.cancel()
        
        searchJob = viewModelScope.launch {
            delay(1000) // Apply 1-second debounce as requested
            
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = repository.searchLocations(query, countryCodes, 25)
                result.fold(
                    onSuccess = { locations ->
                        _searchResults.value = locations
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Search failed"
                        _searchResults.value = emptyList()
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Search failed"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchLocationsWithFilters(query: String, filters: LocationSearchFilters) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        searchJob?.cancel()
        
        searchJob = viewModelScope.launch {
            delay(1000) // Apply 1-second debounce as requested
            
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = repository.searchLocationsWithFilters(query, filters)
                result.fold(
                    onSuccess = { locations ->
                        _searchResults.value = locations
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Search failed"
                        _searchResults.value = emptyList()
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Search failed"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
   
    private fun expandSearchQuery(query: String): String {
        // Add common location suffixes if not present
        val locationSuffixes = listOf("street", "road", "avenue", "district", "ward", "city", "province")
        val queryLower = query.lowercase()
        
        // If query doesn't contain location suffixes, add them
        val hasSuffix = locationSuffixes.any { suffix -> queryLower.contains(suffix) }
        if (!hasSuffix) {
            return "$query district"
        }
        return query
    }
    
    fun reverseGeocode(latitude: Double, longitude: Double) {
        searchJob?.cancel()
        
        searchJob = viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = repository.reverseGeocode(latitude, longitude)
                result.fold(
                    onSuccess = { location ->
                        _searchResults.value = listOf(location)
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Reverse geocoding failed"
                        _searchResults.value = emptyList()
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Reverse geocoding failed"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearResults() {
        _searchResults.value = emptyList()
    }
}
