package de.osca.android.events.presentation.events

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.lifecycle.HiltViewModel
import de.osca.android.essentials.presentation.base.BaseViewModel
import de.osca.android.essentials.presentation.component.screen_wrapper.ScreenWrapperState
import de.osca.android.essentials.utils.extensions.displayContent
import de.osca.android.essentials.utils.extensions.loading
import de.osca.android.events.R
import de.osca.android.events.data.paging.GenericPagingSource
import de.osca.android.events.domain.boundary.EventRepository
import de.osca.android.events.domain.entity.Event
import de.osca.android.events.presentation.args.EventsDesignArgs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for Events
 * @param eventsDesignArgs the design arguments of the module
 * @param repository the api-endpoints for Parse (with implementation)
 *
 * @property allEvents
 * @property displayedEvents
 * @property searchJob
 */
@HiltViewModel
class EventsViewModel
    @Inject
    constructor(
        val eventsDesignArgs: EventsDesignArgs,
        private val repository: EventRepository,
    ) : BaseViewModel() {
        private var _searchText = MutableStateFlow("")
        val searchText: StateFlow<String> get() = _searchText

        private var _selectedDate = MutableStateFlow<LocalDate?>(null)
        val selectedDate: StateFlow<LocalDate?> get() = _selectedDate

        private var _showBookmarkOnly = MutableStateFlow(false)
        val showBookmarkOnly: StateFlow<Boolean> get() = _showBookmarkOnly
        var eventCount by mutableStateOf<Int?>(null)

        var events: Flow<PagingData<Event>> = emptyFlow()
            private set

        val currentEvents: List<Event>
            @Composable
            get() = events.collectAsLazyPagingItems().itemSnapshotList.items

        private var currentPagingSource: GenericPagingSource<Event>? = null
        var detailWrapperState: MutableState<ScreenWrapperState> =
            mutableStateOf(ScreenWrapperState.WaitingInitialization)
        var detailEvent by mutableStateOf<Event?>(null)

        init {
            initializeEvents()
        }

        /**
         * call this function to initialize all events.
         * it sets the screen to loading, fetches the data from parse and when
         * it finished successful then displays the content and when an error
         * occurred it displays a message screen
         */
        fun initializeEvents() {
            viewModelScope.launch {
                wrapperState.loading()

                @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
                events =
                    combine(
                        searchText.debounce(300),
                        selectedDate,
                        showBookmarkOnly,
                    ) { search, date, onlyBookmarks ->
                        eventCount = null
                        getEventCount(search, date, onlyBookmarks)
                        Pager(
                            config = PagingConfig(pageSize = 20, initialLoadSize = 20),
                            pagingSourceFactory = {
                                currentPagingSource =
                                    GenericPagingSource(
                                        onLoad = { skip, limit ->
                                            repository.getEvents(
                                                skip = skip,
                                                limit = limit,
                                                date,
                                                onlyBookmarks,
                                                search,
                                            )
                                        },
                                    )
                                currentPagingSource!!
                            },
                        ).flow
                    }.flatMapLatest { it }.cachedIn(viewModelScope)

                wrapperState.displayContent()
            }
        }

        fun displayContent() {
            wrapperState.displayContent()
        }

        /**
         * toggle bookmark of this event
         */
        fun switchBookmark(event: Event): Job =
            launchDataLoad {
                when (event.isBookmarked.value) {
                    true -> repository.removeBookmark(event)
                    false -> repository.addBookmark(event)
                }
                event.isBookmarked.value = event.isBookmarked.value.not()
            }

        /**
         * search events by a given text.
         * @property searchText displays only events with a matching text
         */
        fun setSearch(searchText: String) {
            _searchText.value = searchText
        }

        /**
         * @property searchedDate all events with this date gets displayed
         */
        fun setDate(searchedDate: LocalDate?) {
            _selectedDate.value = searchedDate
        }

        /**
         * shows only the bookmarked events.
         */
        fun showBookmarked(show: Boolean) {
            _showBookmarkOnly.value = show
        }

        /**
         * Initialize detail view
         */
        fun initDetailView(objectId: String) {
            detailWrapperState.loading()
            viewModelScope.launch {
                fetchEventById(objectId)
            }
        }

        /**
         * Loads event by id
         * */
        private fun fetchEventById(objectId: String): Job =
            launchDataLoad {
                detailEvent = repository.getEventById(objectId)
                detailWrapperState.displayContent()
            }

        fun getEventCount(
            searchText: String,
            selectedDate: LocalDate?,
            showBookmarkOnly: Boolean,
        ): Job =
            launchDataLoad {
                eventCount = repository.getEventCount(selectedDate, showBookmarkOnly, searchText)
            }

        fun entriesFoundText(
            searchText: String,
            selectedDate: LocalDate?,
            showBookmarkOnly: Boolean,
            resources: Resources,
        ): String {
            var entriesFound: String? = null
            eventCount?.let {
                entriesFound =
                    if ((selectedDate == null || selectedDate == LocalDate.now()) && searchText.isBlank() && !showBookmarkOnly) {
                        if (it > 0) {
                            resources.getQuantityString(
                                R.plurals.events_found_today,
                                it,
                                eventCount,
                            )
                        } else {
                            resources.getString(R.string.events_not_found_today)
                        }
                    } else {
                        if (it > 0) {
                            resources.getQuantityString(R.plurals.events_found, it, eventCount)
                        } else {
                            resources.getString(R.string.events_not_found)
                        }
                    }
            }
            return entriesFound ?: ""
        }
    }
