# OSTory

OSTory는 영화 및 작품 정보를 검색하고, 감상 기록과 OST 정보를 함께 관리하기 위한 Android 애플리케이션입니다.
현재는 TMDB API를 활용한 작품 검색 기능을 구현했으며, 이후 Gemini API를 활용한 OST 정보 제공 및 취향 분석 기능을 확장할 예정입니다.

## 주요 기능

### 구현 완료

* TMDB API 연동 기본 구조 설정
* Gemini API 연동 기본 구조 설정
* API Key를 `local.properties`와 `BuildConfig`를 통해 관리
* Retrofit, OkHttp, Coil, Navigation Compose 의존성 추가
* `domain / data / presentation` 기반 패키지 구조 구성
* TMDB 작품 검색 화면 구현
* 검색어 입력 후 TMDB API를 통해 작품 목록 조회
* 검색 결과의 포스터, 제목, 개봉일 정보 표시

### 구현 예정

* 작품 상세 화면 구현
* 감상 기록 작성 및 저장 기능 구현
* 포스터 기반 캘린더 화면 구현
* Gemini API 기반 OST 정보 제공
* Gemini API 기반 감상 취향 분석 기능 구현

## API Key 설정

본 프로젝트는 TMDB API와 Gemini API를 사용합니다.
보안을 위해 실제 API Key는 코드에 직접 작성하지 않고 `local.properties`에서 관리합니다.

프로젝트 루트의 `local.properties` 파일에 아래 내용을 추가해야 합니다.

```properties
TMDB_API_KEY=your_tmdb_api_key
GEMINI_API_KEY=your_gemini_api_key
```

`local.properties` 파일은 GitHub에 업로드하지 않습니다.

## 기술 스택

* Kotlin
* Jetpack Compose
* Navigation Compose
* Retrofit
* OkHttp
* Coil
* TMDB API
* Gemini API

## 작업 내역

* `feat/api-setup`

  * API 연동 기본 구조 설정
  * TMDB 작품 검색 화면 구현
