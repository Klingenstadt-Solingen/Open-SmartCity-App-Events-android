package de.osca.android.events.data.repository

import de.osca.android.essentials.domain.entity.elastic_search.ElasticSearchCountRequest
import de.osca.android.essentials.domain.entity.elastic_search.ElasticSearchRequest
import de.osca.android.essentials.utils.extensions.toApiString
import de.osca.android.events.data.EventsApiService
import de.osca.android.events.domain.boundary.EventRepository
import de.osca.android.events.domain.boundary.EventStorage
import de.osca.android.events.domain.entity.Event
import de.osca.android.networkservice.utils.RequestHandler
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class EventRepositoryImpl
    @Inject
    constructor(
        private val apiService: EventsApiService,
        private val requestHandler: RequestHandler,
        private val eventsApiService: EventsApiService,
        private val eventStorage: EventStorage,
    ) : EventRepository {
        override suspend fun getEvents(
            skip: Int,
            limit: Int,
            date: LocalDate?,
            onlyBookmarks: Boolean,
            searchText: String?,
        ): List<Event> {
            val bookmarkedEventsIds = getBookmarkedEventIds()

            if (getBookmarkedEventIds().isEmpty() && onlyBookmarks) {
                return emptyList()
            }

            val fromDate = date?.atStartOfDay()
            val endDate = date?.plusDays(1)?.atStartOfDay()

            val request =
                EventFilterRequest(
                    fromDate ?: LocalDate.now().atTime(0, 0),
                    endDate,
                    if (onlyBookmarks) bookmarkedEventsIds else emptyList(),
                )

            var events: List<Event> = emptyList()

            if (!searchText.isNullOrEmpty()) {
                try {
                    events =
                        searchEvents(
                            searchText,
                            limit,
                            skip,
                            startDate = fromDate,
                            endDate = endDate,
                            objectIds = if (onlyBookmarks) bookmarkedEventsIds else null,
                        )
                } catch (e: Exception) {
                    // println("EXCEPTION $e")
                }
            } else {
                events = requestHandler.makeRequest {
                    apiService.getEvents(
                        skip = skip,
                        limit = limit,
                        request = request.asFilter(),
                        order = "startDate",
                    )
                } ?: emptyList()
            }

            events.forEach { it.isBookmarked.value = bookmarkedEventsIds.contains(it.objectId) }
            return events
        }

        override suspend fun getEventById(objectId: String): Event? {
            return requestHandler.makeRequest {
                apiService.getEventById(objectId)
            }
        }

        override suspend fun getBookmarkedEvents(): List<Event> = getEvents(onlyBookmarks = true)

        override suspend fun addBookmark(event: Event): Boolean =
            event.objectId?.run {
                eventStorage.addBookmark(this)
            } ?: false

        override suspend fun removeBookmark(event: Event): Boolean =
            event.objectId?.run {
                eventStorage.removeBookmark(this)
            } ?: false

        override suspend fun isBookmarked(event: Event): Boolean {
            val bookmarkedIds = eventStorage.fetchBookmarkedEventIds()
            return !event.objectId.isNullOrBlank() && bookmarkedIds.contains(event.objectId)
        }

        override suspend fun searchEvents(
            query: String,
            size: Int?,
            from: Int,
            startDate: LocalDateTime?,
            endDate: LocalDateTime?,
            objectIds: List<String>?,
        ): List<Event> {
            val request =
                ElasticSearchRequest(
                    size = size,
                    from = from,
                    startDate = startDate?.toApiString(),
                    endDate = endDate?.toApiString(),
                    index = ES_EVENT_INDEX,
                    query = query,
                    objectIds = objectIds,
                    raw = false,
                )

            return requestHandler.makeRequest {
                eventsApiService.elasticSearch(request)
            } ?: emptyList()
        }

        private suspend fun getBookmarkedEventIds(): List<String> = eventStorage.fetchBookmarkedEventIds()

        override suspend fun getEventCount(
            date: LocalDate?,
            onlyBookmarks: Boolean,
            searchText: String,
        ): Int {
            val bookmarkedEventsIds = getBookmarkedEventIds()

            if (getBookmarkedEventIds().isEmpty() && onlyBookmarks) {
                return 0
            }

            val fromDate = date?.atStartOfDay()
            var endDate: LocalDateTime? = date?.plusDays(1)?.atStartOfDay()
            if (endDate == null && searchText.isBlank() && !onlyBookmarks) {
                endDate = LocalDate.now().plusDays(1)?.atStartOfDay()
            }

            val request =
                EventFilterRequest(
                    fromDate ?: LocalDate.now().atTime(0, 0),
                    endDate,
                    if (onlyBookmarks) bookmarkedEventsIds else emptyList(),
                )

            var eventCount: Int? = 0

            if (!searchText.isNullOrEmpty()) {
                try {
                    eventCount =
                        getElasticEventCount(
                            searchText,
                            startDate = fromDate,
                            endDate = endDate,
                            objectIds = if (onlyBookmarks) bookmarkedEventsIds else null,
                        )
                } catch (_: Exception) {
                }
            } else {
                eventCount =
                    requestHandler.makeRequest {
                        apiService.getEventCount(
                            request = request.asFilter(),
                            order = "startDate",
                        )
                    }?.count
            }

            return eventCount ?: 0
        }

        override suspend fun getElasticEventCount(
            query: String,
            startDate: LocalDateTime?,
            endDate: LocalDateTime?,
            objectIds: List<String>?,
        ): Int {
            val request =
                ElasticSearchCountRequest(
                    startDate = startDate?.toApiString(),
                    endDate = endDate?.toApiString(),
                    index = ES_EVENT_INDEX,
                    query = query,
                    objectIds = objectIds,
                    raw = false,
                )
            return requestHandler.makeRequest {
                eventsApiService.elasticSearchCount(request)
            }?.count ?: 0
        }

        companion object {
            const val ES_EVENT_INDEX = "events"
        }
    }
