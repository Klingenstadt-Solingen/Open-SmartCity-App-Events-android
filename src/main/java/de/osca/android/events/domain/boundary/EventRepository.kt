package de.osca.android.events.domain.boundary

import de.osca.android.events.domain.entity.Event
import java.time.LocalDate
import java.time.LocalDateTime

interface EventRepository {
    suspend fun getEvents(
        skip: Int = 0,
        limit: Int = 1000,
        date: LocalDate? = null,
        onlyBookmarks: Boolean = false,
        searchText: String? = null,
    ): List<Event>

    suspend fun getEventById(objectId: String): Event?

    suspend fun getBookmarkedEvents(): List<Event>

    suspend fun addBookmark(event: Event): Boolean

    suspend fun removeBookmark(event: Event): Boolean

    suspend fun isBookmarked(event: Event): Boolean

    suspend fun searchEvents(
        query: String,
        size: Int?,
        from: Int = 0,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        objectIds: List<String>? = null,
    ): List<Event?>

    suspend fun getElasticEventCount(
        query: String,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        objectIds: List<String>?,
    ): Int

    suspend fun getEventCount(
        date: LocalDate?,
        onlyBookmarks: Boolean,
        searchText: String,
    ): Int
}
