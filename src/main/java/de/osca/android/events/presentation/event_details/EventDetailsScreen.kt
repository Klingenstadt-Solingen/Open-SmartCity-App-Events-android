package de.osca.android.events.presentation.event_details

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import de.osca.android.essentials.domain.entity.Coordinates
import de.osca.android.essentials.presentation.component.calendar.AddEventToSystemCalendar
import de.osca.android.essentials.presentation.component.design.MainButton
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.OpenWebsiteElement
import de.osca.android.essentials.presentation.component.screen_wrapper.ScreenWrapper
import de.osca.android.essentials.presentation.component.text.LinkfiedText
import de.osca.android.essentials.presentation.component.topbar.ScreenTopBar
import de.osca.android.essentials.utils.extensions.SetSystemStatusBar
import de.osca.android.essentials.utils.extensions.fromHtml
import de.osca.android.essentials.utils.extensions.openMapRouteTo
import de.osca.android.essentials.utils.extensions.shareText
import de.osca.android.essentials.utils.extensions.toDateString
import de.osca.android.essentials.utils.extensions.toDateTimeString
import de.osca.android.essentials.utils.extensions.toTimeString
import de.osca.android.events.R
import de.osca.android.events.domain.entity.Event
import de.osca.android.events.presentation.args.EventsDesignArgs
import de.osca.android.events.presentation.events.EventsViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventDetailsScreen(
    navController: NavController,
    objectId: String,
    eventsViewModel: EventsViewModel = hiltViewModel(),
    masterDesignArgs: MasterDesignArgs = eventsViewModel.defaultDesignArgs,
    eventsDesignArgs: EventsDesignArgs = eventsViewModel.eventsDesignArgs,
) {
    LaunchedEffect(objectId) {
        eventsViewModel.initDetailView(objectId)
    }
    val event = eventsViewModel.detailEvent

    if (event != null) {
        eventsViewModel.displayContent()

        val context = LocalContext.current
        val shareEvent = remember { mutableStateOf(false) }

        SetSystemStatusBar(
            !(eventsDesignArgs.mIsStatusBarWhite ?: masterDesignArgs.mIsStatusBarWhite),
            Color.Transparent,
        )

        ScreenWrapper(
            topBar = {
                ScreenTopBar(
                    title = stringResource(id = R.string.events_title),
                    navController = navController,
                    overrideBackgroundColor = eventsDesignArgs.mTopBarBackColor,
                    overrideTextColor = eventsDesignArgs.mTopBarTextColor,
                    masterDesignArgs = masterDesignArgs,
                )
            },
            screenWrapperState = eventsViewModel.wrapperState,
            masterDesignArgs = masterDesignArgs,
            moduleDesignArgs = eventsDesignArgs,
        ) {
            LazyColumn(
                state = LazyListState(),
                verticalArrangement =
                    Arrangement.spacedBy(
                        eventsDesignArgs.mRootCardSpacing ?: masterDesignArgs.spaceList,
                    ),
            ) {
                item {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                    ) {
                        if (shareEvent.value) {
                            shareEvent(
                                context = context,
                                title = event.title ?: event.name ?: "",
                                summary = event.info ?: "",
                                appStoreLink = eventsDesignArgs.appStoreLink,
                                url = event.link ?: "",
                                cityName = eventsDesignArgs.cityName,
                                shareEvent = shareEvent,
                            )
                        }

                        Column {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(
                                            if (
                                                eventsViewModel.eventsDesignArgs.showEventImage &&
                                                (event.image != null || eventsDesignArgs.placeholderDetailLogo >= 0)
                                            ) {
                                                400.dp
                                            } else {
                                                230.dp
                                            },
                                        ),
                            ) {
                                if (eventsViewModel.eventsDesignArgs.showEventImage && event.image != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(event.image),
                                        contentDescription = "",
                                        contentScale = ContentScale.Crop,
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .height(300.dp),
                                    )
                                } else if (eventsViewModel.eventsDesignArgs.showEventImage && eventsDesignArgs.placeholderDetailLogo >= 0) {
                                    Image(
                                        painter = painterResource(id = eventsDesignArgs.placeholderDetailLogo),
                                        contentDescription = "",
                                        contentScale = ContentScale.Fit,
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .height(300.dp),
                                    )
                                }

                                event.location?.geopoint.let { eventCoordinates ->
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth(0.75f)
                                                .aspectRatio(1.75f)
                                                .align(Alignment.BottomCenter),
                                    ) {
                                        if (eventCoordinates != null) {
                                            Map(
                                                eventLocation = eventCoordinates,
                                                masterDesignArgs = masterDesignArgs,
                                                eventsDesignArgs = eventsDesignArgs,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    eventsDesignArgs.mRootBoarderSpacing
                                        ?: masterDesignArgs.mBorderSpace,
                                ),
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 16.dp),
                        ) {
                            Text(
                                text = event.title ?: event.name ?: "",
                                color = masterDesignArgs.mCardTextColor,
                                style = masterDesignArgs.bodyTextStyle,
                            )
                            Row(
                                modifier =
                                    Modifier
                                        .padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_clock),
                                    tint = masterDesignArgs.highlightColor,
                                    contentDescription = stringResource(id = R.string.events_time_description),
                                    modifier =
                                        Modifier
                                            .width(14.dp)
                                            .height(14.dp),
                                )

                                Spacer(
                                    modifier =
                                        Modifier
                                            .width(8.dp),
                                )

                                Text(
                                    text =
                                        if (event.isAllDay) {
                                            event.parsedStartDateTime.toDateString() + stringResource(id = R.string.events_full_day)
                                        } else {
                                            event.parsedStartDateTime.toDateTimeString() +
                                                (if (event.parsedEndDateTime != null) " - ${event.parsedEndDateTime.toTimeString()}" else "")
                                        },
                                    color =
                                        eventsDesignArgs.mScreenTextColor
                                            ?: masterDesignArgs.mScreenTextColor,
                                    style = masterDesignArgs.normalTextStyle,
                                )
                            }
                        }

                        Divider(
                            color = masterDesignArgs.highlightColor,
                            thickness = 1.dp,
                            modifier =
                                Modifier
                                    .padding(vertical = 12.dp),
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            if (eventsViewModel.eventsDesignArgs.isBookmarkingEnabled) {
                                IconColumn(
                                    icon =
                                        animateIntAsState(
                                            targetValue =
                                                if (event.isBookmarked.value) {
                                                    R.drawable.ic_heart_solid
                                                } else {
                                                    R.drawable.ic_heart_light
                                                },
                                        ).value,
                                    iconTint = masterDesignArgs.highlightColor,
                                    title = stringResource(id = R.string.events_bookmark),
                                    titleColor = masterDesignArgs.mCardTextColor,
                                    onClick = {
                                        eventsViewModel.switchBookmark(event)
                                    },
                                    masterDesignArgs = masterDesignArgs,
                                )
                            }

                            IconColumn(
                                icon = R.drawable.ic_main_share,
                                iconTint = masterDesignArgs.highlightColor,
                                title = stringResource(id = R.string.global_share),
                                titleColor = masterDesignArgs.mCardTextColor,
                                onClick = {
                                    shareEvent.value = true
                                },
                                masterDesignArgs = masterDesignArgs,
                            )

                            IconColumn(
                                icon = R.drawable.ic_calendar_month,
                                iconTint = masterDesignArgs.highlightColor,
                                title = stringResource(id = R.string.events_calendar),
                                titleColor = masterDesignArgs.mCardTextColor,
                                onClick = {
                                    addEventToCalendar(event, context)
                                },
                                masterDesignArgs = masterDesignArgs,
                            )
                        }

                        Divider(
                            color = masterDesignArgs.highlightColor,
                            thickness = 1.dp,
                            modifier =
                                Modifier
                                    .padding(vertical = 12.dp),
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier =
                                Modifier
                                    .padding(horizontal = 16.dp),
                        ) {
                            Text(
                                text = event.category ?: "",
                                color = masterDesignArgs.mCardTextColor,
                                style = masterDesignArgs.normalTextStyle,
                            )

                            LinkfiedText(
                                masterDesignArgs = masterDesignArgs,
                                text = event.info.fromHtml(),
                            )

                            if (event.offers.isNotEmpty()) {
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.events_ticket_info),
                                        color = masterDesignArgs.mCardTextColor,
                                        style = masterDesignArgs.bodyTextStyle,
                                    )
                                    for (offer in event.offers) {
                                        Text(
                                            text = "Preis: ${offer.price}€",
                                            color = masterDesignArgs.mCardTextColor,
                                            style = masterDesignArgs.normalTextStyle,
                                        )
                                    }
                                }
                            }

                            if (event.location != null) {
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.events_location),
                                        color = masterDesignArgs.mCardTextColor,
                                        style = masterDesignArgs.bodyTextStyle,
                                    )

                                    Text(
                                        text = event.location.address.name,
                                        color = masterDesignArgs.mCardTextColor,
                                        style = masterDesignArgs.normalTextStyle,
                                    )
                                    Text(
                                        text = "${event.location.address.postalCode}, ${event.location.address.addressLocality}",
                                        color = masterDesignArgs.mCardTextColor,
                                        style = masterDesignArgs.normalTextStyle,
                                    )
                                    Text(
                                        text = event.location.address.streetAddress,
                                        color = masterDesignArgs.mCardTextColor,
                                        style = masterDesignArgs.normalTextStyle,
                                    )
                                }
                            }

                            if (event.link != null) {
                                OpenWebsiteElement(
                                    url = event.link,
                                    context = context,
                                    withTitle = true,
                                    masterDesignArgs = masterDesignArgs,
                                    moduleDesignArgs = eventsDesignArgs,
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            if (event.location != null) {
                                MainButton(
                                    masterDesignArgs = masterDesignArgs,
                                    moduleDesignArgs = eventsDesignArgs,
                                    onClick = {
                                        context.openMapRouteTo(event.location.geopoint)
                                    },
                                    buttonText = stringResource(id = R.string.global_show_route),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun addEventToCalendar(
    event: Event,
    context: Context,
) {
    val intent =
        AddEventToSystemCalendar(
            title = event.title ?: event.name ?: "",
            description = event.info ?: "",
            startDateTime = event.parsedStartDateTime ?: LocalDateTime.now(),
            endDateTime = event.parsedEndDateTime,
        )
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.global_add_to_calendar),
        ),
    )
}

@Composable
private fun shareEvent(
    context: Context,
    title: String = "",
    url: String = "",
    summary: String = "",
    appStoreLink: String = "",
    @StringRes cityName: Int = -1,
    shareEvent: MutableState<Boolean>,
) {
    val header = "$title\n\n${summary.fromHtml()}\n\n$url"
    val details = "\n\n${stringResource(id = cityName)}-App:\n$appStoreLink"

    context.shareText(
        title = stringResource(id = R.string.global_share_text),
        text = "$header$details",
    )

    shareEvent.value = false
}

@ExperimentalMaterialApi
@Composable
private fun Map(
    eventLocation: Coordinates,
    masterDesignArgs: MasterDesignArgs,
    eventsDesignArgs: EventsDesignArgs,
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(eventsDesignArgs.mShapeCard ?: masterDesignArgs.mShapeCard),
        elevation = 0.dp,
    ) {
        GoogleMap(
            modifier =
                Modifier
                    .fillMaxSize(),
            cameraPositionState =
                CameraPositionState(
                    CameraPosition(eventLocation.toLatLng(), 15.0f, 0f, 0f),
                ),
            properties =
                MapProperties(
                    mapStyleOptions =
                        if (eventsDesignArgs.mapStyle != null) {
                            MapStyleOptions.loadRawResourceStyle(
                                context,
                                eventsDesignArgs.mapStyle!!,
                            )
                        } else {
                            null
                        },
                ),
            uiSettings =
                MapUiSettings(
                    compassEnabled = false,
                    rotationGesturesEnabled = false,
                    scrollGesturesEnabled = false,
                    tiltGesturesEnabled = false,
                    mapToolbarEnabled = false,
                    indoorLevelPickerEnabled = false,
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false,
                    zoomGesturesEnabled = false,
                ),
            onMapClick = { _ ->
                context.openMapRouteTo(eventLocation)
            },
            onMapLoaded = {
                // ...
            },
        ) {
            Marker(
                state = MarkerState(eventLocation.toLatLng()),
                onClick = {
                    context.openMapRouteTo(eventLocation)
                    true
                },
            )
        }
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
    onClick: (() -> Unit)?,
) {
    Column(
        modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .clickable {
                    onClick?.invoke()
                },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = iconTint,
            modifier =
                modifier
                    .padding(bottom = 4.dp),
        )

        Text(
            text = title,
            style = masterDesignArgs.normalTextStyle,
            color = titleColor,
        )
    }
}
