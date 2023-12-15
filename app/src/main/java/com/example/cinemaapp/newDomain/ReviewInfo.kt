package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class ReviewInfo (

  @SerializedName("count"         ) var count         : Int?    = null,
  @SerializedName("positiveCount" ) var positiveCount : Int?    = null,
  @SerializedName("percentage"    ) var percentage    : String? = null

)