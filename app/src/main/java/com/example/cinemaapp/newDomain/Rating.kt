package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class Rating (

  @SerializedName("kp"                 ) var kp                 : Double? = null,
  @SerializedName("imdb"               ) var imdb               : Double? = null,
  @SerializedName("tmdb"               ) var tmdb               : Double? = null,
  @SerializedName("filmCritics"        ) var filmCritics        : Int?    = null,
  @SerializedName("russianFilmCritics" ) var russianFilmCritics : Double? = null,
  @SerializedName("await"              ) var await              : Double? = null

)