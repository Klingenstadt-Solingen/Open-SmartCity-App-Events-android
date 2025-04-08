package de.osca.android.events.presentation.args

import androidx.compose.ui.unit.Dp
import de.osca.android.essentials.presentation.component.design.ModuleDesignArgs
import de.osca.android.essentials.presentation.component.design.WidgetDesignArgs

/**
 * This is the Event Interface for Event
 * It contains design arguments explicit for this module but also
 * generic module arguments which are by default null and can be set to override
 * the masterDesignArgs
 *
 *  @property topBarElevation
 *  @property cityName
 *  @property previewCountForWidget
 *  @property showEventImage
 *  @property showSearchBar
 *  @property showCalendarPicker
 *  @property isBookmarkingEnabled
 *  @property showGoToMyBookmarksAction
 *  @property showNavigationIcon
 */
interface EventsDesignArgs : ModuleDesignArgs, WidgetDesignArgs {
    val topBarElevation: Dp?
    val cityName: Int
    val appStoreLink: String
    val previewCountForWidget: Int
    val showEventImage: Boolean
    val showSearchBar: Boolean
    val showCalendarPicker: Boolean
    val isBookmarkingEnabled: Boolean
    val showGoToMyBookmarksAction: Boolean
    val showNavigationIcon: Boolean
    val placeholderLogo: Int
    val placeholderDetailLogo: Int
    val isLogoRotated: Boolean
    val mapStyle: Int?
}
