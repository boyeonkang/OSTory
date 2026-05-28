package com.example.ostory.domain.model

data class PreferenceAnalysis(
    val totalCount: Int,
    val averageRating: Double,
    val preferredGenres: List<Pair<String, Int>>,
    val musicKeywords: List<String>,
    val summary: String,
    val recommendedTitles: List<String>
)
