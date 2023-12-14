package com.example.cinemaapp.domain

import com.google.gson.annotations.SerializedName;

data class Data(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("poster")
    var poster: String? = null,

    @SerializedName("year")
    var year: String? = null,

    @SerializedName("country")
    var country: String? = null,

    @SerializedName("imdb_rating")
    var imdbRating: String? = null,

    @SerializedName("genres")
    var genres: ArrayList<String> = arrayListOf(),

    @SerializedName("images")
    var images: ArrayList<String> = arrayListOf()
)
