package de.osca.android.events.presentation.events.state

import de.osca.android.events.domain.entity.Event
import java.lang.Exception

/**
 *
 */
sealed class EventsScreenState(val event: Event?, val error: Exception?) {
    object Loading : EventsScreenState(null, null)
    class ShowData(event: Event) : EventsScreenState(event, null)
    class Error(exception: Exception) : EventsScreenState(null, exception)
}