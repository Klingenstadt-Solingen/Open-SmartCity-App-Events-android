package de.osca.android.events.domain.boundary

interface EventStorage {
    suspend fun fetchBookmarkedEventIds(): List<String>
    suspend fun addBookmark(eventId: String): Boolean
    suspend fun removeBookmark(eventId: String): Boolean
}