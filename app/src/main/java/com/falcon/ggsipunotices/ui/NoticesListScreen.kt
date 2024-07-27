package com.falcon.ggsipunotices.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.falcon.ggsipunotices.R
import com.falcon.ggsipunotices.model.Notice
import com.falcon.ggsipunotices.utils.Resource
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import java.io.File


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun NoticeListScreen(
    startDownloading: (String, String) -> Unit,
    openFile: (File) -> Unit,
    shareFile: (String) -> Unit
) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val noticesState by mainViewModel.notices.collectAsState()
    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = TopAppBarDefaults.smallTopAppBarColors()
        )
        var searchQuery by remember { mutableStateOf("") }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        val pullRefreshState = rememberPullRefreshState(
            refreshing = noticesState is Resource.Loading,
            onRefresh = {
                mainViewModel.fetchNotices()
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            Log.i("NoticeListScreen", "Before Entering When Statement, Class Name:" + noticesState.javaClass.simpleName)
            when (noticesState) {
                is Resource.Loading -> {
                    ShimmerEffect()
                }
                is Resource.Success -> {
                    Log.i("NoticeListScreen", "Inside Resource.Success Block, Class Name:" + noticesState.javaClass.simpleName)
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        val filteredNotices = (noticesState as Resource.Success<List<Notice>>).data.filter {
                            it.title.contains(searchQuery, true)
                        }
                        items(filteredNotices) { notice ->
                            NoticeItem(
                                notice,
                                startDownloading = startDownloading,
                                openFile = openFile,
                                shareFile = shareFile
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = (noticesState as Resource.Error).message,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            PullRefreshIndicator(
                refreshing = noticesState is Resource.Loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}


@Composable
fun ShimmerEffect() {
    Column {
        repeat(10) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(80.dp)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = Color.LightGray
                    )
            ) {}
        }
    }
}
