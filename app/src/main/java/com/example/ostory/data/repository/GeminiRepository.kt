package com.example.ostory.data.repository

import com.example.ostory.BuildConfig
import com.example.ostory.data.remote.gemini.GeminiClient
import com.example.ostory.data.remote.gemini.GeminiPromptBuilder
import com.example.ostory.data.remote.gemini.GeminiRequest
import com.example.ostory.domain.model.OstTrack
import com.example.ostory.domain.model.Work
import com.example.ostory.domain.model.ReviewRecord
import com.example.ostory.presentation.preference.PreferenceAnalysisResult
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository {
    private val apiService = GeminiClient.service
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val gson = Gson()

    data class GeminiOstResponse(
        @SerializedName("ostList") val ostList: List<OstTrack>?
    )

    suspend fun getOstInfo(work: Work): List<OstTrack> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || 
            apiKey == "YOUR_API_KEY" || 
            apiKey == "여기에_GEMINI_API_KEY_입력" || 
            apiKey == "placeholder" || 
            apiKey == "null"
        ) {
            return@withContext emptyList()
        }

        try {
            val prompt = GeminiPromptBuilder.buildOstPrompt(work)
            val request = GeminiRequest(
                contents = listOf(
                    GeminiRequest.Content(
                        parts = listOf(
                            GeminiRequest.Part(text = prompt)
                        )
                    )
                ),
                generationConfig = GeminiRequest.GenerationConfig(
                    responseMimeType = "application/json"
                )
            )

            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return@withContext emptyList()

            val cleanJson = cleanJsonString(jsonText)
            val ostResponse = try {
                gson.fromJson(cleanJson, GeminiOstResponse::class.java)
            } catch (e: Exception) {
                null
            }
            val rawList = ostResponse?.ostList ?: emptyList()

            val filteredList = rawList.filter { track ->
                val title = track.title?.trim() ?: ""
                val artist = track.artist?.trim() ?: ""
                title.isNotEmpty() && title != "알 수 없음" &&
                artist.isNotEmpty() && artist != "알 수 없음"
            }.distinctBy { 
                "${it.title?.trim()?.lowercase()}_${it.artist?.trim()?.lowercase()}"
            }.take(3)

            filteredList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun cleanJsonString(text: String): String {
        var clean = text.trim()
        if (clean.startsWith("```")) {
            clean = clean.removePrefix("```json").removePrefix("```").trim()
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```").trim()
        }
        return clean
    }

    suspend fun analyzePreference(records: List<ReviewRecord>): PreferenceAnalysisResult = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || 
            apiKey == "YOUR_API_KEY" || 
            apiKey == "여기에_GEMINI_API_KEY_입력" || 
            apiKey == "placeholder" || 
            apiKey == "null" ||
            records.isEmpty()
        ) {
            return@withContext PreferenceAnalysisResult()
        }

        try {
            val prompt = GeminiPromptBuilder.buildPreferenceAnalysisPrompt(records)
            val request = GeminiRequest(
                contents = listOf(
                    GeminiRequest.Content(
                        parts = listOf(
                            GeminiRequest.Part(text = prompt)
                        )
                    )
                ),
                generationConfig = GeminiRequest.GenerationConfig(
                    responseMimeType = "application/json"
                )
            )

            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return@withContext PreferenceAnalysisResult()

            val cleanJson = cleanJsonString(jsonText)
            val analysisResult = try {
                gson.fromJson(cleanJson, PreferenceAnalysisResult::class.java)
            } catch (e: Exception) {
                null
            }
            
            analysisResult ?: PreferenceAnalysisResult()
        } catch (e: Exception) {
            e.printStackTrace()
            PreferenceAnalysisResult()
        }
    }
}
