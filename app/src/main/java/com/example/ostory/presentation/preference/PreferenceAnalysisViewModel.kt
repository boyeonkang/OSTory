package com.example.ostory.presentation.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ostory.data.repository.ReviewRepository
import com.example.ostory.domain.model.ReviewRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PreferenceAnalysisViewModel(
    private val reviewRepository: ReviewRepository = ReviewRepository.getInstance()
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
}
