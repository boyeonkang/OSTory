package com.example.ostory.domain.model

data class OstTrack(
    val title: String,
    val artist: String,
    val composer: String,
    val lyricist: String,
    val originalArtist: String?,
    val album: String,
    val duration: String
)
