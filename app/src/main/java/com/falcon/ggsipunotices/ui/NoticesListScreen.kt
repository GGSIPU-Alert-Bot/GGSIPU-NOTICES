package com.falcon.ggsipunotices.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.falcon.ggsipunotices.R
import com.falcon.ggsipunotices.SuperNoticeItem
import com.falcon.ggsipunotices.model.Notice
import com.falcon.ggsipunotices.utils.Resource
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun NoticeListScreen(
    startDownloading: (Context, String?, String) -> Unit,
    openFile: (Context, String) -> Unit,
    shareFile: (Context, String) -> Unit,
    checkFileExists: (Context, String) -> Boolean
) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val noticesState by mainViewModel.notices.collectAsState()
    Column {
        MainScreenHeader()
        var searchQuery by remember { mutableStateOf("") }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black
            ),
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
                            it.title?.contains(searchQuery, true) ?: false
                        }
                        items(filteredNotices) { notice ->
                            SuperNoticeItem(
                                notice = notice,
                                startDownloading = startDownloading,
                                openFile = openFile,
                                shareFile = shareFile,
                                checkFileExists = checkFileExists
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

@Composable
private fun MainScreenHeader() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, 24.dp, 0.dp, 0.dp)
    ) {
        Text(
            text = "GGSIPU NOTICES",
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
