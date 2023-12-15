package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class Poster (

  @SerializedName("url"        ) var url        : String? = null,
  @SerializedName("previewUrl" ) var previewUrl : String? = null

)