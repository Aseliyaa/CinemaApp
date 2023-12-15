package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class ImagesInfo (

  @SerializedName("postersCount"   ) var postersCount   : Int? = null,
  @SerializedName("backdropsCount" ) var backdropsCount : Int? = null,
  @SerializedName("framesCount"    ) var framesCount    : Int? = null

)