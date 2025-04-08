package de.osca.android.events.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.topbar.ScreenTopBar
import de.osca.android.essentials.presentation.component.topbar.defaultNavigationIcon
import de.osca.android.events.R
import de.osca.android.events.presentation.events.EventsViewModel

@Composable
fun EventsTopBar(
    viewModel: EventsViewModel,
    navController: NavController,
    masterDesignArgs: MasterDesignArgs = viewModel.defaultDesignArgs,
) {
    val design = viewModel.eventsDesignArgs
    val mNavigationIcon =
        when (viewModel.eventsDesignArgs.showNavigationIcon) {
            true -> defaultNavigationIcon(navController)
            false -> null
        }

    ScreenTopBar(
        title = stringResource(id = design.vModuleTitle),
        navController = navController,
        overrideBackgroundColor = design.mTopBarBackColor,
        overrideTextColor = design.mTopBarTextColor,
        modifier =
            Modifier
                .padding(bottom = 0.dp),
        navigationIcon = mNavigationIcon,
        actions = {
            val filterBookmarked = viewModel.showBookmarkOnly.collectAsState()

            Row(modifier = Modifier.padding(end = 16.dp)) {
                val bookmarkIcon =
                    when (filterBookmarked.value) {
                        true -> R.drawable.ic_heart_solid
                        false -> R.drawable.ic_heart_light
                    }

                if (viewModel.eventsDesignArgs.isBookmarkingEnabled && viewModel.eventsDesignArgs.showGoToMyBookmarksAction) {
                    IconButton(
                        modifier =
                            Modifier
                                .requiredWidth(22.dp)
                                .requiredHeight(22.dp),
                        onClick = {
                            viewModel.showBookmarked(!filterBookmarked.value)
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = bookmarkIcon),
                            contentDescription = stringResource(id = R.string.events_show_bookmark_description),
                            tint = design.mTopBarTextColor ?: masterDesignArgs.mTopBarTextColor,
                        )
                    }
                }
            }
        },
        masterDesignArgs = masterDesignArgs,
    )
}
