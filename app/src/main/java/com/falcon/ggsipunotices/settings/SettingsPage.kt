package com.falcon.ggsipunotices.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.falcon.ggsipunotices.R
import com.falcon.ggsipunotices.utils.Utils.COLLEGE
import com.falcon.ggsipunotices.utils.Utils.PREFERENCE
import com.falcon.unikit.settings.RegularPreference
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

@Composable
fun SettingsScreen(
    notificationPreferenceList: List<String>,
    collegePreferenceList: List<String>,
    deviceId: String,
    onBackCLick: () -> Boolean,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                contentColor = MaterialTheme.colors.onSurface,
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackCLick()
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                            )
                        },
                    )
                },
                modifier = Modifier.statusBarsPadding(),
            )
        },
    ) { innerPadding ->
        innerPadding
        val context = LocalContext.current
        val openUrlLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {}
        val sendMail = { title: String ->
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, arrayOf("usarcompanion@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, title + R.string.app_name.toString())
            }
            try {
                openUrlLauncher.launch(emailIntent)
            } catch (e: ActivityNotFoundException) {
                Toast
                    .makeText(context, "No Mail App Found", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        val openLink = { url: String ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            openUrlLauncher.launch(intent)
        }
        val openOssLicensesMenuActivity = {
            startActivity(context, Intent(context, OssLicensesMenuActivity::class.java), null)
        }
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            PreferenceCategory("General")
            PreferencePicker(preferenceList = notificationPreferenceList, preferenceName = PREFERENCE, label = "Notification Preference", defaultValue = "All",  enableSearch = false, deviceId = deviceId)
            PreferencePicker(preferenceList = collegePreferenceList, preferenceName = COLLEGE, label = "College Preference", defaultValue = "All", enableSearch = true, deviceId = deviceId)
            RegularPreference("Contact Us", "", {sendMail("Regarding App ")})
            PreferenceCategory("Developer")
            RegularPreference("Bug Report", "",{sendMail("Bug Report For ")})
            RegularPreference("Resources Used", "",{openLink("https://sites.google.com/view/ggsipu-notices/resources-used")})
            RegularPreference("Open Source Libraries", "",{openOssLicensesMenuActivity()})
            RegularPreference("Terms And Conditions", "",{openLink("https://sites.google.com/view/ggsipu-notices/terms-conditions")})
            RegularPreference("Privacy Policy", "",{openLink("https://sites.google.com/view/ggsipu-notices/home")})
        }
    }
}
