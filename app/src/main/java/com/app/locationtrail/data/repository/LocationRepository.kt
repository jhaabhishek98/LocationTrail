package com.app.locationtrail.data.repository

import com.app.locationtrail.data.local.LocationDao
import com.app.locationtrail.data.local.LocationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class LocationRepository @Inject constructor(private val dao: LocationDao) {
    fun getAllLocations(): Flow<List<LocationEntity>> = dao.getAllLocations()

    suspend fun insert(location: LocationEntity) = dao.insert(location)
    suspend fun update(location: LocationEntity) = dao.update(location)
    suspend fun delete(location: LocationEntity) = dao.delete(location)
    suspend fun getDuplicate(name: String, lat: Double, lng: Double): LocationEntity? {
        return dao.getDuplicate(name, lat, lng)
    }
}