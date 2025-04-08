package de.osca.android.events.domain.entity

import androidx.compose.runtime.mutableStateOf
import com.google.gson.annotations.SerializedName
import de.osca.android.essentials.domain.entity.DateEnvelope
import de.osca.android.essentials.domain.entity.Location
import de.osca.android.essentials.utils.extensions.toLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * The Json-Response-Data-Structure
 */
data class Event(
    @SerializedName("objectId")
    val objectId: String? = null,
    @SerializedName("startDate", alternate = ["startDateTime"])
    val rawStartDateTime: DateEnvelope? = null,
    @SerializedName("endDate", alternate = ["endDateTime"])
    val rawEndDateTime: DateEnvelope? = null,
    @SerializedName("location")
    val location: Location? = null,
    @SerializedName("isAllDay")
    val isAllDay: Boolean = false,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("description", alternate = ["info"])
    val info: String? = null,
    @SerializedName("url", alternate = ["link"])
    val link: String? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("subcategory")
    val subcategory: String? = null,
    @SerializedName("eventStatus")
    val eventStatus: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("sourceUrl")
    var sourceUrl: String? = null,
    @SerializedName("sourceId")
    var sourceId: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("thumbImage")
    val thumbImage: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("offers")
    val offers: List<Offer> = emptyList(),
    @SerializedName("canceled")
    val canceled: String? = null,
    @SerializedName("eventCancelMsg")
    val cancellationMessage: String? = null,
) {
    var isBookmarked = mutableStateOf(false)

    val parsedStartDateTime: LocalDateTime? get() = rawStartDateTime?.value?.toLocalDateTime()
    val parsedEndDateTime: LocalDateTime? get() = rawEndDateTime?.value?.toLocalDateTime()

    fun isToday(): Boolean = parsedStartDateTime?.toLocalDate()?.equals(LocalDate.now()) ?: false
}

data class Offer(
    @SerializedName("price")
    val price: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("priceCurrency")
    val priceCurrency: String? = null,
)
