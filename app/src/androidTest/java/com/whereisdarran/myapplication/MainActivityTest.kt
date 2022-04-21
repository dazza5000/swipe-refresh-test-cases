package com.whereisdarran.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import org.junit.Ignore

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun foo() {

        rule.setContent {
            Sample()
        }

        indicatorNode.assertIsDisplayed()

        val restingBounds = indicatorNode.getUnclippedBoundsInRoot()

        swipeRefreshNode.performTouchInput { swipeDown() }

        assertThat(indicatorNode.getUnclippedBoundsInRoot()).isEqualTo(restingBounds)
    }

    @Test
    fun swipeRefreshes() {
        val state = SwipeRefreshState(false)
        var refreshCallCount = 0

        rule.setContent {
            Sample(state) {
                state.isRefreshing = true
                refreshCallCount++
            }
        }

        // Swipe down on the swipe refresh
        swipeRefreshNode.performTouchInput { swipeDown() }

        // Assert that the onRefresh lambda was called once, and that we're refreshing
        assertThat(refreshCallCount).isEqualTo(1)
        assertThat(state.isRefreshing).isTrue()

        // Assert that the indicator is displayed
        indicatorNode.assertIsDisplayed()

        // Now stop 'refreshing' and assert that the indicator is no longer displayed
        state.isRefreshing = false
    }

    @Test
    fun refreshit() {
        val swipeRefreshState = SwipeRefreshState(false)
        var refreshCallCount = 0

        rule.setContent {
            WellSwipeRefresh(swipeRefreshState, {
                swipeRefreshState.isRefreshing = true
                refreshCallCount++
            }, modifier = Modifier.testTag(HOME_SWIPE_REFRESH_TEST_TAG)) {
                LazyColumn {
                    items(30) {
                        Row(Modifier.padding(16.dp)) {
                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "Text",
                                style = MaterialTheme.typography.subtitle2,
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }

        swipeRefreshNode.performTouchInput { swipeDown() }

        assertThat(refreshCallCount).isEqualTo(1)
        assertThat(swipeRefreshState.isRefreshing).isTrue()

        // Assert that the indicator is displayed
        indicatorNode.assertIsDisplayed()

        // Now stop 'refreshing' and assert that the indicator is no longer displayed
        swipeRefreshState.isRefreshing = false
    }

    private val swipeRefreshNode: SemanticsNodeInteraction
        get() = rule.onNodeWithTag(HOME_SWIPE_REFRESH_TEST_TAG)

    private val indicatorNode: SemanticsNodeInteraction
        get() = rule.onNodeWithTag(SWIPE_REFRESH_INDICATOR_TAG)
}


@Composable
private fun Sample(swipeRefreshState: SwipeRefreshState = SwipeRefreshState(true), onRefresh: () -> Unit = { }) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("woot") },
                backgroundColor = MaterialTheme.colors.surface,
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        // Simulate a fake 2-second 'load'. Ideally this 'refreshing' value would
        // come from a ViewModel or similar
        var refreshing by remember { mutableStateOf(true) }
        LaunchedEffect(refreshing) {
            if (refreshing) {
                delay(2000)
                refreshing = false
            }
        }

        SwipeRefresh(
            modifier = Modifier.testTag(HOME_SWIPE_REFRESH_TEST_TAG),
            state = swipeRefreshState,
            onRefresh = onRefresh,
            indicator = { indicatorState, trigger ->
                SwipeRefreshIndicator(
                    state = indicatorState,
                    refreshTriggerDistance = trigger,
                    modifier = Modifier.testTag(SWIPE_REFRESH_INDICATOR_TAG),
                    contentColor = MaterialTheme.colors.primary,
                )
            },
        ) {
            LazyColumn(contentPadding = padding) {
                items(30) { index ->
                    Row(Modifier.padding(16.dp)) {
                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "Text",
                            style = MaterialTheme.typography.subtitle2,
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WellSwipeRefresh(
    state: SwipeRefreshState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    SwipeRefresh(
        state = state,
        onRefresh = onRefresh,
        modifier = modifier,
        indicator = { indicatorState, trigger ->
            SwipeRefreshIndicator(
                state = indicatorState,
                refreshTriggerDistance = trigger,
                modifier = Modifier.testTag(SWIPE_REFRESH_INDICATOR_TAG),
                contentColor = MaterialTheme.colors.primary,
            )
        },
        content = content
    )
}