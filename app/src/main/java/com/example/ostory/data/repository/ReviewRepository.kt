package com.example.ostory.data.repository

import com.example.ostory.domain.model.ReviewRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger

class ReviewRepository private constructor() {
    private val records = mutableListOf<ReviewRecord>()
    private val idCounter = AtomicInteger(1)

    private val _recordsFlow = MutableStateFlow<List<ReviewRecord>>(emptyList())
    val recordsFlow: StateFlow<List<ReviewRecord>> = _recordsFlow.asStateFlow()

    companion object {
        @Volatile
        private var instance: ReviewRepository? = null

        fun getInstance(): ReviewRepository {
            return instance ?: synchronized(this) {
                instance ?: ReviewRepository().also { instance = it }
            }
        }
    }

    fun getRecords(): List<ReviewRecord> {
        return records.toList()
    }

    fun getRecordById(recordId: Int): ReviewRecord? {
        return records.find { it.id == recordId }
    }

    fun addRecord(record: ReviewRecord) {
        val finalRecord = if (record.id <= 0) {
            record.copy(id = idCounter.getAndIncrement())
        } else {
            records.removeAll { it.id == record.id }
            record
        }
        records.add(finalRecord)
        _recordsFlow.value = records.toList()
    }

    fun deleteRecord(recordId: Int) {
        records.removeAll { it.id == recordId }
        _recordsFlow.value = records.toList()
    }
}
