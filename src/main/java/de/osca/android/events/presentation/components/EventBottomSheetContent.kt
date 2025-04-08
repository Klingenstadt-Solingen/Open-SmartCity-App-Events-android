package de.osca.android.events.presentation.components

/*
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.osca.android.essentials.presentation.component.bottom_sheet.BottomSheetAnchor
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.events.R
import de.osca.android.events.domain.entity.Event
import de.osca.android.events.presentation.event_details.EventDetails
import de.osca.android.events.presentation.events.EventsViewModel
import de.osca.android.events.presentation.events.state.EventsScreenState

@ExperimentalMaterialApi
@Composable
fun EventDetails(
    eventsViewModel: EventsViewModel,
    masterDesignArgs: MasterDesignArgs,
    screenState: MutableState<EventsScreenState>,
    @StringRes cityName: Int = -1,
    onBookmark: ((Event) -> Unit)? = null
) {
    val design = eventsViewModel.eventsDesignArgs

    val showErrorDialog = remember { mutableStateOf(false) }
    val isBookmarkedState = remember { mutableStateOf(false) }

    when (screenState.value) {
        is EventsScreenState.ShowData -> {
            isBookmarkedState.value = screenState.value.event?.isBookmarked ?: false
            EventDetails(
                screenState = screenState,
                onBookmark = onBookmark,
                isBookmarkedState = isBookmarkedState,
                masterDesignArgs = masterDesignArgs,
                eventsDesignArgs = design,
                eventsViewModel = eventsViewModel
            )
        }
        is EventsScreenState.Error -> {
            showErrorDialog.value = true
            EventDetailsError(showErrorDialog, masterDesignArgs)
        }
        is EventsScreenState.Loading -> {
            // Nothing to do, the content will expand once it's loaded
        }
    }
    BottomSheetAnchor()
}

@Composable
private fun EventDetailsError(
    openDialog: MutableState<Boolean>,
    masterDesignArgs: MasterDesignArgs
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = {
                Text(
                    text = stringResource(id = R.string.global_error_dialog_title),
                    style = masterDesignArgs.normalTextStyle,
                    color = masterDesignArgs.mCardTextColor
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.events_error_details),
                    style = masterDesignArgs.normalTextStyle,
                    color = masterDesignArgs.mCardTextColor
                )
            },
            confirmButton = {
                Button(onClick = {
                    openDialog.value = false
                }) {
                    Text(
                        text = stringResource(id = R.string.global_ok),
                        style = masterDesignArgs.normalTextStyle,
                        color = masterDesignArgs.mCardTextColor
                    )
                }
            }
        )
    }
}

@Composable
fun IconColumn(
    @DrawableRes icon: Int,
    iconTint: Color,
    title: String,
    titleColor: Color,
    modifier: Modifier = Modifier.size(28.dp),
    masterDesignArgs: MasterDesignArgs,
    onClick: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable {
                onClick?.invoke()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = iconTint,
            modifier = modifier
                .padding(bottom = 4.dp)
        )

        Text(
            text = title,
            style = masterDesignArgs.normalTextStyle,
            color = titleColor
        )
    }
}
*/