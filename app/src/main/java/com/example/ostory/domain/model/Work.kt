package com.example.ostory.domain.model

data class Work(
    val id: Int,
    val titleKo: String,
    val titleEn: String,
    val type: WorkType,
    val year: Int,
    val posterPath: String?,
    val genres: List<Genre>,
    val plot: String,
    val rating: Double,
    val ostList: List<OstTrack>
)
