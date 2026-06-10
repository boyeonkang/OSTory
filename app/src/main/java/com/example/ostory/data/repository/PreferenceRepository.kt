package com.example.ostory.data.repository

import android.content.Context
import com.example.ostory.presentation.preference.PreferenceAnalysisResult
import com.example.ostory.BuildConfig
import com.example.ostory.data.remote.gemini.GeminiClient
import com.example.ostory.data.remote.gemini.GeminiPromptBuilder
import com.example.ostory.data.remote.gemini.GeminiRequest
import com.example.ostory.domain.model.OstTrack
import com.example.ostory.domain.model.PreferenceAnalysis
import com.example.ostory.domain.model.ReviewRecord
import com.example.ostory.domain.model.Work
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PreferenceRepository {
    private val apiService = GeminiClient.service
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val gson = Gson()

    fun saveAnalysisResult(context: Context, result: PreferenceAnalysisResult) {
        try {
            val sharedPreferences = context.getSharedPreferences("ostory_preference", Context.MODE_PRIVATE)
            val json = gson.toJson(result)
            sharedPreferences.edit().putString("preference_analysis_json", json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadAnalysisResult(context: Context): PreferenceAnalysisResult? {
        try {
            val sharedPreferences = context.getSharedPreferences("ostory_preference", Context.MODE_PRIVATE)
            val json = sharedPreferences.getString("preference_analysis_json", null)
            if (!json.isNullOrBlank()) {
                return gson.fromJson(json, PreferenceAnalysisResult::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    suspend fun analyzePreference(
        records: List<ReviewRecord>,
        works: List<Work>
    ): PreferenceAnalysis = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "여기에_GEMINI_API_KEY_입력" || records.isEmpty()) {
            return@withContext getPlaceholderAnalysis(records)
        }

        try {
            val prompt = GeminiPromptBuilder.buildPreferenceAnalysisPrompt(records, works)
            val request = GeminiRequest(
                contents = listOf(
                    GeminiRequest.Content(
                        parts = listOf(GeminiRequest.Part(text = prompt))
                    )
                ),
                generationConfig = GeminiRequest.GenerationConfig(responseMimeType = "application/json")
            )

            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (!jsonText.isNullOrBlank()) {
                val rawAnalysis: RawPreferenceAnalysis = gson.fromJson(jsonText, RawPreferenceAnalysis::class.java)
                val genres = rawAnalysis.preferredGenres?.map { Pair(it.genre ?: "기타", it.count ?: 0) } ?: emptyList()
                
                PreferenceAnalysis(
                    totalCount = rawAnalysis.totalCount ?: records.size,
                    averageRating = rawAnalysis.averageRating ?: records.map { it.rating }.average(),
                    preferredGenres = genres,
                    musicKeywords = rawAnalysis.musicKeywords ?: emptyList(),
                    summary = rawAnalysis.summary ?: "취향 분석 정보를 불러올 수 없습니다.",
                    recommendedTitles = rawAnalysis.recommendedTitles ?: emptyList()
                )
            } else {
                getPlaceholderAnalysis(records)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getPlaceholderAnalysis(records)
        }
    }

    suspend fun fetchOstListForWork(title: String, type: String, year: Int): List<OstTrack> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "여기에_GEMINI_API_KEY_입력") {
            return@withContext emptyList()
        }

        try {
            val prompt = GeminiPromptBuilder.buildOstPrompt(title, type, year)
            val request = GeminiRequest(
                contents = listOf(
                    GeminiRequest.Content(
                        parts = listOf(GeminiRequest.Part(text = prompt))
                    )
                ),
                generationConfig = GeminiRequest.GenerationConfig(responseMimeType = "application/json")
            )

            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (!jsonText.isNullOrBlank()) {
                val ostType = object : TypeToken<List<OstTrack>>() {}.type
                gson.fromJson<List<OstTrack>>(jsonText, ostType) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun getPlaceholderAnalysis(records: List<ReviewRecord>): PreferenceAnalysis {
        return PreferenceAnalysis(
            totalCount = records.size,
            averageRating = if (records.isNotEmpty()) records.map { it.rating }.average() else 0.0,
            preferredGenres = listOf(Pair("감상 기록 부족", 0)),
            musicKeywords = listOf("분석 필요"),
            summary = "감상 기록이 부족하거나 API 설정이 되어 있지 않아 취향 분석 결과를 생성할 수 없습니다. 더 많은 작품을 기록해 주세요!",
            recommendedTitles = emptyList()
        )
    }

    private data class RawPreferenceAnalysis(
        val totalCount: Int?,
        val averageRating: Double?,
        val preferredGenres: List<RawGenreCount>?,
        val musicKeywords: List<String>?,
        val summary: String?,
        val recommendedTitles: List<String>?
    )

    private data class RawGenreCount(
        val genre: String?,
        val count: Int?
    )
}
