package com.app.locationtrail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.locationtrail.data.local.LocationEntity
import com.app.locationtrail.data.repository.LocationRepository
import com.app.locationtrail.utils.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {

    private val _locations = MutableStateFlow<List<LocationEntity>>(emptyList())
    val locations: StateFlow<List<LocationEntity>> = _locations.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.DEFAULT)

    private val _isDuplicate = MutableStateFlow<Boolean?>(null)
    val isDuplicate: StateFlow<Boolean?> = _isDuplicate

    init {
        fetchLocations()
    }

    private fun fetchLocations() {
        viewModelScope.launch {
            repository.getAllLocations().collect { list ->
                _locations.value = sortLocations(list, _sortType.value)
            }
        }
    }

    fun sortLocationsByDistance(type: SortType) {
        _sortType.value = type
        viewModelScope.launch {
            val all = repository.getAllLocations().first()
            _locations.value = sortLocations(all, type)
        }
    }

    private fun sortLocations(
        list: List<LocationEntity>,
        type: SortType
    ): List<LocationEntity> {
        if (list.isEmpty()) return list

        // Use the first location as reference point
        val reference = list.first()

        val sorted = list.sortedBy {
            val dx = it.latitude - reference.latitude
            val dy = it.longitude - reference.longitude
            sqrt(dx.pow(2) + dy.pow(2))
        }

        return when (type) {
            SortType.ASCENDING -> sorted
            SortType.DESCENDING -> sorted.reversed()
            else -> list
        }
    }


    fun addLocation(location: LocationEntity) = viewModelScope.launch {
        val duplicate = repository.getDuplicate(location.name, location.latitude, location.longitude)
        if (duplicate == null) {
            repository.insert(location)
            _isDuplicate.value = false
        } else {
            _isDuplicate.value = true
        }
    }

    fun updateLocation(location: LocationEntity) = viewModelScope.launch {
        val current = repository.getAllLocations().first()
        val isDuplicate = current.any {
            it.id != location.id &&  //  Important: exclude current item
                    it.name == location.name &&
                    it.latitude == location.latitude &&
                    it.longitude == location.longitude
        }
        if (isDuplicate) {
            _isDuplicate.value = true
        } else {
            _isDuplicate.value = false
            repository.update(location)
        }
    }

    fun deleteLocation(location: LocationEntity) = viewModelScope.launch {
        repository.delete(location)
    }
    fun resetDuplicateFlag() {
        _isDuplicate.value = null
    }
}
