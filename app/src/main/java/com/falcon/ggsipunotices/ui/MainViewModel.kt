package com.falcon.ggsipunotices.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.ggsipunotices.model.Notice
import com.falcon.ggsipunotices.repository.NoticeRepository
import com.falcon.ggsipunotices.utils.Resource
import com.falcon.ggsipunotices.utils.Utils.PREFERENCE
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val noticeRepository: NoticeRepository,@ApplicationContext context: Context) : ViewModel() {

    private val _notices = MutableStateFlow<Resource<List<Notice>>>(Resource.Loading)
    val notices: StateFlow<Resource<List<Notice>>> get() = _notices
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    val notificationPreference = sharedPreferences.getString(PREFERENCE, "All") // TODO - USE THIS WHILE CALLING API
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
}
