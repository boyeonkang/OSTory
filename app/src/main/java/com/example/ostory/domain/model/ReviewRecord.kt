package com.example.ostory.domain.model

data class ReviewRecord(
    val id: Int,
    val workId: Int,
    val workType: WorkType,
    val watchedDate: String,
    val rating: Int,
    val comment: String,
    val posterPath: String? = null,
    val titleKo: String? = null,
    val titleEn: String? = null
)
