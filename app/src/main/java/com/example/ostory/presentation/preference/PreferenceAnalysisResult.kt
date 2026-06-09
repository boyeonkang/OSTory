package com.example.ostory.presentation.preference

data class PreferenceAnalysisResult(
    val preferredGenres: List<PreferredGenre> = emptyList(),
    val musicKeywords: List<MusicKeyword> = emptyList(),
    val recommendations: List<RecommendedWork> = emptyList(),
    val summary: String = ""
)

data class PreferredGenre(
    val name: String,
    val reason: String
)

data class MusicKeyword(
    val keyword: String,
    val reason: String
)

data class RecommendedWork(
    val title: String,
    val reason: String
)
