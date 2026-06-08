package com.example.ostory.data.remote.gemini

import com.example.ostory.domain.model.ReviewRecord
import com.example.ostory.domain.model.Work
import com.example.ostory.domain.model.WorkType

object GeminiPromptBuilder {

    fun buildOstPrompt(work: Work): String {
        val genresText = work.genres.joinToString { it.name }
        return """
            아래 정보를 가진 영화/드라마 작품의 대표 OST 및 삽입곡(삽입음악) 정보를 최대 3개까지만 조사해줘. 가장 대표적인 OST 또는 삽입곡 3개만 우선순위대로 반환해줘.
            
            작품 정보:
            - 한국어 제목: ${work.titleKo}
            - 영어 제목: ${work.titleEn}
            - 구분: ${if (work.type == WorkType.MOVIE) "영화" else "드라마"}
            - 제작연도: ${work.year}년
            - 장르: $genresText
            - 줄거리: ${work.plot}
            
            조사 지침:
            1. 실제로 이 작품에 사용된 대표적인 OST 또는 삽입곡 정보만 수집해줘.
            2. 존재하지 않는 OST를 과하게 단정하거나 지어내지 말아줘.
            3. title 또는 artist에 "알 수 없음"을 값으로 넣지 말아줘.
            4. title 또는 artist를 확실하게 알 수 없거나 존재하지 않는다면 해당 항목은 JSON에 일체 포함하지 말아줘. composer, lyricist, album, duration은 모르면 "알 수 없음"을 사용할 수 있지만, title과 artist는 반드시 실제 값이 있을 때만 항목을 생성해야 해.
            5. 알려진 공식 OST 정보가 존재하지 않거나 불확실한 경우 아래와 같이 빈 배열을 반환해줘.
               {
                 "ostList": []
               }
            6. 아래 제공된 JSON 형식으로만 응답을 생성하고, 마크다운 코드 블록(```json ```)을 포함한 어떠한 부가 설명 텍스트도 절대 포함하지 말고 순수 JSON 문자열만 반환해줘.
            
            JSON 응답 형식:
            {
              "ostList": [
                {
                  "title": "곡명",
                  "artist": "가수",
                  "composer": "작곡가 또는 알 수 없음",
                  "lyricist": "작사가 또는 알 수 없음",
                  "originalArtist": "원곡자 또는 null",
                  "album": "앨범 또는 작품명",
                  "duration": "알 수 없음"
                }
              ]
            }
        """.trimIndent()
    }

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
