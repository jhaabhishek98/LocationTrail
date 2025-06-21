package com.app.locationtrail.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.locationtrail.databinding.FragmentLocationListBinding
import com.app.locationtrail.utils.SortType
import com.app.locationtrail.utils.SwipeToDeleteCallback
import com.app.locationtrail.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationListFragment : Fragment() {

    private var _binding: FragmentLocationListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationViewModel by viewModels()
    private lateinit var adapter: LocationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize adapter with delete and edit logic
        adapter = LocationListAdapter(
            onItemClick = {
                // EDIT: Navigate to AddLocationFragment with existing location for edit
                val action = LocationListFragmentDirections
                    .actionLocationListFragmentToAddLocationFragment(it)
                findNavController().navigate(action)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Swipe to delete with 50% threshold
        val swipeCallback = SwipeToDeleteCallback { location ->
            viewModel.deleteLocation(location)
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.recyclerView)

        binding.btnAdd.setOnClickListener {
            // Navigate without any argument (add mode)
            val action = LocationListFragmentDirections
                .actionLocationListFragmentToAddLocationFragment(null)
            findNavController().navigate(action)
        }

        binding.btnSort.setOnClickListener {
            showSortDialog()
        }

        binding.btnNavigate.setOnClickListener {
            val action = LocationListFragmentDirections.actionLocationListFragmentToMapFragment()
            findNavController().navigate(action)
        }

        // Observe and submit list of locations
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.locations.collect {
                    adapter.submitList(it)
                }
            }
        }
    }
    private fun showSortDialog() {
        val options = arrayOf("Ascending", "Descending", "Default")
        AlertDialog.Builder(requireContext())
            .setTitle("Sort By Distance")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewModel.sortLocationsByDistance(SortType.ASCENDING)
                    1 -> viewModel.sortLocationsByDistance(SortType.DESCENDING)
                    2 -> viewModel.sortLocationsByDistance(SortType.DEFAULT)
                }
            }
            .show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
