package com.example.mobile_intern_test.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_intern_test.data.model.AddressItem
import com.example.mobile_intern_test.data.repository.AddressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddressSearchViewModel(private val repository: AddressRepository) : ViewModel() {
    
    private val _searchResults = MutableLiveData<List<AddressItem>>()
    val searchResults: LiveData<List<AddressItem>> = _searchResults
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private var searchJob: Job? = null
    
    fun searchAddresses(query: String, apiKey: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        searchJob?.cancel()
        
        searchJob = viewModelScope.launch {
            delay(1000)
            
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = repository.searchAddresses(query, apiKey)
                result.fold(
                    onSuccess = { addresses ->
                        _searchResults.value = addresses
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
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearResults() {
        _searchResults.value = emptyList()
    }
}
