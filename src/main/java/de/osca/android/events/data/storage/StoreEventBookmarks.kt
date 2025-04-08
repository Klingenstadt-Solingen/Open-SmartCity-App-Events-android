package de.osca.android.events.data.storage

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import de.osca.android.events.BookmarkedEvents
import java.io.InputStream
import java.io.OutputStream

internal val Context.eventsDataStore: DataStore<BookmarkedEvents> by dataStore(
    fileName = "bookmarked_events.pb",
    serializer = BookmarkedEventsSerializer
)

private object BookmarkedEventsSerializer : Serializer<BookmarkedEvents> {
    override val defaultValue: BookmarkedEvents get() = BookmarkedEvents.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): BookmarkedEvents {
        try {
            return BookmarkedEvents.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    }

    override suspend fun writeTo(t: BookmarkedEvents, output: OutputStream) = t.writeTo(output)
}