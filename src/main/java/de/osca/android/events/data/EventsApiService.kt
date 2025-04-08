package de.osca.android.events.data

import de.osca.android.essentials.domain.entity.ParseCountResponse
import de.osca.android.essentials.domain.entity.elastic_search.ElasticSearchCountRequest
import de.osca.android.essentials.domain.entity.elastic_search.ElasticSearchCountResponse
import de.osca.android.essentials.domain.entity.elastic_search.ElasticSearchRequest
import de.osca.android.essentials.utils.annotations.UnwrappedResponse
import de.osca.android.events.domain.entity.Event
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventsApiService {
    /**
     * Getting all Events
     * Endpoint: "classes/Event"
     * Limit: to get more than 100 default Elements
     */
    @GET("classes/Event")
    suspend fun getEvents(
        @Query("limit") limit: Int = 1000,
        @Query("skip") skip: Int = 0,
        @Query("where") request: String? = null,
        @Query("order") order: String? = null,
    ): Response<List<Event>>

    @GET("classes/Event/{objectId}")
    @UnwrappedResponse
    suspend fun getEventById(
        @Path("objectId") objectId: String,
    ): Response<Event>

    @POST("functions/elastic-search")
    suspend fun elasticSearch(
        @Body elasticSearchRequest: ElasticSearchRequest,
    ): Response<List<Event>>

    @POST("functions/elastic-search-count")
    suspend fun elasticSearchCount(
        @Body elasticSearchCountRequest: ElasticSearchCountRequest,
    ): Response<ElasticSearchCountResponse>

    @UnwrappedResponse
    @GET("classes/Event")
    suspend fun getEventCount(
        @Query("limit") limit: Int = 0,
        @Query("count") skip: Int = 1,
        @Query("where") request: String? = null,
        @Query("order") order: String? = null,
    ): Response<ParseCountResponse>
}
