package de.osca.android.events.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.osca.android.essentials.data.client.OSCAHttpClient
import de.osca.android.events.data.EventsApiService
import de.osca.android.events.data.storage.EventStorageImpl
import de.osca.android.events.data.storage.eventsDataStore
import de.osca.android.events.domain.boundary.EventStorage
import javax.inject.Singleton

/**
 * The dependency injection
 */
@Module
@InstallIn(SingletonComponent::class)
class EventsModule {

    @Singleton
    @Provides
    fun eventsApiService(oscaHttpClient: OSCAHttpClient): EventsApiService =
        oscaHttpClient.create(EventsApiService::class.java)

    @Provides
    @Singleton
    fun provideEventStorage(@ApplicationContext appContext: Context): EventStorage {
        return EventStorageImpl(appContext.eventsDataStore)
    }
}
