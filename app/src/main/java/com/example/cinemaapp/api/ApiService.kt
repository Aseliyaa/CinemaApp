package com.example.cinemaapp.api

import com.example.cinemaapp.domain.FilmItem
import com.example.cinemaapp.domain.Genres
import com.example.cinemaapp.domain.ListFilm
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://moviesapi.ir/api/v1/"

interface ApiService {
    @GET("movies/{idFilm}")
    fun getMovieDetails(
        @Path("idFilm") idFilm: Int
    ): Call<FilmItem>

    @GET("movies")
    fun getMovies(@Query("page") page: Int): Call<ListFilm>

    @GET("genres")
    fun getGenres(): Call<List<Genres>>

    companion object Factory {
        fun create(): ApiService {
            val gson = GsonBuilder()
                .create()

            val okHttpClient = OkHttpClient.Builder()
                .build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}