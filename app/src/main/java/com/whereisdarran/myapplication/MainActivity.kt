package com.whereisdarran.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.whereisdarran.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

const val HOME_SWIPE_REFRESH_TEST_TAG = "HOME_SWIPE_REFRESH_TEST_TAG"
const val SWIPE_REFRESH_INDICATOR_TAG = "swipe_refresh_indicator"


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Sample()
                }
            }
        }
    }
}

@Composable
private fun Sample() {
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
        var refreshing by remember { mutableStateOf(false) }
        LaunchedEffect(refreshing) {
            if (refreshing) {
                delay(2000)
                refreshing = false
            }
        }

        SwipeRefresh(
            modifier = Modifier.testTag(HOME_SWIPE_REFRESH_TEST_TAG),
            state = rememberSwipeRefreshState(isRefreshing = refreshing),
            onRefresh = { refreshing = true },
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
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}