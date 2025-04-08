package de.osca.android.events.presentation.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.LatLng
import de.osca.android.essentials.presentation.component.design.BaseCardContainer
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.utils.extensions.metersToDistanceString
import de.osca.android.essentials.utils.extensions.toCoordinates
import de.osca.android.essentials.utils.extensions.toDateTimeString
import de.osca.android.events.R
import de.osca.android.events.domain.entity.Event
import de.osca.android.events.presentation.args.EventsDesignArgs
import de.osca.android.events.presentation.events.EventsViewModel

@Composable
fun EventsInfoCard(
    event: Event,
    modifier: Modifier = Modifier,
    onBookmark: ((Event) -> Unit)?,
    onClick: () -> Unit,
    location: LatLng,
    eventsViewModel: EventsViewModel,
    masterDesignArgs: MasterDesignArgs,
    eventDesignArgs: EventsDesignArgs = eventsViewModel.eventsDesignArgs,
) {
    val context = LocalContext.current
    val distanceToEvent = location.toCoordinates().distanceTo(event.location?.geopoint)

    val bookmarkIcon =
        animateIntAsState(
            when (event.isBookmarked.value) {
                true -> R.drawable.ic_heart_solid
                false -> R.drawable.ic_heart_light
            },
        )

    BaseCardContainer(
        modifier = modifier,
        moduleDesignArgs = eventDesignArgs,
        overrideConstraintHeight = if (eventDesignArgs.isLogoRotated) 180.dp else 210.dp,
        onClick = {
            onClick()
        },
        masterDesignArgs = masterDesignArgs,
    ) {
        Box {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = event.category ?: "",
                        maxLines = 1,
                        color = eventDesignArgs.mCardTextColor ?: masterDesignArgs.mCardTextColor,
                        style = masterDesignArgs.normalTextStyle,
                        modifier = Modifier.weight(1.0f),
                    )

                    if (eventsViewModel.eventsDesignArgs.isBookmarkingEnabled) {
                        IconButton(
                            modifier = Modifier.size(22.dp),
                            onClick = {
                                onBookmark?.invoke(event)
                            },
                        ) {
                            Icon(
                                painterResource(bookmarkIcon.value),
                                contentDescription = "",
                                tint = masterDesignArgs.highlightColor,
                            )
                        }
                    }
                }

                Row {
                    Text(
                        text = event.title ?: event.name ?: "",
                        maxLines = 2,
                        color = eventDesignArgs.mCardTextColor ?: masterDesignArgs.mCardTextColor,
                        style = masterDesignArgs.overlineTextStyle,
                        modifier =
                            Modifier
                                .weight(9f),
                    )
                }

                Column {
                    Column {
                        Text(
                            text =
                                if (event.isAllDay) {
                                    "${
                                        event.parsedStartDateTime.toDateTimeString()
                                            ?.replace(" ", " | ")
                                    } ganztags"
                                } else {
                                    event.parsedStartDateTime.toDateTimeString()?.replace(" ", " | ")
                                        ?: ""
                                },
                            maxLines = 1,
                            color = masterDesignArgs.highlightColor,
                            style = masterDesignArgs.normalTextStyle,
                        )

                        if (distanceToEvent < 1000000f) {
                            Row(
                                modifier =
                                    Modifier
                                        .padding(top = 16.dp),
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_location_arrow_light),
                                    contentDescription = "",
                                    modifier =
                                        Modifier
                                            .width(14.dp)
                                            .height(14.dp),
                                    tint =
                                        eventDesignArgs.mCardTextColor
                                            ?: masterDesignArgs.mCardTextColor,
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = context.metersToDistanceString(distanceToEvent),
                                    maxLines = 1,
                                    color = masterDesignArgs.highlightColor,
                                    style = masterDesignArgs.normalTextStyle,
                                )
                            }
                        }
                    }
                }
            }

            if (eventsViewModel.eventsDesignArgs.showEventImage) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom,
                    modifier =
                        Modifier
                            .fillMaxSize(),
                ) {
                    Card(
                        modifier =
                            Modifier
                                .offset(y = if (eventDesignArgs.isLogoRotated) 25.dp else 0.dp)
                                .requiredWidth(100.dp)
                                .requiredHeight(100.dp)
                                .rotate(if (eventDesignArgs.isLogoRotated) 45f else 0f),
                        shape =
                            RoundedCornerShape(
                                eventDesignArgs.mShapeCard ?: masterDesignArgs.mShapeCard,
                            ),
                        backgroundColor =
                            eventDesignArgs.mCardBackColor
                                ?: masterDesignArgs.mCardBackColor,
                        elevation = 0.dp,
                    ) {
                        if (event.image != null) {
                            Image(
                                painter = rememberAsyncImagePainter(event.image),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier =
                                    Modifier
                                        .fillMaxSize(),
                            )
                        } else {
                            Image(
                                painter = painterResource(id = eventDesignArgs.placeholderLogo),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier =
                                    Modifier
                                        .fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}
