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
import com.example.mobile_intern_test.data.repository.AddressRepository
import com.example.mobile_intern_test.databinding.ActivityMainBinding
import com.example.mobile_intern_test.di.NetworkModule
import com.example.mobile_intern_test.ui.adapter.AddressAdapter
import com.example.mobile_intern_test.ui.viewmodel.AddressSearchViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AddressSearchViewModel
    private lateinit var addressAdapter: AddressAdapter
    
    private val HERE_API_KEY = "IQrjZrk2hHHIRTeCkoPAsXTAc-oWy7MI_a-tLfScIEY"
    
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
        val repository = AddressRepository(NetworkModule.hereApiService)
        
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AddressSearchViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AddressSearchViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        
        viewModel = ViewModelProvider(this, factory)[AddressSearchViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(emptyList(), "")
        binding.rvAddresses.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = addressAdapter
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
        if (HERE_API_KEY == "YOUR_HERE_API_KEY_HERE") {
            showError("Please add your HERE API key to the MainActivity")
            return
        }
        
        viewModel.searchAddresses(query, HERE_API_KEY)
    }
    
    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { addresses ->
            if (addresses.isNotEmpty()) {
                showResults(addresses)
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
    
    private fun showResults(addresses: List<com.example.mobile_intern_test.data.model.AddressItem>) {
        binding.rvAddresses.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        
        val currentQuery = binding.etSearch.text.toString()
        addressAdapter = AddressAdapter(addresses, currentQuery)
        binding.rvAddresses.adapter = addressAdapter
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