package de.osca.android.events.widget

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import de.osca.android.essentials.presentation.component.design.BaseListContainer
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.utils.extensions.safeTake
import de.osca.android.events.navigation.EventsNavItems
import de.osca.android.events.presentation.components.EventsInfoCard
import de.osca.android.events.presentation.events.EventsViewModel

/**
 * The Widget for Events
 *
 * @param navController
 * @param initialLocation
 * @param eventsViewModel
 * @param masterDesignArgs
 */
@Composable
fun EventWidget(
    navController: NavController,
    initialLocation: LatLng,
    @DrawableRes iconUnderLine: Int = -1,
    underLineColor: Color = Color.White,
    eventsViewModel: EventsViewModel = hiltViewModel(),
    masterDesignArgs: MasterDesignArgs = eventsViewModel.defaultDesignArgs,
) {
    if (eventsViewModel.eventsDesignArgs.vIsWidgetVisible) {
        val design = eventsViewModel.eventsDesignArgs

        LaunchedEffect(Unit) {
            eventsViewModel.initializeEvents()
        }

        BaseListContainer(
            text = stringResource(id = design.vWidgetTitle),
            showMoreOption = design.vWidgetShowMoreOption,
            moduleDesignArgs = design,
            iconUnderLine = iconUnderLine,
            underLineColor = underLineColor,
            onMoreOptionClick = {
                navController.navigate(EventsNavItems.EventsNavItem.route)
            },
            masterDesignArgs = masterDesignArgs,
        ) {
            eventsViewModel.currentEvents
                .safeTake(eventsViewModel.eventsDesignArgs.previewCountForWidget)
                .forEach { event ->
                    EventsInfoCard(
                        event = event,
                        onClick = {
                            event.objectId?.let {
                                navController.navigate(EventsNavItems.getEventDetailRoute(it))
                            }
                        },
                        onBookmark = eventsViewModel::switchBookmark,
                        location = initialLocation,
                        masterDesignArgs = masterDesignArgs,
                        eventsViewModel = eventsViewModel,
                        eventDesignArgs = design,
                    )
                }
        }
    }
}
