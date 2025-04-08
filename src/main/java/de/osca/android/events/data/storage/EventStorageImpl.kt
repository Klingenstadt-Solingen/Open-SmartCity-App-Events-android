package de.osca.android.events.data.storage

import androidx.datastore.core.DataStore
import de.osca.android.events.BookmarkedEvents
import de.osca.android.events.domain.boundary.EventStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class EventStorageImpl(
    private val dataStore: DataStore<BookmarkedEvents>
) : EventStorage {
    override suspend fun fetchBookmarkedEventIds(): List<String> = withContext(Dispatchers.IO) {
        try {
            dataStore.data.first().objectIdList.distinct()
        } catch (exception: Exception) {
            emptyList()
        }
    }

    override suspend fun addBookmark(eventId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            dataStore.updateData { bookmarkedEvents ->
                bookmarkedEvents.toBuilder()
                    .addObjectId(eventId)
                    .build()
            }
            true
        } catch (exception: Exception) {
            false
        }
    }

    override suspend fun removeBookmark(eventId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val updatedList = fetchBookmarkedEventIds().distinct().toMutableList()
            updatedList.remove(eventId)

            dataStore.updateData { bookmarkedEvents ->
                bookmarkedEvents.toBuilder()
                    .clearObjectId()
                    .addAllObjectId(updatedList)
                    .build()
            }
            true
        } catch (exception: Exception) {
            false
        }
    }
}