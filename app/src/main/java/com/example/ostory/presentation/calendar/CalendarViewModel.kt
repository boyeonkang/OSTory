package com.example.ostory.presentation.calendar

import androidx.lifecycle.ViewModel
import com.example.ostory.data.repository.ReviewRepository
import com.example.ostory.domain.model.ReviewRecord
import kotlinx.coroutines.flow.StateFlow

class CalendarViewModel(
    private val reviewRepository: ReviewRepository = ReviewRepository.getInstance()
) : ViewModel() {
    val recordsFlow: StateFlow<List<ReviewRecord>> = reviewRepository.recordsFlow
    fun getRecords(): List<ReviewRecord> = reviewRepository.getRecords()
}
