package com.example.ostory.data.remote.tmdb

import com.example.ostory.domain.model.Genre
import com.example.ostory.domain.model.Work
import com.example.ostory.domain.model.WorkType

private val GENRE_MAP = mapOf(
    28 to "액션",
    12 to "모험",
    16 to "애니메이션",
    35 to "코미디",
    80 to "범죄",
    99 to "다큐멘터리",
    18 to "드라마",
    10751 to "가족",
    14 to "판타지",
    36 to "역사",
    27 to "공포",
    10402 to "음악",
    9648 to "미스터리",
    10749 to "로맨스",
    878 to "SF",
    10770 to "TV 영화",
    53 to "스릴러",
    10752 to "전쟁",
    37 to "서부",
    10759 to "액션 및 모험",
    10762 to "키즈",
    10763 to "뉴스",
    10764 to "리얼리티",
    10765 to "SF 및 판타지",
    10766 to "드라마",
    10767 to "토크",
    10768 to "전쟁 및 정치"
)

fun Int.toGenre(): Genre {
    return Genre(id = this, name = GENRE_MAP[this] ?: "기타")
}

fun TmdbGenreDto.toGenre(): Genre {
    return Genre(id = this.id, name = this.name)
}

fun String?.toPosterUrl(): String? {
    return if (this.isNullOrBlank()) null else "https://image.tmdb.org/t/p/w500$this"
}

fun String?.extractYear(): Int {
    if (this.isNullOrBlank()) return 0
    return try {
        this.split("-")[0].toInt()
    } catch (e: Exception) {
        0
    }
}

fun TmdbMovieDto.toWork(): Work {
    return Work(
        id = this.id,
        titleKo = this.title,
        titleEn = this.originalTitle,
        type = WorkType.MOVIE,
        year = this.releaseDate.extractYear(),
        posterPath = this.posterPath.toPosterUrl(),
        genres = this.genreIds?.map { it.toGenre() } ?: emptyList(),
        plot = this.overview,
        rating = this.voteAverage,
        ostList = emptyList()
    )
}

fun TmdbTvDto.toWork(): Work {
    return Work(
        id = this.id,
        titleKo = this.name,
        titleEn = this.originalName,
        type = WorkType.DRAMA,
        year = this.firstAirDate.extractYear(),
        posterPath = this.posterPath.toPosterUrl(),
        genres = this.genreIds?.map { it.toGenre() } ?: emptyList(),
        plot = this.overview,
        rating = this.voteAverage,
        ostList = emptyList()
    )
}

fun TmdbMovieDetailDto.toWork(): Work {
    return Work(
        id = this.id,
        titleKo = this.title,
        titleEn = this.originalTitle,
        type = WorkType.MOVIE,
        year = this.releaseDate.extractYear(),
        posterPath = this.posterPath.toPosterUrl(),
        genres = this.genres?.map { it.toGenre() } ?: emptyList(),
        plot = this.overview,
        rating = this.voteAverage,
        ostList = emptyList()
    )
}

fun TmdbTvDetailDto.toWork(): Work {
    return Work(
        id = this.id,
        titleKo = this.name,
        titleEn = this.originalName,
        type = WorkType.DRAMA,
        year = this.firstAirDate.extractYear(),
        posterPath = this.posterPath.toPosterUrl(),
        genres = this.genres?.map { it.toGenre() } ?: emptyList(),
        plot = this.overview,
        rating = this.voteAverage,
        ostList = emptyList()
    )
}
