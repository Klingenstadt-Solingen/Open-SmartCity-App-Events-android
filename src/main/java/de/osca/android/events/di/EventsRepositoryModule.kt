package de.osca.android.events.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.osca.android.events.data.repository.EventRepositoryImpl
import de.osca.android.events.domain.boundary.EventRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class EventsRepositoryModule {
    @Binds
    abstract fun provideEventRepository(repositoryImpl: EventRepositoryImpl): EventRepository
}