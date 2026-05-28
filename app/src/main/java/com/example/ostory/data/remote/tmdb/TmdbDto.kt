package com.example.ostory.data.remote.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbMultiSearchResponse(
    @SerializedName("results") val results: List<TmdbSearchResultDto>
)

data class TmdbSearchResultDto(
    @SerializedName("id") val id: Int,
    @SerializedName("media_type") val mediaType: String, // "movie", "tv", "person"
    @SerializedName("title") val title: String?, // movie
    @SerializedName("name") val name: String?, // tv
    @SerializedName("original_title") val originalTitle: String?, // movie
    @SerializedName("original_name") val originalName: String?, // tv
    @SerializedName("release_date") val releaseDate: String?, // movie
    @SerializedName("first_air_date") val firstAirDate: String?, // tv
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("genre_ids") val genreIds: List<Int>?
)

data class TmdbMovieSearchResponse(
    @SerializedName("results") val results: List<TmdbMovieDto>
)

data class TmdbTvSearchResponse(
    @SerializedName("results") val results: List<TmdbTvDto>
)

data class TmdbMovieDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("genre_ids") val genreIds: List<Int>?
)

data class TmdbTvDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("original_name") val originalName: String,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("genre_ids") val genreIds: List<Int>?
)

data class TmdbMovieDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("genres") val genres: List<TmdbGenreDto>?
)

data class TmdbTvDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("original_name") val originalName: String,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("genres") val genres: List<TmdbGenreDto>?
)

data class TmdbGenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
