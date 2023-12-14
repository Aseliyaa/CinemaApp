package com.example.cinemaapp.domain

import com.google.gson.annotations.SerializedName

data class Metadata(

    @SerializedName("current_page")
    var currentPage: String? = null,

    @SerializedName("per_page")
    var perPage: Int? = null,

    @SerializedName("page_count")
    var pageCount: Int? = null,

    @SerializedName("total_count")
    var totalCount: Int? = null
)
