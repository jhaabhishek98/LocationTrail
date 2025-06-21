package com.app.locationtrail.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.locationtrail.R
import com.app.locationtrail.data.local.LocationEntity
import com.app.locationtrail.databinding.FragmentMapBinding
import com.app.locationtrail.utils.DistanceUtils
import com.app.locationtrail.viewmodel.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.locations.collect { locations ->
                    googleMap?.clear()
                    if (locations.isNotEmpty()) {
                        addMarkersAndPolyline(locations)
                    }
                }
            }
        }
    }

    private fun addMarkersAndPolyline(locations: List<LocationEntity>) {
        val sorted = DistanceUtils.sortByDistanceFrom(locations.first(), locations.drop(1))
        val fullList = listOf(locations.first()) + sorted
        val polylinePoints = mutableListOf<LatLng>()

        fullList.forEach { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            polylinePoints.add(latLng)
            googleMap?.addMarker(
                MarkerOptions().position(latLng).title(location.name)
            )
        }

        if (polylinePoints.size >= 2) {
            googleMap?.addPolyline(
                PolylineOptions().addAll(polylinePoints).color(0xFF2196F3.toInt()).width(5f)
            )
        }

        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(polylinePoints.first(), 10f)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
