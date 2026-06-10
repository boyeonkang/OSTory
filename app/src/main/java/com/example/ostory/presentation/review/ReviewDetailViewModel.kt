package com.example.ostory.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ostory.data.repository.ReviewRepository
import com.example.ostory.data.repository.GeminiRepository
import com.example.ostory.domain.model.ReviewRecord
import com.example.ostory.domain.model.OstTrack
import com.example.ostory.domain.model.Work
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ReviewDetailViewModel(
    private val reviewRepository: ReviewRepository = ReviewRepository.getInstance(),
    private val geminiRepository: GeminiRepository = GeminiRepository()
) : ViewModel() {

    private val _record = MutableStateFlow<ReviewRecord?>(null)
    val record: StateFlow<ReviewRecord?> = _record.asStateFlow()

    private val _ostList = MutableStateFlow<List<OstTrack>>(emptyList())
    val ostList: StateFlow<List<OstTrack>> = _ostList.asStateFlow()

    private val _isOstLoading = MutableStateFlow(false)
    val isOstLoading: StateFlow<Boolean> = _isOstLoading.asStateFlow()

    private val _isOstLoaded = MutableStateFlow(false)
    val isOstLoaded: StateFlow<Boolean> = _isOstLoaded.asStateFlow()

    private var collectJob: kotlinx.coroutines.Job? = null

    fun loadReviewRecord(recordId: Int) {
        collectJob?.cancel()
        _isOstLoaded.value = false
        _ostList.value = emptyList()
        collectJob = viewModelScope.launch {
            reviewRepository.recordsFlow.collect { list ->
                val record = list.find { it.id == recordId }
                _record.value = record
                if (record != null && !record.ostList.isNullOrEmpty()) {
                    _ostList.value = record.ostList
                    _isOstLoaded.value = true
                }
            }
        }
    }

    fun fetchOst() {
        val currentRecord = _record.value ?: return
        if (_isOstLoading.value) return

        viewModelScope.launch {
            _isOstLoading.value = true
            try {
                val year = try {
                    LocalDate.parse(currentRecord.watchedDate).year
                } catch (e: Exception) {
                    0
                }
                val tempWork = Work(
                    id = currentRecord.workId,
                    titleKo = currentRecord.titleKo ?: "",
                    titleEn = currentRecord.titleEn ?: "",
                    type = currentRecord.workType,
                    year = year,
                    posterPath = currentRecord.posterPath,
                    genres = emptyList(),
                    plot = "",
                    rating = 0.0,
                    ostList = emptyList()
                )
                val ost = geminiRepository.getOstInfo(tempWork)
                _ostList.value = ost
                if (ost.isNotEmpty()) {
                    val updatedRecord = currentRecord.copy(ostList = ost)
                    reviewRepository.updateRecord(updatedRecord)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _ostList.value = emptyList()
            } finally {
                _isOstLoading.value = false
                _isOstLoaded.value = true
            }
        }
    }
}
