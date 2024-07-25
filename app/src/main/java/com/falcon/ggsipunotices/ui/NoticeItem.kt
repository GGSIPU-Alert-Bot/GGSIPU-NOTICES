package com.falcon.ggsipunotices.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.falcon.ggsipunotices.model.Notice

@Composable
fun NoticeItem(notice: Notice) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = notice.title, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = notice.date)
    }
}
