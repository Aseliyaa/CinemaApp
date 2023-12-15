package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class Names (

  @SerializedName("name"     ) var name     : String? = null,
  @SerializedName("language" ) var language : String? = null,
  @SerializedName("type"     ) var type     : String? = null

)