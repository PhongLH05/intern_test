package com.example.mobile_intern_test.ui.adapter

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_intern_test.R
import com.example.mobile_intern_test.data.model.AddressItem
import com.example.mobile_intern_test.databinding.ItemAddressBinding

class AddressAdapter(
    private val addresses: List<AddressItem>,
    private val searchQuery: String
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(private val binding: ItemAddressBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(address: AddressItem) {
            val highlightedTitle = highlightSearchKeywords(address.title, searchQuery)
            binding.tvTitle.text = highlightedTitle
            
            binding.tvAddress.text = address.address.label
            
            binding.root.setOnClickListener {
                openInGoogleMaps(address.position.latitude, address.position.longitude)
            }
        }
        
        private fun highlightSearchKeywords(text: String, query: String): SpannableString {
            val spannableString = SpannableString(text)
            val queryLower = query.lowercase()
            val textLower = text.lowercase()
            
            var startIndex = 0
            while (true) {
                val index = textLower.indexOf(queryLower, startIndex)
                if (index == -1) break
                
                spannableString.setSpan(
                    BackgroundColorSpan(ContextCompat.getColor(itemView.context, R.color.highlight_color)),
                    index,
                    index + query.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                startIndex = index + 1
            }
            
            return spannableString
        }
        
        private fun openInGoogleMaps(latitude: Double, longitude: Double) {
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            
            if (mapIntent.resolveActivity(itemView.context.packageManager) != null) {
                itemView.context.startActivity(mapIntent)
            } else {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                itemView.context.startActivity(fallbackIntent)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return AddressViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addresses[position])
    }
    
    override fun getItemCount(): Int = addresses.size
}
