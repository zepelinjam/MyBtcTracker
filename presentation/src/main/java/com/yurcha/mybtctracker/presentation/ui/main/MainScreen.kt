package com.yurcha.mybtctracker.presentation.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yurcha.mybtctracker.presentation.ui.utils.rememberFlowWithLifecycle
import com.yurcha.domain.model.BitcoinRate
import com.yurcha.domain.model.Transaction

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    LaunchedEffect(effect) {
        effect.collect { action ->
            /*
            when (action) {
                is MainScreenReducer.MainScreenEffect.NavigateToTopic -> {
                    // This effect would result in a navigation to another screen of the application
                    // with the topicId as a parameter.
                    Log.d("ForYouScreen", "Navigate to topic with id: ${action.topicId}")
                }

                is MainScreenReducer.MainScreenEffect.NavigateToNews -> launchCustomChromeTab(
                    context,
                    Uri.parse(action.newsUrl),
                    backgroundColor
                )
            } */
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getMainData()
    }

    MainScreenContent(
        modifier = modifier,
        isBitcoinRateLoading = state.value.isBitcoinRateLoading,
        isBalanceLoading = state.value.isBalanceLoading,
        isTransactionsLoading = state.value.isTransactionsLoading,
        transactions = state.value.transactions,
        bitcoinRate = state.value.bitcoinRate,
        bitcoinBalance = state.value.bitcoinBalance,
        onRefillBalanceClicked = {
            /*
            viewModel.sendEvent(
                event = ForYouScreenReducer.ForYouEvent.UpdateTopicIsFollowed(
                    topicId = topicId,
                    isFollowed = isChecked,
                )
            ) */
        },
        onAddTransactionClicked = viewModel::onTopicClick,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreenContent(
    isBitcoinRateLoading: Boolean,
    isBalanceLoading: Boolean,
    isTransactionsLoading: Boolean,
    transactions: List<Transaction>,
    bitcoinRate: BitcoinRate,
    bitcoinBalance: String,
    onRefillBalanceClicked: () -> Unit,
    onAddTransactionClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {

    // This code should be called when the UI is ready for use and relates to Time To Full Display.
//    ReportDrawnWhen { !topicsLoading && !newsLoading }

    Text(
        text = "${bitcoinRate.currentPrice} BTC",
        modifier = Modifier
    )

    /*

    val itemsAvailable = remember {
        derivedStateOf {
            val topicsSize = if (topicsLoading) 0 else 1
            val transactionSize = if (newsLoading) 0 else news.size
            topicsSize + transactionSize
        }
    }

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(
        itemsAvailable = itemsAvailable.value,
    )

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(300.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 24.dp,
            modifier = Modifier
                .testTag("forYou:feed"),
            state = state,
        ) {
            if (topicsVisible) {
                item(span = StaggeredGridItemSpan.FullLine, contentType = "onboarding") {
                    CompositionLocalProvider(LocalLoading provides topicsLoading) {
                        Column(
                            modifier = Modifier.layout { measurable, constraints ->
                                val placeable = measurable.measure(
                                    constraints.copy(
                                        maxWidth = constraints.maxWidth + 32.dp.roundToPx(),
                                    ),
                                )
                                layout(placeable.width, placeable.height) {
                                    placeable.place(0, 0)
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.feature_foryou_onboarding_guidance_title),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = stringResource(R.string.feature_foryou_onboarding_guidance_subtitle),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, start = 24.dp, end = 24.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            TopicSelection(
                                topics = topics,
                                onTopicCheckedChanged = onTopicCheckedChanged,
                                modifier = Modifier
                                    .padding(bottom = 8.dp),
                            )
                            // Done button
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                NiaButton(
                                    onClick = saveFollowedTopics,
                                    enabled = topics.any { it.isFollowed },
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp)
                                        .widthIn(364.dp)
                                        .fillMaxWidth(),
                                ) {
                                    Text(
                                        text = stringResource(R.string.feature_foryou_done),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            items(
                items = news,
                key = { it.id },
                contentType = { "newsFeedItem" },
            ) { userNewsResource ->
                CompositionLocalProvider(LocalLoading provides newsLoading) {
                    NewsResourceCardExpanded(
                        userNewsResource = userNewsResource,
                        isBookmarked = userNewsResource.isSaved,
                        onClick = {
                            onNewsResourceViewed(userNewsResource.id)
                        },
                        hasBeenViewed = userNewsResource.hasBeenViewed,
                        onToggleBookmark = {
                            onNewsResourcesCheckedChanged(
                                userNewsResource.id,
                                !userNewsResource.isSaved,
                            )
                        },
                        onTopicClick = onTopicClick,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .animateItemPlacement(),
                    )
                }
            }

            item(span = StaggeredGridItemSpan.FullLine, contentType = "bottomSpacing") {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Add space for the content to clear the "offline" snackbar.
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                }
            }
        }
        AnimatedVisibility(
            visible = newsLoading || topicsLoading,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
            ) + fadeOut(),
        ) {
            val loadingContentDescription = stringResource(id = R.string.feature_foryou_loading)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                NiaOverlayLoadingWheel(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
            }
        }
        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable.value,
            ),
        )
    }
    NotificationPermissionEffect() */
}

/*
@DevicePreviews
@Composable
fun ForYouScreenPopulatedFeed(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    ComposeMVITheme {
        MainScreenContent(
            topicsLoading = false,
            newsLoading = false,
            topics = emptyList(),
            topicsVisible = false,
            news = userNewsResources,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenOfflinePopulatedFeed(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    ComposeMVITheme {
        MainScreenContent(
            topicsLoading = false,
            newsLoading = false,
            topics = emptyList(),
            topicsVisible = false,
            news = userNewsResources,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenTopicSelection(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    ComposeMVITheme {
        MainScreenContent(
            topicsLoading = false,
            newsLoading = false,
            topics = userNewsResources.flatMap { news -> news.followableTopics }
                .distinctBy { it.topic.id },
            topicsVisible = true,
            news = userNewsResources,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenLoading() {
    ComposeMVITheme {
        MainScreenContent(
            topicsLoading = true,
            newsLoading = true,
            topics = emptyList(),
            topicsVisible = false,
            news = emptyList(),
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenPopulatedAndLoading(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    ComposeMVITheme {
        MainScreenContent(
            topicsLoading = true,
            newsLoading = false,
            topics = emptyList(),
            topicsVisible = false,
            news = userNewsResources,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
        )
    }
} */