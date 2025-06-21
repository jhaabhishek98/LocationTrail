package com.app.locationtrail.utils

import com.app.locationtrail.data.local.LocationEntity
import kotlin.math.*

object DistanceUtils {
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    fun sortByDistanceFrom(
        reference: LocationEntity,
        locations: List<LocationEntity>,
        ascending: Boolean = true
    ): List<LocationEntity> {
        return locations.sortedBy {
            calculateDistanceKm(reference.latitude, reference.longitude, it.latitude, it.longitude)
        }.let { if (!ascending) it.reversed() else it }
    }
}
