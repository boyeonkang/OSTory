package com.example.ostory.data.repository

import com.example.ostory.BuildConfig
import com.example.ostory.data.remote.tmdb.TmdbClient
import com.example.ostory.data.remote.tmdb.toWork
import com.example.ostory.domain.model.Work
import com.example.ostory.domain.model.WorkType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class WorkRepository {
    private val apiService = TmdbClient.service
    private val apiKey = BuildConfig.TMDB_API_KEY

    suspend fun searchWorks(query: String): List<Work> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "여기에_TMDB_API_KEY_입력") {
            return@withContext emptyList()
        }
        try {
            val movieDeferred = async { apiService.searchMovies(apiKey, query) }
            val tvDeferred = async { apiService.searchTvShows(apiKey, query) }

            val movies = movieDeferred.await().results.map { it.toWork() }
            val tvShows = tvDeferred.await().results.map { it.toWork() }

            movies + tvShows
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getWorkDetail(id: Int, type: WorkType): Work? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "여기에_TMDB_API_KEY_입력") {
            return@withContext null
        }
        try {
            when (type) {
                WorkType.MOVIE -> {
                    apiService.getMovieDetails(id, apiKey).toWork()
                }
                WorkType.DRAMA -> {
                    apiService.getTvDetails(id, apiKey).toWork()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
