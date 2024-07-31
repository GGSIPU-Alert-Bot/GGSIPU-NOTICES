package com.falcon.ggsipunotices

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.Settled
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.falcon.ggsipunotices.model.Notice
import com.falcon.ggsipunotices.ui.NoticeItem
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.util.Objects
import java.util.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperNoticeItem(
    notice: Notice,
    modifier: Modifier = Modifier,
    startDownloading: (String, Context, String?, Int, CoroutineScope, ComponentActivity?) -> Unit,
    openFile: (Context, File) -> Unit,
    shareFile: (String, File) -> Unit,
    activity: ComponentActivity?
) {
    val context = LocalContext.current
    val currentItem by rememberUpdatedState(notice)

    // Change currentItem to notice, if issue persists // TODO
    val fileTitle = removeNonAlphaNumeric(currentItem.title.toString()).plus(".pdf") // For download / share purposes

    val file = File(
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
        fileTitle
    )
//    val cat = FileProvider.getUriForFile(
//        Objects.requireNonNull(activity?.applicationContext)!!,
//        activity?.packageName + ".provider", file3);
//    val file = File()
//
    val scope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it) {
                StartToEnd -> {
                    // CurrentItem // Share
                    Toast.makeText(context, "StartToEnd Swiped", Toast.LENGTH_SHORT).show()
                    if (file.exists()) {
                        shareFile(fileTitle, file)
                    } else {
                        startDownloading(fileTitle, context, notice.url, Random().nextInt(), scope, activity)
                    }


                }
                EndToStart -> {
                    // CurrentItem // Download
                    Toast.makeText(context, "EndToStart Swiped", Toast.LENGTH_SHORT).show()
                    if (file.exists()) {
                        openFile(context, file)
                    } else {
                        startDownloading(fileTitle, context, notice.url, Random().nextInt(), scope, activity)
                    }
                }
                Settled -> return@rememberSwipeToDismissBoxState false
            }
            return@rememberSwipeToDismissBoxState false
        },
        // positional threshold of 25%
        positionalThreshold = { it * .30f }
    )
    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = { DismissBackground(dismissState)},
        content = {
            NoticeItem(
                notice = notice
            )
        })
}

fun removeNonAlphaNumeric(input: String): String {
    val regex = "[^a-zA-Z0-9]".toRegex()
    return input.replace(regex, "")
}
