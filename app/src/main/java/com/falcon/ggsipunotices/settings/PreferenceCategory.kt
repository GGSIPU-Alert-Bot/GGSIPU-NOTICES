package com.falcon.unikit.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceCategory(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        modifier = modifier
            .padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 8.dp,
            ),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.subtitle2,
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreferenceCategoryPreview() {
    PreferenceCategory(title = "Miscellaneous")
}