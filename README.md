# OSTory

OSTory는 영화와 드라마를 검색하고, 감상 기록을 캘린더에 포스터 형태로 저장하며, 작품별 OST 정보와 AI 기반 취향 분석을 함께 제공하는 Android 애플리케이션입니다.

사용자는 TMDB API를 통해 영화와 드라마 정보를 검색하고, 작품 상세 화면에서 포스터, 제목, 장르, 줄거리, 평점 정보를 확인할 수 있습니다. 감상한 작품은 날짜, 별점, 한줄평과 함께 저장할 수 있으며, 저장된 기록은 캘린더 화면에 포스터 썸네일로 표시됩니다.

또한 Gemini API를 활용하여 작품별 대표 OST 정보를 불러오고, 사용자의 감상 기록과 한줄평을 바탕으로 작품 취향, 음악 취향, 추천 작품을 분석합니다.

---

## 주요 기능

### 구현 완료

* TMDB API 기반 영화/드라마 검색
* 검색 결과 목록 표시
  * 포스터
  * 작품 제목
  * 원제 또는 영어 제목
  * 작품 유형
  * 연도
* 작품 상세 정보 조회
  * 포스터
  * 제목
  * 원제 또는 영어 제목
  * 영화/드라마 구분
  * 연도
  * 장르
  * 줄거리
  * 평점
* Gemini API 기반 OST 정보 조회
  * 대표 OST 및 삽입곡 정보 표시
  * YouTube 검색 연결
  * OST 정보가 없는 경우 안내 문구 표시
* 감상 기록 작성
  * 감상 날짜 선택
  * 별점 입력
  * 한줄평 입력
  * 기록 저장
* 포스터 기반 감상 캘린더
  * 날짜별 감상 기록 표시
  * 감상 기록이 있는 날짜에 포스터 썸네일 표시
  * 월 이동 기능
* 감상 기록 상세 화면
  * 저장된 작품 정보 확인
  * 별점과 한줄평 확인
  * 기록 삭제
* Gemini API 기반 AI 취향 분석
  * 총 감상 기록 수 표시
  * 평균 별점 표시
  * 선호 장르 분석
  * 음악 취향 키워드 분석
  * AI 추천 작품 표시
  * Gemini API 요청 한도 초과 시 안내 문구 표시
* 하단 내비게이션
  * 캘린더
  * 검색
  * 취향

---

## 구현 제외 범위

본 프로젝트의 MVP 범위를 고려하여 다음 기능은 구현하지 않았습니다.

* 실제 음원 재생
* 음악 스트리밍
* Spotify 또는 YouTube Music 연동
* 음악 인식 기능
* 복잡한 추천 알고리즘
* 사용자 계정/로그인 기능

OST 정보와 AI 취향 분석은 Gemini API를 활용한 텍스트 기반 정보 제공 방식으로 구현했습니다.

---

## API Key 설정

본 프로젝트는 TMDB API와 Gemini API를 사용합니다.

보안을 위해 실제 API Key는 Kotlin 코드에 직접 작성하지 않고, 프로젝트 루트의 `local.properties` 파일에서 관리합니다.

`local.properties` 파일에 아래 내용을 추가해야 합니다.

```properties
TMDB_API_KEY=your_tmdb_api_key
GEMINI_API_KEY=your_gemini_api_key
```

`local.properties` 파일은 GitHub에 업로드하지 않습니다.

---

## 기술 스택

* Kotlin
* Jetpack Compose
* Material 3
* Navigation Compose
* ViewModel
* Coroutine
* Retrofit
* OkHttp
* Gson Converter
* Coil
* TMDB API
* Google Gemini API

---

## 패키지 구조

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
│  ├─ calendar/
│  ├─ search/
│  ├─ detail/
│  ├─ review/
│  └─ preference/
└─ ui/
   └─ theme/
```

---

## 실행 방법

1. 프로젝트를 Android Studio에서 엽니다.
2. 프로젝트 루트에 `local.properties` 파일을 생성하거나 기존 파일을 수정합니다.
3. `TMDB_API_KEY`와 `GEMINI_API_KEY` 값을 입력합니다.
4. Gradle Sync를 실행합니다.
5. Android Emulator 또는 실제 Android 기기에서 앱을 실행합니다.

---

## 빌드 확인

아래 명령어로 Kotlin 컴파일을 확인할 수 있습니다.

```powershell
.\gradlew compileDebugKotlin
```

---

## 주의 사항

* 실제 API Key는 GitHub에 업로드하지 않습니다.
* Gemini API는 무료 요청 한도가 있으므로, 한도를 초과하면 OST 정보 조회 또는 AI 취향 분석이 일시적으로 제한될 수 있습니다.
* API 요청 한도 초과 시 앱에서는 사용자에게 안내 문구를 표시합니다.
* 영화 및 드라마 정보는 TMDB API 응답을 기반으로 표시됩니다.
* OST 및 취향 분석 결과는 Gemini API 응답을 기반으로 생성되므로 작품에 따라 결과가 다를 수 있습니다.

---

## 작업 내역

* `feat/api-setup`
  * TMDB API와 Gemini API 연동 기본 구조 설정
  * API Key를 `local.properties`와 `BuildConfig`로 관리
  * Retrofit, OkHttp, Coil, Navigation Compose 의존성 추가
  * `domain / data / presentation` 기반 패키지 구조 구성
* `feat/search`
  * TMDB 기반 작품 검색 화면 구현
  * 검색 결과 목록 표시
  * 검색 결과 선택 시 작품 상세 화면 이동
* `feat/work-detail`
  * 작품 상세 화면 구현
  * 포스터, 제목, 장르, 줄거리, 평점 표시
  * 감상 기록 작성 화면 이동 기능 구현
* `feat/review`
  * 감상 기록 작성 화면 구현
  * 별점 및 한줄평 입력 기능 구현
  * 감상 기록 상세 화면 구현
  * 기록 삭제 기능 구현
* `feat/calendar`
  * 포스터 기반 캘린더 화면 구현
  * 저장된 감상 기록을 날짜별 포스터로 표시
  * 월 이동 기능 구현
* `feat/gemini-ost`
  * Gemini API 기반 OST 정보 조회 기능 구현
  * 대표 OST 정보 표시
  * YouTube 검색 연결 기능 구현
* `feat/ai-analysis`
  * Gemini API 기반 AI 취향 분석 화면 구현
  * 선호 장르, 음악 취향, 추천 작품 표시
  * API 요청 한도 초과 안내 처리

---

## 프로젝트 상태

현재 OSTory는 기말 프로젝트 MVP 기준으로 다음 핵심 흐름을 구현한 상태입니다.

```text
작품 검색
→ 작품 상세 조회
→ OST 정보 확인
→ 감상 기록 작성
→ 캘린더에 포스터 표시
→ 감상 기록 상세 확인
→ AI 취향 분석
```

향후에는 감상 기록 저장 방식 고도화, OST 정보 캐싱, 추천 기능 개선 등을 추가로 확장할 수 있습니다.
