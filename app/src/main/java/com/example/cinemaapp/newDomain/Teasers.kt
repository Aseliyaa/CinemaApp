package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class Teasers (

  @SerializedName("url"  ) var url  : String? = null,
  @SerializedName("name" ) var name : String? = null,
  @SerializedName("site" ) var site : String? = null,
  @SerializedName("type" ) var type : String? = null,
  @SerializedName("size" ) var size : Int?    = null

)