package com.example.ostory.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ostory.data.repository.WorkRepository
import com.example.ostory.domain.model.Work
import com.example.ostory.domain.model.WorkType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReviewWriteViewModel(
    private val repository: WorkRepository = WorkRepository()
) : ViewModel() {

    private val _work = MutableStateFlow<Work?>(null)
    val work: StateFlow<Work?> = _work.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _rating = MutableStateFlow(0)
    val rating: StateFlow<Int> = _rating.asStateFlow()

    private val _reviewText = MutableStateFlow("")
    val reviewText: StateFlow<String> = _reviewText.asStateFlow()

    val isSaveEnabled: StateFlow<Boolean> = combine(_rating, _reviewText) { rating, text ->
        rating in 1..5 && text.trim().isNotEmpty()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun loadWorkDetail(workId: Int, workType: String) {
        val type = when (workType.uppercase()) {
            "MOVIE" -> WorkType.MOVIE
            "DRAMA", "TV" -> WorkType.DRAMA
            else -> {
                when (workType.lowercase()) {
                    "movie" -> WorkType.MOVIE
                    "drama", "tv" -> WorkType.DRAMA
                    else -> {
                        _errorMessage.value = "알 수 없는 작품 유형입니다."
                        return
                    }
                }
            }
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.getWorkDetail(workId, type)
                if (result == null) {
                    _errorMessage.value = "작품 상세 정보를 불러올 수 없습니다."
                } else {
                    _work.value = result
                }
            } catch (e: Exception) {
                _errorMessage.value = "작품 상세 정보를 불러오는 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setRating(value: Int) {
        _rating.value = value
    }

    fun setReviewText(value: String) {
        if (value.length <= 100) {
            _reviewText.value = value
        }
    }
}
