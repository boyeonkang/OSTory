# OSTory AGENT.md

## 1. 프로젝트 개요

OSTory는 사용자가 감상한 영화와 드라마를 달력에 포스터 형태로 기록하고, 작품별 대표 OST와 삽입곡 정보를 함께 확인할 수 있는 Android 앱이다.

사용자는 영화 또는 드라마를 검색하고, 작품 상세 정보를 확인한 뒤, 감상 날짜와 별점, 한줄평을 입력하여 감상 기록을 저장할 수 있다. 저장된 기록은 캘린더에 포스터 썸네일로 표시된다.

또한 저장된 감상 기록, 한줄평, 작품 장르, OST 정보를 기반으로 Gemini API를 활용하여 사용자의 작품 취향과 음악 취향을 분석한다. 분석 결과를 바탕으로 사용자가 좋아할 만한 영화나 드라마 추천도 제공한다.

본 프로젝트는 실제 TMDB API와 Gemini API를 연동하여 구현을 완료하였다. API Key는 보안을 위해 코드에 직접 노출하지 않고 `local.properties`와 `BuildConfig`를 통해 안전하게 관리한다.

---

## 2. 개발 우선순위 및 구현 상태

본 프로젝트는 다음 기준을 최우선으로 준수하여 개발되었다.

1. 기말 프로젝트 제안서를 최우선 기준으로 한다.
2. 요구사항명세서의 기능 요구사항을 두 번째 기준으로 한다.
3. `Design` 폴더의 UI 이미지를 세 번째 기준으로 한다.

OSTory의 핵심 MVP는 다음 세 가지이며, 모두 성공적으로 구현되어 활성화 상태이다.

1. **포스터 기반 감상 캘린더:** 날짜별 감상 내역을 포스터 형태로 한눈에 확인하며, 동일 날짜에 다중 기록 추가가 가능하다.
2. **작품별 대표 OST 및 삽입곡 정보 제공:** Gemini API를 사용해 각 영화/드라마의 OST 목록을 동적으로 가져와 로컬에 함께 영구 저장한다.
3. **Gemini API 기반 AI 작품·음악 취향 분석:** 저장된 감상 기록을 분석하여 사용자의 장르별 선호도, 취향 키워드, 그리고 어울리는 콘텐츠 추천 리포트를 생성하고 로컬에 캐싱 및 저장한다.

단, 실제 음원 재생, 음악 스트리밍, Spotify 또는 YouTube Music 연동, 음악 인식 기능은 기획 단계부터 구현 범위에서 명확히 제외되었다.

---

## 3. 프로젝트 기본 정보

| 항목          | 값                                                                                               |
| ------------- | ------------------------------------------------------------------------------------------------ |
| 앱 이름       | OSTory                                                                                           |
| 언어          | Kotlin                                                                                           |
| UI            | Jetpack Compose + Material 3                                                                     |
| 최소 SDK      | API 26                                                                                           |
| 패키지명      | com.example.ostory                                                                               |
| 아키텍처      | domain / data / presentation 기반의 Clean Architecture                                           |
| 작품 정보 API | TMDB API                                                                                         |
| AI 분석 API   | Google Gemini API                                                                                |
| 데이터 저장   | SharedPreferences 기반의 로컬 저장소를 활용하여 앱 재실행 후에도 저장 데이터를 안전하게 유지한다 |
| API Key 관리  | local.properties + BuildConfig                                                                   |

---

## 4. 핵심 의존성

프로젝트에는 다음 라이브러리를 사용하고 있다.

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

Android Studio 기본 템플릿에 포함된 Compose BOM, Compose UI, Material 3, Activity Compose 의존성은 중복되지 않도록 깔끔하게 정돈되어 있다.

---

## 5. 사용 도구 및 개발 규칙

Antigravity 개발 시 다음 도구를 활용하였다.

* `mobile-android-design`: Jetpack Compose와 Material 3 기반의 완성도 높은 반응형 UI 구현
* `android-clean-architecture`: domain, data, presentation 계층의 명확한 역할 분리
* `sequential-thinking`: 요구사항을 단계별로 파악하고 안전하게 구현 및 테스트 진행

핵심 개발 규칙은 다음과 같이 철저히 적용되어 있다.

* TMDB API를 사용하여 영화와 드라마의 제목, 포스터, 줄거리, 장르, 평점 정보를 실시간으로 연동한다.
* Gemini API를 사용하여 작품별 대표 OST 정보 요약, 감상 기록 기반 취향 분석, 작품 추천 결과를 실시간 파싱하고 화면에 전달한다.
* API Key 보안: 절대 Kotlin 소스 코드에 직접 작성하지 않고, `local.properties`에 정의한 뒤 Gradle 컴파일 시점에 `BuildConfig`로 불러와 사용한다.
* `local.properties` 파일은 GitHub 원격 저장소에 업로드되지 않도록 `.gitignore`에 등록되어 있다.
* 음악 스트리밍, 실제 음원 재생, Spotify/YouTube Music 연동, 음악 인식 등 오디오 재생 관련 기능은 구현 범위에서 제외되어 텍스트 정보 제공에 집중한다.
* OST 정보는 작품 상세 화면과 감상 기록 상세 화면에서 직관적인 텍스트 리스트 형태로 제공 및 저장 유지된다.
* Gemini API 요청 한도 초과(Quota Limit) 또는 네트워크 에러 발생 시 앱이 크래시되지 않도록 안전한 예외 처리가 반영되어 있으며, 사용자에게 안내 메시지(Fallback UI)를 표시한다.

---

## 6. API Key 관리 방식

프로젝트의 `local.properties` 파일에는 다음 설정을 적용하여 사용 중이다.

```properties
TMDB_API_KEY=여기에_TMDB_API_KEY_입력
GEMINI_API_KEY=여기에_GEMINI_API_KEY_입력
```

앱 코드에서는 다음과 같이 접근할 수 있도록 Gradle 환경이 구성되어 있다.

```kotlin
BuildConfig.TMDB_API_KEY
BuildConfig.GEMINI_API_KEY
```

**보안 수칙 준수 현황:**

* 실제 API Key 값은 GitHub에 노출되지 않도록 철저히 관리된다.
* `local.properties`는 `.gitignore`에 완전히 포함되어 있다.
* 코드, README, 주석, 커밋 메시지에 실제 API Key 값을 명시하지 않는다.
* OkHttp 로깅 인터셉터는 릴리스 시점에 API Key가 유출되지 않도록 보완 조치되어 있다.

---

## 7. 핵심 기능 및 구현 상태

OSTory 앱은 요구사항명세서의 모든 핵심 기능을 구현 완료하였다. 각 기능은 실시간 API 통신 및 신뢰성 있는 로컬 데이터 캐싱 구조로 결합되어 있다.

### 7.1 온보딩 화면 및 앱 런처 아이콘

* **최초 진입 온보딩:** 앱을 처음 설치하고 실행했을 때, 서비스의 시작을 알리는 소개 화면(`OnboardingScreen`)이 정상 노출된다.
* **비가역성 흐름 제어:** 온보딩 화면에서 "시작하기"를 누르면 메인 화면(캘린더)으로 이동하며 최초 실행 플래그(`isFirstRun`)가 업데이트된다. 이후 앱 재실행 시에는 온보딩이 노출되지 않고 메인으로 직행하며, 메인에서 뒤로가기 동작 시 온보딩 화면으로 돌아가지 않고 앱이 즉시 안전하게 종료된다.
* **앱 아이콘 패키징:** 맞춤형 OSTory 런처 아이콘이 프로젝트에 정상 적용되어 있다.

### 7.2 작품 검색 (TMDB API 연동)

* 영화 및 드라마 제목을 입력하여 실시간으로 통합 검색할 수 있다.
* 검색 결과는 작품 포스터 썸네일, 한글 제목, 원제(또는 영어 제목), 작품 유형(영화/드라마 구분), 개봉/방영 연도 정보를 깔끔한 리스트 형태로 표시한다.
* 검색 결과가 없는 경우 "검색 결과가 없습니다."라는 안내 문구를 노출하여 빈 화면을 보완한다.
* 일반 검색 탭을 통해 검색한 작품을 터치하면 상세 정보 화면으로 전환된다.

### 7.3 작품 상세 정보 조회

* TMDB API로부터 상세 데이터를 받아와 포스터, 제목, 원제, 유형, 연도, 장르 리스트, 줄거리, 평점 정보를 표시한다.
* 장르 정보는 TMDB에서 반환된 오리지널 데이터 그대로 한국어로 정밀 맵핑하여 노출한다.
* 평점 정보는 노란색 별 아이콘 및 수치 텍스트 형태로 시각화하여 사용자가 한눈에 파악할 수 있다.
* 상세 페이지 내에서 Gemini API를 통해 받아온 해당 작품의 대표 OST 및 삽입곡 목록이 표시된다.
* 화면 하단에는 눈에 잘 띄는 디자인의 "감상 기록 남기기" 버튼이 구성되어 있다.

### 7.4 감상 기록 등록 및 수정/삭제

* 작품 상세 화면 혹은 캘린더 날짜 지정 검색을 통해 진입하여 감상 기록을 작성할 수 있다.
* 감상 기록은 해당 작품의 포스터/제목 정보와 더불어 감상 날짜 선택, 별점 부여(1~5점), 한줄평(글자 수 실시간 카운터 포함을 입력받아 저장한다.
* 필수값인 별점과 한줄평이 작성되어야만 저장 버튼이 활성화되도록 유효성 검사가 적용되어 있다.
* 다중 감상 기록 지원: 같은 날짜에 복수의 작품을 기록하는 경우에도 제한 없이 여러 개의 개별 감상 기록을 저장할 수 있다.
* 기록 수정 및 삭제: 감상 기록 상세 화면에서 기록을 간편하게 삭제할 수 있는 휴지통 버튼을 제공하며, 기존 감상 정보의 수정 및 재저장 기능이 완벽하게 지원된다.
* 저장된 기록은 SharedPreferences 기반의 로컬 저장소에 JSON 형태로 직렬화되어 보존되므로 앱을 재시작해도 소실되지 않는다.

### 7.5 포스터 기반 감상 캘린더

* 메인 화면으로, 달력의 날짜 칸 안에 사용자가 감상한 작품의 포스터 썸네일이 채워지는 포스터형 캘린더가 완벽히 동작한다.
* 상단 영역에 현재 연도와 월이 표시되며, 좌우 화살표 버튼을 통해 달을 자유롭게 전환할 수 있다.
* 선택한 날짜는 파란색 원으로 하이라이트된다.
* 특정 날짜를 터치하면 해당 날짜에 저장된 모든 감상 기록 리스트가 바텀 시트 형식으로 미려하게 열린다.
* 바텀 시트 내부에는 새로운 감상 기록을 추가할 수 있는 "+" 버튼이 제공되며, 터치 시 날짜가 자동으로 유지된 채로 작품 검색 화면으로 전환된다.

### 7.6 OST 및 삽입곡 정보 제공 (Gemini API 연동)

* 작품 상세 및 감상 기록 상세 조회 시, 해당 작품의 대표 OST 목록을 Gemini API가 실시간 분석 및 요약하여 반환한다.
* 요약 정보는 곡명, 아티스트, 앨범 등 표준 텍스트 정보 포맷으로 추출되며, 추출된 데이터는 로컬 저장소에 감상 기록과 함께 영구 저장되어 이후 API 트래픽을 최소화한다.
* Gemini API 호출 에러, Quota 한도 초과 또는 네트워크가 끊긴 상황에서도 앱이 꺼지지 않도록 예외(Try-Catch)를 처리하였으며, 해당 영역에 "OST 정보를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요." 등의 Fallback 메시지를 띄워 사용성을 보장한다.

### 7.7 AI 작품 및 음악 취향 분석 & 추천 (Gemini API 연동)

* 사용자가 그동안 누적하여 등록한 감상 기록들의 평점, 한줄평, 영화 장르, OST 데이터를 기반으로 Gemini API가 심층 취향 분석을 수행한다.
* 분석 결과 화면("취향" 탭)에서는 다음 정보를 카드와 칩(Chip) 형태의 세련된 UI로 보여준다.
  - 감상 기록 통계: 총 관람작 수 및 평균 별점 정보
  - 선호 장르 분석: 선호하는 영화/드라마 장르 리스트 및 비율
  - 취향 키워드: 작품 스타일 및 선호 음악 스타일에 대한 키워드 칩
  - 취향 요약 리포트: 사용자의 성향을 재미있게 요약한 맞춤형 요약 문장
  - 추천 작품 정보: 취향에 매칭되는 영화/드라마 추천 목록 및 상세한 추천 사유
* 분석 완료된 리포트는 로컬 저장소에 안전하게 저장 및 보존되어 재진입 시 즉각 로드된다.
* 감상 데이터가 아예 없거나 분석에 필요한 최소 조건(기록 1개 이상)이 안 될 경우, "더 많은 작품을 기록하고 AI 취향 분석을 시작해 보세요!"라는 가이드 화면을 노출한다.

---

## 8. 데이터 모델

다음 핵심 모델들이 `domain/model` 패키지에 구현되어 동작하고 있다.

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

---

## 9. 패키지 구조

Clean Architecture를 지향하는 구조로 완벽히 정돈되어 관리되고 있다.

```text
app/src/main/java/com/example/ostory/
├─ domain/
│  └─ model/              # 핵심 도메인 모델 정의
├─ data/
│  ├─ remote/             # 원격 API 데이터 소스
│  │  ├─ tmdb/            # TMDB API 서비스 및 클라이언트
│  │  └─ gemini/          # Gemini API 서비스 및 프롬프트 빌더
│  ├─ repository/         # Repository 인터페이스 구현체
│  └─ local/              # SharedPreferences 로컬 데이터 저장소
├─ presentation/
│  ├─ navigation/         # NavHost 및 Route 네비게이션 설계
│  ├─ calendar/           # 캘린더 홈 및 일별 기록 조회 바텀시트
│  ├─ search/             # TMDB 실시간 통합 검색 화면
│  ├─ detail/             # 작품 상세 정보 및 대표 OST 조회 화면
│  ├─ review/             # 감상 기록 작성(등록/수정) 및 상세 조회 화면
│  └─ preference/         # Gemini 기반 AI 취향 분석 및 맞춤 추천 화면
└─ ui/
   └─ theme/              # 어플리케이션 테마 및 컬러 디자인 시스템
```

---

## 10. TMDB API 구조

`data/remote/tmdb` 패키지에 구축되어 있으며, Retrofit 서비스를 사용하여 영화와 드라마 정보를 완벽하게 결합한다.

* 구현 파일:
  - `TmdbApiService.kt`: TMDB 엔드포인트 정의 (영화/드라마 검색 및 상세 조회)
  - `TmdbClient.kt`: Retrofit 인스턴스 빌더 및 헤더(Bearer Authorization 토큰) 세팅
  - `TmdbDto.kt`: API 응답 파싱용 데이터 전송 객체(DTO)
  - `TmdbMapper.kt`: TMDB DTO 객체를 domain 계층의 `Work` 모델로 변환하는 변환기
* 이미지 로드 시에는 Coil 라이브러리를 사용하며, 규격화된 다음 포스터 경로 형식을 매핑하여 이미지를 효율적으로 캐싱 및 렌더링한다.
  ```text
  https://image.tmdb.org/t/p/w500/{posterPath}
  ```

---

## 11. Gemini API 구조

`data/remote/gemini` 패키지에 구축되어 있으며, Google Generative AI API를 활용해 텍스트 요약 및 분석 추론을 실시간 처리한다.

* 구현 파일:
  - `GeminiApiService.kt`: API 통신 인터페이스 및 Endpoint 구현
  - `GeminiClient.kt`: Gemini 연동을 위한 클라이언트 및 JSON 직렬화 설정
  - `GeminiPromptBuilder.kt`: 일관되고 정밀한 JSON 포맷 출력을 강제하기 위한 프롬프트 구조화 모듈
* 프롬프트 관리: AI가 임의의 텍스트가 아닌 정확히 규격화된 JSON 문자열을 반환하도록 유도하여, 앱 코드에서 파싱 에러 없이 통계, 키워드 칩 리스트, 추천 사유 등을 안전하게 데이터 클래스로 바인딩할 수 있도록 최적화되어 있다.

---

## 12. Repository 구조

비즈니스 로직과 데이터 소스 간의 결합도를 낮추기 위해 `data/repository` 내에 Repository 패턴이 완벽하게 구현되어 있다.

* WorkRepositoryImpl: TMDB API를 통해 검색 및 상세 조회를 관장하며 데이터 매퍼를 거쳐 도메인에 가공된 `Work` 모델을 전달한다.
* ReviewRepositoryImpl: SharedPreferences를 매개로 작동하며, 감상 기록의 실시간 추가, 삭제, 수정 사항을 영구적으로 디스크에 쓰고 읽는다. 동일 날짜 다중 기록 등록을 위한 바인딩 구조가 통합되어 있다.
* PreferenceRepositoryImpl: 저장된 감상 기록 데이터를 취합하여 Gemini API에 분석 프롬프트를 요청하고, 응답받은 사용자의 취향 정보 분석 결과(`PreferenceAnalysis`)를 로컬 디바이스에 저장하여 캐싱을 유지한다.

---

## 13. 구현 완료 단계 (Phase History)

본 프로젝트는 세 단계에 걸쳐 유기적인 빌드 상태를 유지하며 성공적으로 구현을 완료하였다.

### Phase 1 완료 — TMDB API 기반 작품 검색 및 상세 조회

* `local.properties`와 `BuildConfig`를 활용하여 TMDB API Key 연동 환경을 안전하게 구축 완료하였다.
* Retrofit 통신을 통해 실시간 영화 및 드라마 목록 검색 환경을 제공하며, 연도 및 유형(영화/드라마) 정보를 리스트에 노출한다.
* 상세 조회 기능을 연결하여 줄거리, 장르, 평점, 포스터를 오류 없이 렌더링하도록 완성했다.

### Phase 2 완료 — 감상 기록 저장 및 다중 포스터 기반 캘린더

* SharedPreferences를 직렬화 저장소로 활용하는 감상 기록 저장 모듈을 완성하였다.
* 날짜, 별점(1~5점), 한줄평(글자 제한) 기능을 구현하고 유효성 제어를 결합하였다.
* 감상 기록을 달력에 포스터 썸네일로 바인딩하여 캘린더 홈을 직관적으로 시각화했다.
* 같은 날짜에 다중 작품 기록 기능을 구현했으며, 바텀 시트 목록 연동 및 추가 버튼을 통한 날짜 데이터 유지 검색 전환까지 완벽히 마쳤다.
* 기록의 상세 보기 및 실시간 수정/삭제 프로세스를 안전하게 연결했다.

### Phase 3 완료 — Gemini API 기반 OST 정보 및 AI 취향 분석

* 작품 정보 연동 시 Gemini API를 통해 대표 OST 정보를 추출하여 텍스트로 요약 및 영구 기록하는 프로세스를 완비했다.
* 축적된 로컬 감상 기록 데이터를 토대로 Gemini Pro를 호출해 선호 장르 통계, 취향 키워드 칩, 추천 작품 및 상세 이유 카드를 생성하는 분석 탭을 완료했다.
* Gemini API가 Quota 초과(오류 코드 429 등) 혹은 네트워크 유실 상태에 직면했을 때 크래시 없이 안전하게 작동하고, 적합한 사용자 Fallback 메시지를 띄우는 복구 로직을 구현했다.

---

## 14. 디자인 시스템 준수

전체 UI는 기획 시안(`Design` 폴더 내 이미지들)의 톤앤매너를 정밀하게 계승하여 모던하게 마감되었다.

* 컬러 시스템: 깨끗한 화이트 배경을 기초로 하며, 포인트 테마 컬러로 감각적인 퍼플(Purple) 계열을 사용하고 있다.
* 캘린더 디자인: 선택된 날짜는 선명한 파란색 원으로 강조되고, 토요일은 파란색, 일요일은 빨간색으로 가독성 있게 요일을 구분한다.
* 레이아웃: 모바일 화면에 어울리는 둥근 모서리(Rounded Corner) 카드 및 텍스트 칩(Chip)을 적극 사용하여 정보를 보기 편하게 그룹화했다.
* 임시 데이터 제거: 릴리스 가능한 품질을 유지하기 위해 테스트용 더미 텍스트, placeholder, TODO 마크업 등은 배포 화면에서 모두 제거되었다.

---

## 15. 현재 단계 및 향후 안정화 계획

### 15.1 현재 단계

핵심 요구사항 기능(온보딩, 검색, 상세조회, 저장/수정/삭제, 포스터 캘린더, 다중 기록, Gemini OST 연동, AI 취향 분석 및 작품 추천, Fallback 안전 설계)과 Gradle 빌드 테스트 패스를 100% 완료한 최종 구현 상태이다.

### 15.2 시연 전 우선순위 안정화 계획

신규 기능 추가는 완료되었으므로, 성공적인 시연(데모) 및 최종 제출을 위한 안정화 작업에 전념한다.

1. **상태 전환 및 코너 케이스 테스트****:**
   - 네트워크 연결이 차단된 비행기 모드 상태에서 로컬 저장된 데이터가 정상 복원되는지 확인.
   - 검색어가 매우 길거나 특수문자가 들어갔을 때의 실시간 예외 상황 모니터링.
2. **시연 시나리오 기반 영상 촬영:**
   - 최초 온보딩 진입 $\rightarrow$ 시작하기 터치 $\rightarrow$ 캘린더 화면 확인 $\rightarrow$ 특정 날짜 선택 후 작품 검색 및 추가 $\rightarrow$ 저장 완료 후 캘린더에 포스터 표시 확인 $\rightarrow$ 저장된 감상 정보 확인 및 수정 $\rightarrow$ "취향" 탭 진입 후 Gemini AI 분석 및 추천 리포트 렌더링 확인 $\rightarrow$ 최종 종료까지의 흐름을 물 흐르듯 데모 영상으로 레코딩하여 준비한다.
3. **배포용 리소스 검증:**
   - 최종 런처 아이콘의 해상도 검사 및 AndroidManifest.xml 설정 최종 모니터링.
4. **문서 및 README 최종 정리:**
   - 신규 사용자가 프로젝트를 열었을 때 `local.properties`에 API Key를 세팅하는 방법과 최소 SDK 조건 등을 명확히 안내할 수 있도록 가이드를 정밀하게 검토한다.
