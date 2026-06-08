package com.example.ostory.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ostory.data.repository.WorkRepository
import com.example.ostory.data.repository.GeminiRepository
import com.example.ostory.domain.model.Work
import com.example.ostory.domain.model.WorkType
import com.example.ostory.domain.model.OstTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkDetailViewModel(
    private val repository: WorkRepository = WorkRepository(),
    private val geminiRepository: GeminiRepository = GeminiRepository()
) : ViewModel() {

    private val _work = MutableStateFlow<Work?>(null)
    val work: StateFlow<Work?> = _work.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _ostList = MutableStateFlow<List<OstTrack>>(emptyList())
    val ostList: StateFlow<List<OstTrack>> = _ostList.asStateFlow()

    private val _isOstLoading = MutableStateFlow(false)
    val isOstLoading: StateFlow<Boolean> = _isOstLoading.asStateFlow()

    private val _isOstLoaded = MutableStateFlow(false)
    val isOstLoaded: StateFlow<Boolean> = _isOstLoaded.asStateFlow()

    fun loadWorkDetail(workId: Int, workType: String) {
        val type = when (workType) {
            "movie" -> WorkType.MOVIE
            "drama", "tv" -> WorkType.DRAMA
            else -> {
                _errorMessage.value = "알 수 없는 작품 유형입니다."
                return
            }
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isOstLoaded.value = false
            _ostList.value = emptyList()

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

    fun fetchOst() {
        val currentWork = _work.value ?: return
        if (_isOstLoading.value) return

        viewModelScope.launch {
            _isOstLoading.value = true
            try {
                val ost = geminiRepository.getOstInfo(currentWork)
                _ostList.value = ost
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