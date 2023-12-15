package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class SimilarMovies (

  @SerializedName("id"              ) var id              : Int?    = null,
  @SerializedName("rating"          ) var rating          : Rating? = Rating(),
  @SerializedName("year"            ) var year            : Int?    = null,
  @SerializedName("name"            ) var name            : String? = null,
  @SerializedName("enName"          ) var enName          : String? = null,
  @SerializedName("alternativeName" ) var alternativeName : String? = null,
  @SerializedName("type"            ) var type            : String? = null,
  @SerializedName("poster"          ) var poster          : Poster? = Poster()

)