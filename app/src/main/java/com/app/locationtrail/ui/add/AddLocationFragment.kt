package com.app.locationtrail.ui.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.app.locationtrail.data.local.LocationEntity
import com.app.locationtrail.databinding.FragmentAddLocationBinding
import com.app.locationtrail.viewmodel.LocationViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddLocationFragment : Fragment() {

    private var _binding: FragmentAddLocationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationViewModel by viewModels()

    private var selectedPlace: Place? = null
    private var existingLocation: LocationEntity? = null

    //  Register ActivityResultLauncher
    private val autocompleteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedPlace = Autocomplete.getPlaceFromIntent(result.data!!)
            binding.etPlace.setText(selectedPlace?.name)
        } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
            Toast.makeText(requireContext(), "Error selecting place", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  Get passed arguments
        existingLocation = arguments?.let {
            AddLocationFragmentArgs.fromBundle(it).locationEntity
        }

        //  Pre-fill if editing
        existingLocation?.let {
            binding.etPlace.setText(it.name)
            selectedPlace = Place.builder()
                .setName(it.name)
                .setLatLng(LatLng(it.latitude, it.longitude))
                .build()
        }

        //  Place Picker
        binding.etPlace.setOnClickListener {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(requireContext())
            autocompleteLauncher.launch(intent)
        }

        //  Save or Update
        binding.btnSave.setOnClickListener {
            val place = selectedPlace
            if (place != null && place.latLng != null) {
                val location = LocationEntity(
                    id = existingLocation?.id ?: 0,
                    name = place.name ?: "Unnamed",
                    latitude = place.latLng!!.latitude,
                    longitude = place.latLng!!.longitude,
                    createdAt = existingLocation?.createdAt ?: System.currentTimeMillis()
                )

                if (existingLocation == null) {
                    viewModel.addLocation(location)
                } else {
                    viewModel.updateLocation(location)
                }

            } else {
                Toast.makeText(requireContext(), "Please select a valid place", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isDuplicate.collect { result ->
                    result?.let { isDuplicate ->
                        if (isDuplicate) {
                            Toast.makeText(requireContext(), "Location already exists!", Toast.LENGTH_SHORT).show()
                        } else {
                            findNavController().navigateUp()
                        }

                        // Reset state
                        viewModel.resetDuplicateFlag()
                    }
                }
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
