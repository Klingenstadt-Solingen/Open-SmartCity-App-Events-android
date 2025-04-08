package de.osca.android.events.data.repository

import de.osca.android.essentials.utils.extensions.toApiString
import java.time.LocalDateTime

class EventFilterRequest(
    val fromDate: LocalDateTime,
    val toDate: LocalDateTime?,
    val objectIds: List<String> = emptyList(),
) {
    companion object {
        private val gte = "\$gte"
        private val lte = "\$lte"
        private val or = "\$or"
        private val inQ = "\$in"
        private val and = "\$and"
        private val exists = "\$exists"
    }

    fun asFilter(): String {
        val qStartGtFrom = "{\"startDate\":${filter(gte, date(fromDate))}}"
        val qEndNotExists = "{\"endDate\":${filter(exists, "false")}}"
        val qStartLtEnd = "{\"startDate\":${filter(lte, date(toDate))}}"

        val qEndGtStart = "{\"endDate\":${filter(gte, date(fromDate))}}"

        val closedQuery = filter(and, listOf(qStartGtFrom, qStartLtEnd, qEndNotExists).toString())
        val openQuery = filter(and, listOf(qStartLtEnd, qEndGtStart).toString())

        val dateQuery = "{\"$or\":[$closedQuery, $openQuery]}"

        val objectIdsQuery = "{\"objectId\":{\"$inQ\":${list(objectIds)}}}"

        val filterIds = objectIds.isNotEmpty()

        val listFilter = mutableListOf<String>()

        if (toDate != null) {
            listFilter.add(dateQuery)
        }
        if (filterIds) {
            listFilter.add(objectIdsQuery)
        }

        if (listFilter.isEmpty()) {
            return qStartGtFrom
        }

        return filter(and, listFilter.toString())
    }

    private fun date(date: LocalDateTime?): String =
        "{${value("__type")}:${value("Date")},${value("iso")}:${value(date?.toApiString())}}"

    private fun list(list: List<String>): String = list.map { "\"$it\"" }.toString()

    private fun value(value: String?): String = "\"$value\""

    private fun filter(
        filter: String,
        field: String,
    ): String = "{\"$filter\":$field}"
}
