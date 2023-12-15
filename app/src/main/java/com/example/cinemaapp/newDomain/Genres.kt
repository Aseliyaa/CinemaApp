package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class Genres (

  @SerializedName("name" ) var name : String? = null,

  var isClicked: Boolean = false
)