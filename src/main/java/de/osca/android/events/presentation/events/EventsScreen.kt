package de.osca.android.events.presentation.events

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.gms.maps.model.LatLng
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerColors
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import de.osca.android.essentials.domain.entity.Coordinates
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.screen_wrapper.ScreenWrapper
import de.osca.android.essentials.utils.extensions.SetSystemStatusBar
import de.osca.android.essentials.utils.extensions.getLastDeviceLocation
import de.osca.android.essentials.utils.extensions.shortToast
import de.osca.android.events.R
import de.osca.android.events.navigation.EventsNavItems
import de.osca.android.events.presentation.args.EventsDesignArgs
import de.osca.android.events.presentation.components.EventsInfoCard
import de.osca.android.events.presentation.components.EventsTopBar
import java.lang.Float.max
import java.time.LocalDate

@Suppress("ktlint:standard:function-naming")
/**
 * Main Screen for Events
 *
 * @param navController from the navigationGraph
 * @param eventsViewModel screen creates the corresponding viewModel
 * @param masterDesignArgs main design arguments for the overall design
 */
@ExperimentalMaterialApi
@Composable
fun EventsScreen(
    navController: NavController,
    eventFilter: String? = null,
    eventsViewModel: EventsViewModel = hiltViewModel(),
    masterDesignArgs: MasterDesignArgs = eventsViewModel.defaultDesignArgs,
    eventsDesignArgs: EventsDesignArgs = eventsViewModel.eventsDesignArgs,
) {
    val context = LocalContext.current

    LaunchedEffect(eventFilter) {
        eventsViewModel.setSearch(eventFilter ?: "")
    }

    val location =
        remember {
            mutableStateOf(Coordinates().toLatLng())
        }

    context.getLastDeviceLocation { result ->
        result?.let { latLng ->
            location.value =
                LatLng(
                    latLng.latitude,
                    latLng.longitude,
                )
        } ?: with(context) {
            shortToast(text = getString(R.string.global_no_location))
        }
    }

    val scrollState = rememberLazyListState()
    val collapsed by remember {
        derivedStateOf {
            scrollState.firstVisibleItemScrollOffset <= 0
        }
    }
    val eventsList = eventsViewModel.events.collectAsLazyPagingItems()

    SetSystemStatusBar(
        !(eventsDesignArgs.mIsStatusBarWhite ?: masterDesignArgs.mIsStatusBarWhite),
        Color.Transparent,
    )

    ScreenWrapper(
        topBar = {
            EventsTopBar(viewModel = eventsViewModel, navController = navController)
        },
        screenWrapperState = eventsViewModel.wrapperState,
        retryAction = {
            eventsViewModel.initializeEvents()
        },
        masterDesignArgs = masterDesignArgs,
        moduleDesignArgs = eventsDesignArgs,
    ) {
        if (eventsViewModel.eventsDesignArgs.showSearchBar) {
            CollapsableToolbar(
                viewModel = eventsViewModel,
                collapsed = collapsed,
                designArgs = eventsViewModel.eventsDesignArgs,
                masterDesignArgs = masterDesignArgs,
            )
        }

        if (eventsList.itemCount > 0) {
            LazyColumn(
                state = scrollState,
                contentPadding =
                    PaddingValues(
                        top = 16.dp,
                        bottom = 32.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(count = eventsList.itemCount, itemContent = { index ->
                    val event = eventsList[index]

                    if (event != null) {
                        EventsInfoCard(
                            event = event,
                            onClick = {
                                event.objectId?.let {
                                    navController.navigate(EventsNavItems.getEventDetailRoute(it))
                                }
                            },
                            onBookmark = eventsViewModel::switchBookmark,
                            location = location.value,
                            masterDesignArgs = masterDesignArgs,
                            eventsViewModel = eventsViewModel,
                            eventDesignArgs = eventsDesignArgs,
                        )
                    }
                })
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.events_not_found),
                    style = masterDesignArgs.normalTextStyle,
                    color = eventsDesignArgs.mHintTextColor ?: masterDesignArgs.mHintTextColor,
                )
            }
        }
    }
}

@Composable
fun EventsDatePickerDialog(
    datePickerState: MaterialDialogState,
    positiveTextColor: Color,
    negativeTextColor: Color,
    datePickerColors: DatePickerColors,
    onDatePicked: (LocalDate) -> Unit,
) {
    MaterialDialog(
        dialogState = datePickerState,
        buttons = {
            positiveButton(
                stringResource(id = R.string.global_ok),
                textStyle = MaterialTheme.typography.button.copy(color = positiveTextColor),
            )
            negativeButton(
                stringResource(id = R.string.global_cancel),
                textStyle = MaterialTheme.typography.button.copy(color = negativeTextColor),
            )
        },
    ) {
        val currentYear = LocalDate.now().year
        datepicker(
            title = stringResource(R.string.global_select_date),
            yearRange = IntRange(currentYear, currentYear + 10),
            colors = datePickerColors,
        ) { date ->
            onDatePicked.invoke(date)
        }
    }
}

@Composable
fun SectionSearchBar(
    modifier: Modifier = Modifier,
    collapsed: Boolean,
    alpha: Float,
    viewModel: EventsViewModel,
    designArgs: EventsDesignArgs,
    masterDesignArgs: MasterDesignArgs,
) {
    val searchText by viewModel.searchText.collectAsState()
    val datePickerState = rememberMaterialDialogState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val selectedDate by viewModel.selectedDate.collectAsState()
    val showBookmarkOnly by viewModel.showBookmarkOnly.collectAsState()

    val resources = LocalContext.current.resources

    EventsDatePickerDialog(
        datePickerState = datePickerState,
        positiveTextColor = masterDesignArgs.mDialogsBackColor,
        negativeTextColor = masterDesignArgs.mDialogsBackColor,
        datePickerColors =
            DatePickerDefaults.colors(
                headerBackgroundColor = masterDesignArgs.mDialogsBackColor,
                headerTextColor = masterDesignArgs.mDialogsTextColor,
                dateActiveBackgroundColor = masterDesignArgs.mDialogsBackColor,
                dateActiveTextColor = masterDesignArgs.mDialogsTextColor,
            ),
    ) { pickedDate ->
        viewModel.setDate(pickedDate)
    }

    Card(
        elevation = designArgs.topBarElevation ?: masterDesignArgs.mSheetElevation,
        shape = masterDesignArgs.mShapeTopSheet,
        backgroundColor = designArgs.mTopBarBackColor ?: masterDesignArgs.mTopBarBackColor,
        contentColor = designArgs.mTopBarTextColor ?: masterDesignArgs.mTopBarTextColor,
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        ) {
            AnimatedVisibility(
                visible = collapsed,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column(
                    modifier =
                        Modifier
                            .padding(vertical = 0.dp),
                ) {
                    Text(
                        text =
                            viewModel.entriesFoundText(
                                searchText,
                                selectedDate,
                                showBookmarkOnly,
                                resources,
                            ),
                        style = masterDesignArgs.normalTextStyle,
                        modifier = Modifier.padding(top = 15.dp),
                        color =
                            (
                                designArgs.mTopBarTextColor
                                    ?: masterDesignArgs.mTopBarTextColor
                            ).copy(alpha = alpha),
                    )
                }
            }

            Row(
                modifier =
                    Modifier
                        .padding(top = 16.dp, bottom = 22.dp)
                        .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center,
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = { viewModel.setSearch(it) },
                    decorationBox = { innerTextField ->
                        SearchTextFieldDecorationBox(
                            innerTextField = innerTextField,
                            iconTint = masterDesignArgs.mDialogsBackColor,
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.weight(5f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions =
                        KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            },
                        ),
                )

                if (designArgs.showCalendarPicker) {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(vertical = 0.dp, horizontal = 8.dp)
                                .background(Color.White, RoundedCornerShape(5.dp))
                                .clickable {
                                    if (selectedDate != null) {
                                        viewModel.setDate(null)
                                    } else {
                                        datePickerState.show()
                                    }
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter =
                                painterResource(
                                    id = if (selectedDate != null) R.drawable.ic_close_circle else R.drawable.ic_calendar_light,
                                ),
                            contentDescription = stringResource(id = R.string.events_calendar),
                            tint = masterDesignArgs.mDialogsBackColor,
                            modifier =
                                Modifier
                                    .size(40.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchTextFieldDecorationBox(
    iconTint: Color,
    icon: (@Composable () -> Unit)? = null,
    innerTextField: @Composable () -> Unit,
    backgroundColor: Color = Color.White,
) {
    Row(
        modifier =
            Modifier
                .background(backgroundColor, RoundedCornerShape(5.dp))
                .padding(vertical = 6.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        innerTextField()

        if (icon == null) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = stringResource(id = R.string.global_search),
                tint = iconTint,
                modifier =
                    Modifier
                        .width(28.dp)
                        .height(28.dp),
            )
        } else {
            icon.invoke()
        }
    }
}

@Composable
private fun CollapsableToolbar(
    viewModel: EventsViewModel,
    collapsed: Boolean,
    designArgs: EventsDesignArgs,
    masterDesignArgs: MasterDesignArgs,
) {
    val position by animateFloatAsState(
        targetValue = max(-1f, -((1 - (if (collapsed) 0f else 1f)) * 100)),
        label = "",
    )

    SectionSearchBar(
        viewModel = viewModel,
        collapsed = collapsed,
        alpha = 1f,
        modifier =
            Modifier.graphicsLayer {
                translationY = position
            },
        designArgs = designArgs,
        masterDesignArgs = masterDesignArgs,
    )
}
