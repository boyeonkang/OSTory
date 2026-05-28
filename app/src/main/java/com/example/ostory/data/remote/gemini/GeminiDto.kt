package com.example.ostory.data.remote.gemini

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    @SerializedName("contents") val contents: List<Content>,
    @SerializedName("generationConfig") val generationConfig: GenerationConfig? = null
) {
    data class Content(
        @SerializedName("parts") val parts: List<Part>
    )

    data class Part(
        @SerializedName("text") val text: String
    )

    data class GenerationConfig(
        @SerializedName("responseMimeType") val responseMimeType: String? = null
    )
}

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<Candidate>?
) {
    data class Candidate(
        @SerializedName("content") val content: Content?
    )

    data class Content(
        @SerializedName("parts") val parts: List<Part>?
    )

    data class Part(
        @SerializedName("text") val text: String?
    )
}
