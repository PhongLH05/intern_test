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
import com.example.mobile_intern_test.data.model.LocationDetail
import com.example.mobile_intern_test.databinding.ItemAddressBinding

class LocationAdapter(
    private val locations: List<LocationDetail>,
    private val searchQuery: String
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(private val binding: ItemAddressBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(location: LocationDetail) {
            val highlightedDisplayName = highlightSearchKeywords(location.displayName, searchQuery)
            binding.tvTitle.text = highlightedDisplayName
            
            // Show importance score for better understanding of result relevance
//            binding.tvTitle.append(" (${String.format("%.2f", location.importance)})")
            
            binding.root.setOnClickListener {
                openInGoogleMaps(location.lat.toDoubleOrNull() ?: 0.0, location.lon.toDoubleOrNull() ?: 0.0)
            }
        }
        
        private fun highlightSearchKeywords(text: String, query: String): SpannableString {
            val spannableString = SpannableString(text)
            val queryLower = query.lowercase()
            val textLower = text.lowercase()
            
            // Split query into individual words for better highlighting
            val queryWords = query.split(" ").filter { it.isNotBlank() }
            
            queryWords.forEach { word ->
                var startIndex = 0
                while (true) {
                    val index = textLower.indexOf(word.lowercase(), startIndex)
                    if (index == -1) break
                    
                    spannableString.setSpan(
                        BackgroundColorSpan(ContextCompat.getColor(itemView.context, R.color.highlight_color)),
                        index,
                        index + word.length,
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    startIndex = index + 1
                }
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
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemAddressBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return LocationViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations[position])
    }
    
    override fun getItemCount(): Int = locations.size
}
