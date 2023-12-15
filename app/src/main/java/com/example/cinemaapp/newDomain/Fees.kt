package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class Fees (

  @SerializedName("world"  ) var world  : World?  = World(),
  @SerializedName("russia" ) var russia : Russia? = Russia(),
  @SerializedName("usa"    ) var usa    : Usa?    = Usa()

)