package com.app.locationtrail.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.locationtrail.data.local.LocationEntity
import com.app.locationtrail.databinding.ItemLocationBinding

class LocationListAdapter(
    private val onItemClick: (LocationEntity) -> Unit
) : ListAdapter<LocationEntity, LocationListAdapter.LocationViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location)
    }

    inner class LocationViewHolder(private val binding: ItemLocationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(location: LocationEntity) {
            binding.location = location
            binding.executePendingBindings()
            binding.root.setOnClickListener { onItemClick(location) }
        }

            fun getItem(): LocationEntity = getItem(getAdapterPosition())

    }

    class DiffCallback : DiffUtil.ItemCallback<LocationEntity>() {
        override fun areItemsTheSame(oldItem: LocationEntity, newItem: LocationEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: LocationEntity, newItem: LocationEntity) = oldItem == newItem
    }
}