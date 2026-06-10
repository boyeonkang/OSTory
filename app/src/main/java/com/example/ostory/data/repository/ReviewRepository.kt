package com.example.ostory.data.repository

import android.content.Context
import com.example.ostory.domain.model.ReviewRecord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger

class ReviewRepository private constructor(context: Context?) {
    private val sharedPreferences = context?.getSharedPreferences("ostory_reviews", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val records = mutableListOf<ReviewRecord>()
    private val idCounter = AtomicInteger(1)

    private val _recordsFlow = MutableStateFlow<List<ReviewRecord>>(emptyList())
    val recordsFlow: StateFlow<List<ReviewRecord>> = _recordsFlow.asStateFlow()

    init {
        loadFromPreferences()
    }

    private fun loadFromPreferences() {
        val prefs = sharedPreferences ?: return
        try {
            val json = prefs.getString("reviews_json", null)
            if (!json.isNullOrBlank()) {
                val type = object : TypeToken<List<ReviewRecord>>() {}.type
                val loadedList: List<ReviewRecord> = gson.fromJson(json, type) ?: emptyList()
                records.clear()
                records.addAll(loadedList)
                _recordsFlow.value = records.toList()

                val maxId = records.map { it.id }.maxOrNull() ?: 0
                idCounter.set(maxId + 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveToPreferences() {
        val prefs = sharedPreferences ?: return
        try {
            val json = gson.toJson(records)
            prefs.edit().putString("reviews_json", json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @Volatile
        private var instance: ReviewRepository? = null

        fun initialize(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ReviewRepository(context.applicationContext)
                    }
                }
            }
        }

        fun getInstance(): ReviewRepository {
            return instance ?: synchronized(this) {
                instance ?: ReviewRepository(null).also { instance = it }
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
        saveToPreferences()
    }

    fun updateRecord(record: ReviewRecord) {
        val index = records.indexOfFirst { it.id == record.id }
        if (index != -1) {
            records[index] = record
            _recordsFlow.value = records.toList()
            saveToPreferences()
        }
    }

    fun deleteRecord(recordId: Int) {
        records.removeAll { it.id == recordId }
        _recordsFlow.value = records.toList()
        saveToPreferences()
    }
}
