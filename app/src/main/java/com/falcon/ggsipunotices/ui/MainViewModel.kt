package com.falcon.ggsipunotices.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.ggsipunotices.model.Notice
import com.falcon.ggsipunotices.repository.NoticeRepository
import com.falcon.ggsipunotices.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val noticeRepository: NoticeRepository) : ViewModel() {

    private val _notices = MutableStateFlow<Resource<List<Notice>>>(Resource.Loading)
    val notices: StateFlow<Resource<List<Notice>>> get() = _notices

    init {
        fetchNotices()
    }

    internal fun fetchNotices() {
        viewModelScope.launch {
            _notices.value = Resource.Loading
            try {
                val notices = noticeRepository.getNotices()
                _notices.value = Resource.Success(notices)
            } catch (e: Exception) {
                _notices.value = Resource.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }
}
