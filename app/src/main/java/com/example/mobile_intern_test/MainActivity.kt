package com.example.mobile_intern_test

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobile_intern_test.data.repository.LocationRepository
import com.example.mobile_intern_test.databinding.ActivityMainBinding
import com.example.mobile_intern_test.di.NetworkModule
import com.example.mobile_intern_test.ui.adapter.LocationAdapter
import com.example.mobile_intern_test.ui.viewmodel.LocationSearchViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: LocationSearchViewModel
    private lateinit var locationAdapter: LocationAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupRecyclerView()
        setupSearchInput()
        observeViewModel()
        showEmptyState()
    }
    
    private fun setupViewModel() {
        val repository = LocationRepository(NetworkModule.locationApiService)
        
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LocationSearchViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return LocationSearchViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        
        viewModel = ViewModelProvider(this, factory)[LocationSearchViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        locationAdapter = LocationAdapter(emptyList(), "")
        binding.rvAddresses.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = locationAdapter
        }
    }
    
    private fun setupSearchInput() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                if (query.isBlank()) {
                    showEmptyState()
                } else {
                    performSearch(query)
                }
            }
        })
    }
    
    private fun performSearch(query: String) {
        // Search with country code filter for Vietnam (VN) for better accuracy
        viewModel.searchLocations(query, "VN")
    }
    
    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { locations ->
            if (locations.isNotEmpty()) {
                showResults(locations)
            } else {
                showEmptyState()
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                showError(it)
                viewModel.clearError()
            }
        }
    }
    
    private fun showResults(locations: List<com.example.mobile_intern_test.data.model.LocationDetail>) {
        binding.rvAddresses.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        
        val currentQuery = binding.etSearch.text.toString()
        locationAdapter = LocationAdapter(locations, currentQuery)
        binding.rvAddresses.adapter = locationAdapter
    }
    
    private fun showEmptyState() {
        binding.rvAddresses.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
    }
    
    private fun showError(message: String) {
        binding.rvAddresses.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = message
        
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}