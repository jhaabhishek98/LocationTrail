package com.app.locationtrail.data.local
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationEntity)

    @Update
    suspend fun update(location: LocationEntity)

    @Delete
    suspend fun delete(location: LocationEntity)

    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE name = :name OR (latitude = :lat AND longitude = :lng) LIMIT 1")
    suspend fun getDuplicate(name: String, lat: Double, lng: Double): LocationEntity?
}