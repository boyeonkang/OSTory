package com.example.ostory.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ostory.data.repository.WorkRepository
import com.example.ostory.domain.model.Work
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: WorkRepository = WorkRepository()
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Work>>(emptyList())
    val results: StateFlow<List<Work>> = _results.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun search() {
        val currentQuery = _query.value.trim()
        if (currentQuery.isEmpty()) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _hasSearched.value = true
            try {
                val searchResults = repository.searchWorks(currentQuery)
                _results.value = searchResults
            } catch (e: Exception) {
                _results.value = emptyList()
                _errorMessage.value = "검색 중 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSearch() {
        _query.value = ""
        _results.value = emptyList()
        _isLoading.value = false
        _errorMessage.value = null
        _hasSearched.value = false
    }
}
