package com.example.cinemaapp.api

import com.example.cinemaapp.domain.Data
import com.example.cinemaapp.domain.FilmItem
import com.example.cinemaapp.domain.Genres
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RequestsOperator {
    companion object {

        private val apiService = ApiService.create()
        suspend fun sendRequest(pageId: Int): List<Data> {
            return withContext(Dispatchers.IO) {
                val call = apiService.getMovies(pageId)
                val response = call.execute()

                if (response.isSuccessful) {
                    response.body()?.data ?: emptyList()
                } else {
                    emptyList()
                }
            }
        }

        suspend fun sendGenresRequest(): List<Genres> {
            return withContext(Dispatchers.IO) {
                val call = apiService.getGenres()
                val response = call.execute()

                if (response.isSuccessful){
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
            }
        }

        suspend fun sendDetailsRequest(idFilm: Int): FilmItem {
            return withContext(Dispatchers.IO) {
                val call = apiService.getMovieDetails(idFilm)
                val response = call.execute()

                if (response.isSuccessful) {
                    response.body() ?: throw Exception("Response body is null")
                } else {
                    throw Exception("Request failed with code ${response.code()}")
                }
            }
        }
    }
}