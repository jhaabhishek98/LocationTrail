package com.app.locationtrail.di

import android.content.Context
import androidx.room.Room
import com.app.locationtrail.data.local.AppDatabase
import com.app.locationtrail.data.local.LocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "location_db"
            ).fallbackToDestructiveMigration(false).build()

    @Provides
    fun provideLocationDao(db: AppDatabase): LocationDao = db.locationDao()
}
