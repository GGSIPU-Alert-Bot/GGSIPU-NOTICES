package com.falcon.ggsipunotices.settings

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

@Composable
fun PreferencePicker(
    preferenceList: List<String>,
    preferenceName: String,
    defaultValue: String,
    enableSearch: Boolean,
    label: String,
    onPreferenceChange: (String) -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }
    val preference = sharedPreferences.getString(preferenceName, defaultValue)
    Log.i(preferenceName, "meow" + preference.toString())
    var mSelectedText by remember { mutableStateOf(preference) }
    val editor = sharedPreferences.edit()

    var mExpanded by remember { mutableStateOf(false) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}
    val icon = if (mExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    var searchQuery by remember { mutableStateOf("") }
    var filteredList: List<String>
    Column(
        Modifier.padding(10.dp, 5.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = mSelectedText.toString(),
            onValueChange = {
                mSelectedText = it
                editor.putString(preferenceName, it)
                editor.apply()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                }
            ,
            label = {
                Text(
                    text = label,
                    modifier = Modifier
                        .clickable {
                            mExpanded = !mExpanded
                        }
                    )
                },
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = "KeyboardArrow",
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            mExpanded = !mExpanded
                        }
                )
            }
        )
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .padding(0.dp, 10.dp, 0.dp, 0.dp)
                .background(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White
                )
                .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                .clickable {
                    mExpanded = true
                }
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            if (enableSearch) {
                androidx.compose.material3.OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { androidx.compose.material3.Text("Search") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                filteredList = preferenceList.filter {
                    it.contains(searchQuery, true) ?: false
                }
            } else {
                filteredList = preferenceList
            }
            filteredList.forEach { preference ->
                DropdownMenuItem(onClick = {
                    onPreferenceChange(preference)
                    editor.putString(preferenceName, preference)
                    editor.apply()
                    Log.i("qwertyuiop", preference)
                    mSelectedText = preference
                    mExpanded = false
                }) {
                    Text(
                        text = preference,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}