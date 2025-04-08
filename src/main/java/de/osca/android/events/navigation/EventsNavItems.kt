package de.osca.android.events.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import de.osca.android.essentials.domain.entity.navigation.NavigationItem
import de.osca.android.events.R
import de.osca.android.events.navigation.EventsNavItems.EventsNavItem.icon
import de.osca.android.events.navigation.EventsNavItems.EventsNavItem.route
import de.osca.android.events.navigation.EventsNavItems.EventsNavItem.title

/**
 * Navigation Routes for Events
 */
sealed class EventsNavItems {
    /**
     * Route for the default/main screen
     * @property title title of the route (a name to display)
     * @property route route for this navItem (name is irrelevant)
     * @property icon the icon to display
     */
    object EventsNavItem : NavigationItem(
        title = R.string.events_title,
        route = "events",
        icon = R.drawable.ic_circle,
        deepLinks = listOf(navDeepLink { uriPattern = "solingen://events" }),
    )

    object EventDetailsNavItem : NavigationItem(
        title = R.string.events_title,
        route = "event/detail?${ARG_EVENT}={${ARG_EVENT}}",
        icon = R.drawable.ic_circle,
        arguments =
            listOf(
                navArgument(ARG_EVENT) {
                    type = NavType.StringType
                    nullable = false
                },
            ),
        deepLinks =
            listOf(
                navDeepLink {
                    uriPattern = "solingen://events/detail?${ARG_EVENT}={${ARG_EVENT}}"
                },
            ),
    )

    companion object {
        const val ARG_EVENT = "object"

        fun getEventDetailRoute(objectId: String): String {
            val routeBuilder = StringBuilder()
            routeBuilder.append("event/detail")
            routeBuilder.append("?$ARG_EVENT=$objectId")
            return routeBuilder.toString()
        }
    }
}
