package com.falcon.ggsipunotices

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.Settled
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.falcon.ggsipunotices.model.Notice
import com.falcon.ggsipunotices.ui.NoticeItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailItem(
    notice: Notice,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentItem by rememberUpdatedState(notice)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it) {
                StartToEnd -> {
                    // CurrentItem // Download
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                }
                EndToStart -> {
                    // CurrentItem // Share
                    Toast.makeText(context, "Item archived", Toast.LENGTH_SHORT).show()
                }
                Settled -> return@rememberSwipeToDismissBoxState false
            }
            return@rememberSwipeToDismissBoxState true
        },
        // positional threshold of 25%
        positionalThreshold = { it * .25f }
    )
    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = { DismissBackground(dismissState)},
        content = {
            NoticeItem(
                notice = notice,
                startDownloading = { a, b ->
                    return@NoticeItem
                },
                openFile = { file ->
                    return@NoticeItem
                },
                shareFile = { a ->
                    return@NoticeItem
                }
            )
        })
}
