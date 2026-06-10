package com.example.ostory.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ostory.data.repository.WorkRepository
import com.example.ostory.domain.model.Work
import com.example.ostory.domain.model.WorkType
import com.example.ostory.data.repository.ReviewRepository
import com.example.ostory.domain.model.ReviewRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    private val _watchedDate = MutableStateFlow<String>("")
    val watchedDate: StateFlow<String> = _watchedDate.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private var editingRecordId: Int = 0

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

    fun initDate(date: String?) {
        if (_watchedDate.value.isEmpty()) {
            _watchedDate.value = if (!date.isNullOrBlank() && date != "{selectedDate}") {
                date
            } else {
                LocalDate.now().toString()
            }
        }
    }

    fun setWatchedDate(dateStr: String) {
        _watchedDate.value = dateStr
    }

    fun loadReviewRecord(reviewId: Int) {
        editingRecordId = reviewId
        _isEditMode.value = true
        val record = ReviewRepository.getInstance().getRecordById(reviewId)
        if (record != null) {
            _rating.value = record.rating
            _reviewText.value = record.comment
            _watchedDate.value = record.watchedDate
            loadWorkDetail(record.workId, record.workType.name)
        }
    }

    fun saveReviewRecord(selectedDate: String? = null): Boolean {
        val currentWork = _work.value ?: return false
        val ratingVal = _rating.value
        val commentVal = _reviewText.value
        if (ratingVal !in 1..5 || commentVal.trim().isEmpty()) return false

        val finalDate = if (_isEditMode.value) {
            _watchedDate.value
        } else {
            if (!selectedDate.isNullOrBlank() && selectedDate != "{selectedDate}") selectedDate else _watchedDate.value.ifBlank { LocalDate.now().toString() }
        }

        val record = ReviewRecord(
            id = if (_isEditMode.value) editingRecordId else 0,
            workId = currentWork.id,
            workType = currentWork.type,
            watchedDate = finalDate,
            rating = ratingVal,
            comment = commentVal,
            posterPath = currentWork.posterPath,
            titleKo = currentWork.titleKo,
            titleEn = currentWork.titleEn
        )
        if (_isEditMode.value) {
            ReviewRepository.getInstance().updateRecord(record)
        } else {
            ReviewRepository.getInstance().addRecord(record)
        }
        return true
    }
}
