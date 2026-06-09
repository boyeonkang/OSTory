package com.example.ostory.presentation.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ostory.data.repository.ReviewRepository
import com.example.ostory.data.repository.GeminiRepository
import com.example.ostory.domain.model.ReviewRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PreferenceAnalysisViewModel(
    private val reviewRepository: ReviewRepository = ReviewRepository.getInstance(),
    private val geminiRepository: GeminiRepository = GeminiRepository()
) : ViewModel() {

    val records: StateFlow<List<ReviewRecord>> = reviewRepository.recordsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = reviewRepository.getRecords()
        )

    val totalCount: StateFlow<Int> = records
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = reviewRepository.getRecords().size
        )

    val averageRating: StateFlow<Double> = records
        .map { list ->
            if (list.isEmpty()) 0.0 else list.map { it.rating }.average()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = if (reviewRepository.getRecords().isEmpty()) 0.0 else reviewRepository.getRecords().map { it.rating }.average()
        )

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisResult = MutableStateFlow<PreferenceAnalysisResult?>(null)
    val analysisResult: StateFlow<PreferenceAnalysisResult?> = _analysisResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun analyzePreferences() {
        val currentRecords = records.value
        if (currentRecords.isEmpty()) return
        if (_isAnalyzing.value) return

        viewModelScope.launch {
            _isAnalyzing.value = true
            _errorMessage.value = null
            try {
                val result = geminiRepository.analyzePreference(currentRecords)
                _analysisResult.value = result
                _errorMessage.value = null
            } catch (e: HttpException) {
                e.printStackTrace()
                if (e.code() == 429) {
                    _errorMessage.value = "오늘의 AI 분석 요청 한도를 초과했습니다. 잠시 후 다시 시도하거나 내일 다시 이용해 주세요."
                } else {
                    _errorMessage.value = "AI 취향 분석 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
                }
                _analysisResult.value = null
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "AI 취향 분석 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
                _analysisResult.value = null
            } finally {
                _isAnalyzing.value = false
            }
        }
    }
}
