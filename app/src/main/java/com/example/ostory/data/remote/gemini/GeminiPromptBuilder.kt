package com.example.ostory.data.remote.gemini

import com.example.ostory.domain.model.ReviewRecord
import com.example.ostory.domain.model.Work

object GeminiPromptBuilder {

    fun buildOstPrompt(title: String, type: String, year: Int): String {
        return """
            작품명: $title
            구분: $type
            제작연도: $year
            
            위 작품의 대표 OST 및 삽입곡(삽입음악) 정보 3~5개를 조사하여 아래 JSON 형식으로만 반환해줘. 마크다운 코드가 아닌 순수 JSON 텍스트로만 반환하고, 다른 텍스트 설명은 포함하지 마.
            
            JSON 구조 예시:
            [
              {
                "title": "곡 제목",
                "artist": "가수/아티스트명",
                "composer": "작곡가명",
                "lyricist": "작사가명",
                "originalArtist": "원곡자명 (리메이크나 기성곡 삽입이 아니면 null)",
                "album": "앨범명",
                "duration": "곡 길이 (예: 3:45)"
              }
            ]
        """.trimIndent()
    }

    fun buildPreferenceAnalysisPrompt(records: List<ReviewRecord>, works: List<Work>): String {
        val recordsText = records.joinToString(separator = "\n") { record ->
            val work = works.find { it.id == record.workId && it.type == record.workType }
            val title = work?.titleKo ?: "알 수 없음"
            val genres = work?.genres?.joinToString { it.name } ?: "없음"
            "작품명: $title (유형: ${record.workType.name}, 장르: $genres), 별점: ${record.rating}/5, 한줄평: ${record.comment}, 감상일: ${record.watchedDate}"
        }

        return """
            사용자의 감상 기록 리스트:
            $recordsText
            
            위 감상 기록을 바탕으로 사용자의 작품 및 음악 취향을 분석하고, 선호하는 장르 랭킹, 음악 취향 키워드, 한글 취향 요약 보고서, 추천 작품 목록을 도출해줘.
            아래 JSON 형식으로만 응답하고 다른 텍스트 설명은 절대 포함하지 마.
            
            JSON 구조 예시:
            {
              "totalCount": ${records.size},
              "averageRating": ${if (records.isNotEmpty()) records.map { it.rating }.average() else 0.0},
              "preferredGenres": [
                {
                  "genre": "장르명",
                  "count": 3
                }
              ],
              "musicKeywords": ["키워드1", "키워드2", "키워드3"],
              "summary": "전반적인 취향 분석 및 음악적 특징에 대한 설명 요약",
              "recommendedTitles": ["추천 작품 제목 1", "추천 작품 제목 2"]
            }
        """.trimIndent()
    }
}
