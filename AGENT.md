# OSTory AGENT.md

## 1. 프로젝트 개요

OSTory는 사용자가 감상한 영화와 드라마를 달력에 포스터 형태로 기록하고, 작품별 대표 OST와 삽입곡 정보를 함께 확인할 수 있는 Android 앱이다.

사용자는 영화 또는 드라마를 검색하고, 작품 상세 정보를 확인한 뒤, 감상 날짜와 별점, 한줄평을 입력하여 감상 기록을 저장할 수 있다. 저장된 기록은 캘린더에 포스터 썸네일로 표시된다.

또한 저장된 감상 기록, 한줄평, 작품 장르, OST 정보를 기반으로 Gemini API를 활용하여 사용자의 작품 취향과 음악 취향을 분석한다. 분석 결과를 바탕으로 사용자가 좋아할 만한 영화나 드라마 추천도 제공한다.

본 프로젝트는 실제 TMDB API와 Gemini API를 활용하는 것을 목표로 한다. 단, API Key는 코드에 직접 작성하지 않고 `local.properties`와 `BuildConfig`를 통해 관리한다.

---

## 2. 개발 우선순위

본 프로젝트의 개발 기준은 다음 순서를 따른다.

1. 기말 프로젝트 제안서를 최우선 기준으로 한다.
2. 요구사항명세서의 기능 요구사항을 두 번째 기준으로 한다.
3. `Design` 폴더의 UI 이미지를 세 번째 기준으로 한다.

OSTory의 핵심 MVP는 다음 세 가지이다.

1. 포스터 기반 감상 캘린더
2. 작품별 대표 OST 및 삽입곡 정보 제공
3. Gemini API 기반 AI 작품·음악 취향 분석

따라서 개발 과정에서는 단순한 더미 앱이 아니라, TMDB API를 활용한 작품 검색 및 상세 조회, Gemini API를 활용한 OST 정보 요약 및 취향 분석 기능을 구현하는 것을 목표로 한다.

다만 실제 음원 재생, 음악 스트리밍, Spotify 또는 YouTube Music 연동, 음악 인식 기능은 구현 범위에서 제외한다.

---

## 3. 프로젝트 기본 정보

| 항목          | 값                                                                   |
| ------------- | -------------------------------------------------------------------- |
| 앱 이름       | OSTory                                                               |
| 언어          | Kotlin                                                               |
| UI            | Jetpack Compose + Material 3                                         |
| 최소 SDK      | API 26                                                               |
| 패키지명      | com.example.ostory                                                   |
| 아키텍처      | domain / data / presentation 기반의 단순 Clean Architecture          |
| 작품 정보 API | TMDB API                                                             |
| AI 분석 API   | Google Gemini API                                                    |
| 데이터 저장   | 감상 기록은 앱 내부 로컬 저장소를 사용하여 앱 재실행 후에도 유지한다 |
| API Key 관리  | local.properties + BuildConfig                                       |

---

## 4. 핵심 의존성

프로젝트에는 다음 라이브러리를 사용한다.

```kotlin
// Network
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Image loading
implementation("io.coil-kt:coil-compose:2.7.0")

// Navigation
implementation("androidx.navigation:navigation-compose:2.8.5")

// Lifecycle / ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

// Material Icons
implementation("androidx.compose.material:material-icons-extended")
```

이미 Android Studio 기본 템플릿에 포함된 Compose BOM, Compose UI, Material 3, Activity Compose 의존성은 중복 추가하지 않는다.

---

## 5. 사용 도구 및 규칙

Antigravity에서 다음 도구를 활용한다.

* `mobile-android-design`: Jetpack Compose와 Material 3 기반 UI 구현
* `android-clean-architecture`: domain, data, presentation 계층 분리
* `sequential-thinking`: 복잡한 작업을 단계별로 나누어 수행

개발 규칙은 다음과 같다.

* TMDB API를 사용하여 영화와 드라마의 제목, 포스터, 줄거리, 장르, 평점 정보를 가져온다.
* Gemini API를 사용하여 작품별 대표 OST 정보 요약, 감상 기록 기반 취향 분석, 작품 추천 결과를 생성한다.
* API Key는 절대 Kotlin 코드에 직접 작성하지 않는다.
* `local.properties`에 API Key를 저장하고, Gradle에서 `BuildConfig`로 접근한다.
* `local.properties`는 GitHub에 업로드하지 않는다.
* 음악 스트리밍, 실제 음원 재생, Spotify 또는 YouTube Music 연동, 음악 인식 기능은 구현하지 않는다.
* OST 정보는 작품 상세 화면과 감상 기록 상세 화면에서 텍스트 정보로 제공한다.
* Gemini API 요청 한도 초과 또는 네트워크 오류가 발생한 경우 앱이 종료되지 않도록 처리하고 사용자에게 안내 문구를 표시한다.

---

## 6. API Key 관리 방식

프로젝트의 `local.properties` 파일에는 다음 값을 사용한다.

```properties
TMDB_API_KEY=여기에_TMDB_API_KEY_입력
GEMINI_API_KEY=여기에_GEMINI_API_KEY_입력
```

앱 코드에서는 다음과 같이 접근할 수 있도록 Gradle 설정을 구성한다.

```kotlin
BuildConfig.TMDB_API_KEY
BuildConfig.GEMINI_API_KEY
```

주의 사항:

* 실제 API Key 값은 GitHub에 올라가면 안 된다.
* `local.properties`는 `.gitignore`에 포함되어야 한다.
* 코드, README, 주석, 커밋 메시지에 실제 API Key를 작성하지 않는다.
* OkHttp 로그에 API Key가 노출되지 않도록 최종 제출 전에는 로그 레벨을 적절히 조정한다.

---

## 7. 핵심 기능

OSTory 앱은 사용자가 작성한 요구사항명세서의 모든 기능 요구사항을 구현 대상으로 한다.
단, 개발은 기능별로 단계적으로 진행하며, 각 기능은 TMDB API, Gemini API, 앱 내부 로컬 저장 구조를 조합하여 구현한다.

### 7.1 작품 검색

* 사용자는 영화 또는 드라마 제목을 검색할 수 있다.
* 검색은 TMDB API를 통해 수행한다.
* 검색 결과에는 작품 포스터, 한글 제목, 원제 또는 영어 제목, 작품 유형, 연도, 장르 정보를 표시한다.
* 사용자는 검색 결과 중 하나를 선택하여 작품 상세 화면으로 이동할 수 있다.
* 검색 결과가 없을 경우 “검색 결과가 없습니다.”라는 안내 문구를 표시한다.

### 7.2 작품 상세 정보 조회

* 사용자는 선택한 영화 또는 드라마의 상세 정보를 확인할 수 있다.
* 작품 상세 정보는 TMDB API를 통해 가져온다.
* 작품 상세 화면에는 포스터, 제목, 원제 또는 영어 제목, 작품 유형, 연도, 장르, 줄거리, 평점 정보를 표시한다.
* 정보가 없는 항목은 빈 화면으로 두지 않고 안내 문구를 표시한다.
* 상세 화면 하단에는 “감상 기록 남기기” 버튼을 표시한다.

### 7.3 감상 기록 등록

* 사용자는 선택한 작품을 감상 기록으로 등록할 수 있다.
* 감상 기록에는 작품 정보, 감상 날짜, 별점, 한줄평이 포함된다.
* 필수 입력값인 별점과 한줄평이 누락되면 저장되지 않고 안내 메시지를 표시한다.
* 저장된 감상 기록은 캘린더 화면과 감상 기록 상세 화면에서 확인할 수 있다.
* 감상 기록은 앱 내부 로컬 저장소에 저장하여 앱을 종료한 뒤 다시 실행해도 유지되도록 구현한다.
* 향후 필요에 따라 DataStore 또는 Room으로 확장할 수 있는 구조를 고려한다.

### 7.4 포스터 기반 감상 캘린더 조회

* 사용자는 캘린더 화면에서 날짜별 감상 기록을 확인할 수 있다.
* 감상 기록이 있는 날짜에는 해당 작품의 포스터 썸네일을 표시한다.
* 사용자가 날짜를 선택하면 해당 날짜에 저장된 감상 기록을 확인할 수 있다.
* 사용자는 월 단위로 이전 달과 다음 달을 이동할 수 있다.
* 캘린더 홈 화면은 `Design/01-calendar-home.png`를 기준으로 구현한다.

### 7.5 OST 및 삽입곡 정보 조회

* 사용자는 작품 상세 화면과 감상 기록 상세 화면에서 작품별 대표 OST와 삽입곡 정보를 확인할 수 있다.
* 각 음악 정보에는 곡명, 가수, 작곡가, 작사가, 원곡자, 앨범 또는 발매 정보를 표시할 수 있는 구조를 둔다.
* 리메이크곡이나 기존 곡이 삽입곡으로 사용된 경우 원곡 정보와 사용된 버전 정보를 구분하여 표시할 수 있는 구조로 작성한다.
* OST 정보는 TMDB API에서 직접 제공하지 않을 수 있으므로, Gemini API를 활용하여 대표 OST와 삽입곡 정보를 요약하거나, 작품 상세 조회 이후 별도 데이터 구조로 제공한다.
* OST 정보가 없는 경우 “등록된 OST 정보가 없습니다.”라는 안내 문구를 표시한다.
* Gemini API 요청 한도 초과 또는 네트워크 오류로 OST 정보를 불러오지 못한 경우, 실제 OST가 없는 경우와 구분되는 안내 문구를 표시한다.
* 실제 음원 재생, 음악 스트리밍, Spotify 또는 YouTube Music 연동, 음악 인식 기능은 구현하지 않는다.

### 7.6 AI 작품·음악 취향 분석

* 시스템은 사용자가 저장한 감상 기록, 별점, 한줄평, 작품 장르, OST 정보를 기반으로 취향을 분석한다.
* AI 분석은 Gemini API를 사용한다.
* 분석 결과에는 총 관람작 수, 평균 별점, 선호 장르, 작품 취향 키워드, 음악 취향 키워드, 취향 요약 문장을 포함한다.
* 감상 기록이 부족한 경우 분석 결과 대신 더 많은 기록이 필요하다는 안내 문구를 표시한다.
* Gemini API 요청 한도 초과 또는 네트워크 오류가 발생한 경우 앱이 종료되지 않고 사용자에게 안내 문구를 표시한다.
* AI 취향 분석 화면은 `Design/08-ai-analysis.png`의 분위기를 참고하되, 실제 구현에서는 카드와 chip 중심의 결과 화면을 사용한다.

### 7.7 작품 추천

* 시스템은 Gemini API 분석 결과를 바탕으로 사용자가 좋아할 만한 영화나 드라마를 추천한다.
* 추천 결과에는 작품 제목, 추천 이유, 관련 장르 또는 음악 취향 키워드를 표시한다.
* 추천 결과가 없을 경우 감상 기록을 추가하라는 안내 문구를 표시한다.
* 추천 기능은 복잡한 추천 알고리즘이 아니라, Gemini API의 취향 분석 결과를 기반으로 한 간단한 추천 수준으로 구현한다.

---

## 8. 데이터 모델

다음 모델을 `domain/model` 패키지에 구현한다.

```kotlin
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

enum class WorkType {
    MOVIE,
    DRAMA
}

data class Genre(
    val id: Int,
    val name: String
)

data class OstTrack(
    val title: String,
    val artist: String,
    val composer: String,
    val lyricist: String,
    val originalArtist: String?,
    val album: String,
    val duration: String
)

data class ReviewRecord(
    val id: Int,
    val workId: Int,
    val workType: WorkType,
    val watchedDate: String,
    val rating: Int,
    val comment: String
)

data class PreferenceAnalysis(
    val totalCount: Int,
    val averageRating: Double,
    val preferredGenres: List<Pair<String, Int>>,
    val musicKeywords: List<String>,
    val summary: String,
    val recommendedTitles: List<String>
)
```

실제 구현 과정에서 화면 요구사항에 따라 분석 결과 모델이나 Gemini 응답 DTO는 위 구조와 다르게 세분화될 수 있다. 단, domain 모델과 API DTO는 분리하여 관리한다.

---

## 9. 패키지 구조

Android 프로젝트 내부의 패키지 구조는 다음을 따른다.

```text
app/src/main/java/com/example/ostory/
├─ domain/
│  └─ model/
├─ data/
│  ├─ remote/
│  │  ├─ tmdb/
│  │  └─ gemini/
│  ├─ repository/
│  └─ local/
├─ presentation/
│  ├─ navigation/
│  ├─ calendar/
│  ├─ search/
│  ├─ detail/
│  ├─ review/
│  └─ preference/
└─ ui/
   └─ theme/
```

---

## 10. TMDB API 구조

TMDB API 관련 코드는 `data/remote/tmdb` 패키지에 둔다.

필요한 파일 예시는 다음과 같다.

* `TmdbApiService.kt`
* `TmdbClient.kt`
* `TmdbDto.kt`
* `TmdbMapper.kt`

필요한 기능은 다음과 같다.

* 영화 검색
* 드라마 검색
* 영화 상세 조회
* 드라마 상세 조회
* 포스터 경로 처리

TMDB 이미지 URL은 다음 형식을 기준으로 한다.

```text
https://image.tmdb.org/t/p/w500/{posterPath}
```

TMDB API 응답 DTO는 앱 내부 모델인 `Work`로 변환하여 presentation 계층에 전달한다.

---

## 11. Gemini API 구조

Gemini API 관련 코드는 `data/remote/gemini` 패키지에 둔다.

필요한 파일 예시는 다음과 같다.

* `GeminiApiService.kt`
* `GeminiClient.kt`
* `GeminiDto.kt`
* `GeminiPromptBuilder.kt`

Gemini API는 다음 정보를 입력으로 사용할 수 있다.

* 사용자가 저장한 작품 제목
* 작품 장르
* 별점
* 한줄평
* OST 목록
* 감상 날짜

Gemini API는 다음 결과를 반환하도록 구성한다.

* 작품별 대표 OST 및 삽입곡 정보
* 취향 요약
* 선호 장르
* 작품 취향 키워드
* 음악 취향 키워드
* 추천 작품 제목
* 추천 이유

Gemini 응답이 실패하거나 비어 있는 경우 기본 안내 문구를 표시한다. 또한 Gemini API 요청 한도 초과 또는 네트워크 오류가 발생한 경우 앱이 종료되지 않도록 처리하고, 사용자에게 적절한 안내 문구를 표시한다.

---

## 12. Repository 구조

`data/repository` 패키지에 다음 Repository를 둔다.

### 12.1 WorkRepository

TMDB API를 통해 작품 검색과 상세 정보를 가져온다.

필요한 함수:

```kotlin
suspend fun searchWorks(query: String): List<Work>
suspend fun getWorkDetail(id: Int, type: WorkType): Work?
```

### 12.2 ReviewRepository

사용자의 감상 기록을 저장하고 조회한다.

필요한 함수:

```kotlin
fun getRecords(): List<ReviewRecord>
fun getRecordById(recordId: Int): ReviewRecord?
fun addRecord(record: ReviewRecord)
fun deleteRecord(recordId: Int)
```

ReviewRepository는 앱 내부 로컬 저장소를 통해 감상 기록을 저장하고, 앱 재실행 후에도 저장된 기록을 복원할 수 있어야 한다.

### 12.3 PreferenceRepository

Gemini API를 통해 사용자의 취향 분석 결과를 가져온다.

필요한 함수:

```kotlin
suspend fun analyzePreference(
    records: List<ReviewRecord>,
    works: List<Work>
): PreferenceAnalysis
```

Gemini API 요청 실패, 응답 파싱 실패, 요청 한도 초과 상황을 안전하게 처리하고, UI 계층에서 적절한 안내 문구를 표시할 수 있도록 오류 상태를 전달한다.

---

## 13. 구현 Phase

본 프로젝트는 세 단계로 구현한다.

### Phase 1 — TMDB API 기반 작품 검색 및 상세 조회

* TMDB API Key를 `local.properties`와 `BuildConfig`로 관리한다.
* Retrofit과 OkHttp를 사용하여 TMDB API에 연결한다.
* 영화와 드라마를 검색할 수 있다.
* 검색 결과에는 포스터, 제목, 원제, 작품 유형, 연도 정보를 표시한다.
* 작품 상세 화면에는 포스터, 제목, 장르, 줄거리, 평점 정보를 표시한다.
* 상세 화면에는 “감상 기록 남기기” 버튼을 제공한다.

### Phase 2 — 감상 기록 저장 및 포스터 기반 캘린더

* 사용자는 작품 상세 화면에서 감상 기록을 작성할 수 있다.
* 감상 기록에는 감상 날짜, 별점, 한줄평을 포함한다.
* 저장된 감상 기록은 앱 내부 로컬 저장소에 저장된다.
* 저장된 감상 기록은 앱 재실행 후에도 유지된다.
* 저장된 감상 기록은 캘린더 화면에 포스터 썸네일로 표시된다.
* 날짜를 선택하면 해당 날짜의 감상 기록을 확인할 수 있다.
* 감상 기록 상세 화면에서 별점, 한줄평, OST 정보를 확인할 수 있다.

### Phase 3 — Gemini API 기반 OST 정보 및 AI 취향 분석

* Gemini API를 활용하여 작품별 대표 OST와 삽입곡 정보를 요약한다.
* 각 OST 정보에는 곡명, 가수, 작곡가, 작사가, 원곡자, 앨범 정보를 포함할 수 있는 구조를 둔다.
* 사용자의 감상 기록, 한줄평, 작품 장르, OST 정보를 Gemini API로 분석한다.
* 분석 결과에는 선호 장르, 작품 취향 키워드, 음악 취향 키워드, 취향 요약, 추천 작품을 포함한다.
* Gemini API 요청 한도 초과 또는 네트워크 오류가 발생한 경우 사용자에게 안내 문구를 표시한다.
* 실제 음원 재생, 음악 스트리밍, 음악 인식 기능은 구현하지 않는다.

---

## 14. 디자인 원칙

전체 UI는 `Design` 폴더의 화면 이미지를 따른다.

* 배경은 흰색을 기본으로 한다.
* 전체적으로 깔끔하고 여백이 넓은 모바일 앱 스타일을 사용한다.
* 주요 강조색은 보라색 계열을 사용한다.
* 캘린더 선택 날짜는 파란색 원형 배경으로 표시한다.
* 일요일은 빨간색, 토요일은 파란색으로 표시한다.
* 카드와 입력창은 연한 회색 배경과 둥근 모서리를 사용한다.
* 포스터 이미지는 둥근 모서리로 표시한다.
* 하단 내비게이션은 캘린더, 검색, 취향 3개 탭으로 구성한다.
* 별점은 노란색 별 아이콘으로 표시한다.
* 제출용 화면에는 테스트, 더미, placeholder, TODO 등 개발 중 임시 문구를 노출하지 않는다.

참고 화면은 다음과 같다.

* `Design/01-calendar-home.png`
* `Design/02-review-detail.png`
* `Design/03-search.png`
* `Design/04-search-result.png`
* `Design/05-work-detail.png`
* `Design/06-review-write.png`
* `Design/07-review-saved.png`
* `Design/08-ai-analysis.png`

---

## 15. 화면별 구현 기준

### 15.1 캘린더 홈 화면

* 상단에 앱 이름 “OSTory”를 표시한다.
* 가운데에 현재 월을 표시한다.
* 좌우 화살표로 이전 달, 다음 달 이동이 가능해야 한다.
* 요일은 일, 월, 화, 수, 목, 금, 토 순서로 표시한다.
* 감상 기록이 있는 날짜에는 포스터 썸네일을 표시한다.
* 선택된 날짜는 파란색 원으로 강조한다.
* 하단에는 캘린더, 검색, 취향 탭을 표시한다.

### 15.2 작품 검색 화면

* 상단에 닫기 버튼과 검색창을 표시한다.
* 검색창 placeholder는 “영화나 드라마 제목을 검색하세요”로 한다.
* 검색 결과에는 포스터, 한글 제목, 영어 제목 또는 원제, 작품 유형, 연도를 표시한다.
* 검색 결과가 없으면 안내 문구를 표시한다.
* 검색 결과를 누르면 작품 상세 화면으로 이동한다.
* 사용자에게 보이는 화면에는 테스트 렌더링 확인 등 개발 중 임시 문구를 표시하지 않는다.

### 15.3 작품 상세 화면

* 상단에 뒤로가기 버튼을 표시한다.
* 큰 포스터 이미지를 표시한다.
* 제목, 영어 제목 또는 원제, 작품 유형, 연도, 장르, 줄거리, 평점, OST 목록을 표시한다.
* 장르는 TMDB에서 받은 값을 임의로 변경하지 않고 표시한다.
* 평점은 숫자 평점과 별 아이콘 표시가 서로 일치하도록 구성한다.
* 하단에는 보라색 “감상 기록 남기기” 버튼을 표시한다.

### 15.4 감상 기록 작성 화면

* 상단 제목은 “감상 기록”으로 한다.
* 선택한 작품의 포스터, 제목, 영어 제목 또는 원제, 감상 날짜를 표시한다.
* 별점 5개를 선택할 수 있게 한다.
* 한줄평 입력창을 제공한다.
* 글자 수 카운터를 표시한다.
* 별점과 한줄평이 입력되어야 저장 버튼이 활성화된다.

### 15.5 감상 기록 상세 화면

* 저장된 작품의 포스터, 제목, 영어 제목 또는 원제, 감상 날짜를 표시한다.
* 사용자가 입력한 별점과 한줄평을 표시한다.
* OST 목록을 표시한다.
* 상단 오른쪽에는 삭제 아이콘을 표시한다.

### 15.6 AI 취향 분석 화면

* 총 관람작 수를 표시한다.
* 평균 별점을 표시한다.
* 선호 장르를 카드와 chip 형태로 표시한다.
* 작품 취향 키워드와 음악 취향 키워드를 카드와 chip 형태로 표시한다.
* Gemini API가 생성한 취향 요약을 표시한다.
* 추천 작품과 추천 이유를 표시한다.
* Gemini API 요청 한도 초과 또는 네트워크 오류가 발생한 경우 사용자에게 안내 문구를 표시한다.
* AI 분석 결과가 없는 경우에도 앱이 종료되지 않고 빈 상태 또는 안내 문구를 표시한다.

---

## 16. 구현 순서

1. 프로젝트 생성 + 의존성 + API Key 설정 + 빌드 확인
2. 데이터 모델 + 패키지 구조 생성
3. TMDB API Service / Client / DTO / Mapper 구현
4. 작품 검색 화면 구현
5. 작품 상세 화면 구현
6. Gemini API 기반 OST 정보 표시 구조 구현
7. 감상 기록 작성 화면 구현
8. 감상 기록 저장소 구현
9. 포스터 기반 캘린더 화면 구현
10. 감상 기록 상세 화면 구현
11. Gemini API Service / Prompt / Repository 구현
12. AI 취향 분석 화면 구현
13. UI 마감
14. 빌드 오류 수정
15. GitHub 업로드

---

## 17. 코딩 규칙

* 함수명과 변수명은 영어를 사용한다.
* 사용자에게 보이는 UI 문구는 한국어를 사용해도 된다.
* 하나의 Composable 함수가 너무 길어지지 않도록 화면 내부 컴포넌트를 분리한다.
* 화면과 데이터 모델을 분리한다.
* API DTO와 앱 내부 domain model을 분리한다.
* API 호출은 repository 계층에서 처리한다.
* UI에서 API Key에 직접 접근하지 않는다.
* API 실패, 네트워크 오류, 검색 결과 없음 상태를 처리한다.
* Gemini API 요청 한도 초과 또는 응답 파싱 실패 상황을 안전하게 처리한다.
* 실제 API Key는 코드에 직접 작성하지 않는다.
* local.properties는 GitHub에 업로드하지 않는다.
* 사용자에게 보이는 UI에는 테스트, 더미, placeholder, TODO 등 개발 중 임시 문구를 남기지 않는다.
* 이미 정상 동작하는 기능을 수정할 때는 관련 기능만 최소 범위로 수정하고, 검색, 캘린더, 감상 기록, OST, AI 분석 등 다른 기능을 임의로 변경하지 않는다.
* 빌드가 가능한 상태를 유지한다.
