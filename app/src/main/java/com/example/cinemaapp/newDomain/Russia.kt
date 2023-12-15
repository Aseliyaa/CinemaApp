package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class Russia (

  @SerializedName("value"    ) var value    : Int?    = null,
  @SerializedName("currency" ) var currency : String? = null

)