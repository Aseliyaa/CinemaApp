package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class Videos (

  @SerializedName("trailers" ) var trailers : ArrayList<Trailers> = arrayListOf(),
  @SerializedName("teasers"  ) var teasers  : ArrayList<Teasers>  = arrayListOf()

)