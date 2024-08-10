package com.falcon.ggsipunotices.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.ggsipunotices.model.FcmCollegePreferenceRequest
import com.falcon.ggsipunotices.model.FcmPreferenceRequest
import com.falcon.ggsipunotices.model.Notice
import com.falcon.ggsipunotices.repository.FcmPreferenceRepository
import com.falcon.ggsipunotices.repository.NoticeRepository
import com.falcon.ggsipunotices.utils.Resource
import com.falcon.ggsipunotices.utils.Utils.PREFERENCE
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val noticeRepository: NoticeRepository,
    private val fcmPreferenceRepository: FcmPreferenceRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _notices = MutableStateFlow<Resource<List<Notice>>>(Resource.Loading)
    val notices: StateFlow<Resource<List<Notice>>> get() = _notices
//    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
//    val notificationPreference = sharedPreferences.getString(PREFERENCE, "All") // TODO - USE THIS WHILE CALLING API
    init {
        Log.i("MainViewModel", "MainViewModel initialized")
        fetchNotices()
    }

    internal fun fetchNotices() {
        Log.i("MainViewModel", "Call to fetchNotices")
        _notices.value = Resource.Loading
        Log.i("MainViewModel", "Before coroutine launch, Class Name:" + notices.value.javaClass.simpleName)
        viewModelScope.launch {
            try {
                Log.i("MainViewModel", "Just Before Repository Call, Class Name:" + notices.value.javaClass.simpleName)
                val result = noticeRepository.getNotices()
                _notices.value = Resource.Success(result)
                Log.i("MainViewModel", "Just After Repository Call, Class Name:" + notices.value.javaClass.simpleName)
            } catch (e: Exception) {
                _notices.value = Resource.Error(e.localizedMessage ?: "An error occurred")
            }
        }
        Log.i("MainViewModel", "After coroutine launch, Class Name:" + notices.value.javaClass.simpleName)
    }

    internal fun sendFcmCollegePreference(deviceId: String, college: String) {
        viewModelScope.launch {
            try {
                val response = fcmPreferenceRepository.sendCollegePreference(
                    deviceId = deviceId,
                    preference = FcmCollegePreferenceRequest(college)
                )
                if (response.isSuccessful) {
                    Log.i("CollegePreferenceChange", "College Preference Updated")
                    Toast.makeText(context, "College Preference Updated", Toast.LENGTH_SHORT).show()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed To Update College Preference", Toast.LENGTH_SHORT).show()
                        Log.i("CollegePreferenceChange Error", response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                Log.i("FCM Error:", e.message.toString())
            }
        }
    }

    internal fun sendFcmNotificationPreference(deviceId: String, notificationPreference: String) {
        viewModelScope.launch {
            try {
                val response = fcmPreferenceRepository.sendFcmPreference(
                    deviceId = deviceId,
                    preference = if (notificationPreference == "All") FcmPreferenceRequest("all") else FcmPreferenceRequest("high_priority")
                )
                if (response.isSuccessful) {
                    Log.i("NotificationPreferenceChange", "Notification Preference Updated")
                    Toast.makeText(context, "Notification Preference Updated", Toast.LENGTH_SHORT).show()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed To Update Notification Preference", Toast.LENGTH_SHORT).show()
                        Log.i("NotificationPreferenceChange Error", response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                Log.i("FCM Error:", e.message.toString())
            }
        }
    }
}
