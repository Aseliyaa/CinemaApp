package com.example.cinemaapp.newDomain

import com.google.gson.annotations.SerializedName


data class Networks (

  @SerializedName("items" ) var items : ArrayList<Items> = arrayListOf()

)