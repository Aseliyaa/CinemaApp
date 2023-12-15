package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class Facts (

  @SerializedName("value"   ) var value   : String?  = null,
  @SerializedName("type"    ) var type    : String?  = null,
  @SerializedName("spoiler" ) var spoiler : Boolean? = null

)