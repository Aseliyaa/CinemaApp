package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class Audience (

  @SerializedName("count"   ) var count   : Int?    = null,
  @SerializedName("country" ) var country : String? = null

)