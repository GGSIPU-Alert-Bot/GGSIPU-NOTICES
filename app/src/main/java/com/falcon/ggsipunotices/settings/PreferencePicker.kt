package com.falcon.ggsipunotices.settings

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.falcon.ggsipunotices.utils.Utils.PREFERENCE

@Composable
fun PreferencePicker(preferenceList: List<String>){
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }
    val preference = sharedPreferences.getString(PREFERENCE, "All")
    Log.i(PREFERENCE, "meow" + preference.toString())
    var mSelectedText by remember { mutableStateOf(preference) }
    val editor = sharedPreferences.edit()

    var mExpanded by remember { mutableStateOf(false) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    Column(
        Modifier
            .padding(10.dp, 5.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = mSelectedText.toString(),
            onValueChange = {
                mSelectedText = it
                editor.putString(PREFERENCE, it)
                editor.apply()
//                TRY HERE TO OPEN DROP DOWN TODO()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                }
            ,
            label = {Text("Preference", modifier = Modifier
                .clickable {
                    mExpanded = !mExpanded
                })},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier
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
                .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                .clickable {
                    mExpanded = true
                }
        ) {
            preferenceList.forEach { preference ->
                DropdownMenuItem(onClick = {
                    editor.putString(PREFERENCE, preference)
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