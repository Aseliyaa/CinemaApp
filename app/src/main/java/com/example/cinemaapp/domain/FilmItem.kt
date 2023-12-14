package com.example.cinemaapp.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "films_db")
class FilmItem {
    @SerializedName("id")
    var id: Int? = null

    var userId: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("poster")
    var poster: String? = null

    @SerializedName("year")
    var year: String? = null

    @SerializedName("rated")
    var rated: String? = null

    @SerializedName("released")
    var released: String? = null

    @SerializedName("runtime")
    var runtime: String? = null

    @SerializedName("director")
    var director: String? = null

    @SerializedName("writer")
    var writer: String? = null

    @SerializedName("actors")
    var actors: String? = null

    @SerializedName("plot")
    var plot: String? = null

    @SerializedName("country")
    var country: String? = null

    @SerializedName("awards")
    var awards: String? = null

    @SerializedName("metascore")
    var metascore: String? = null

    @SerializedName("imdb_rating")
    var imdbRating: String? = null

    @SerializedName("imdb_votes")
    var imdbVotes: String? = null

    @SerializedName("imdb_id")
    var imdbId: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("genres")
    var genres: List<String>? = null

    @SerializedName("images")
    var images: List<String>? = null

    @PrimaryKey(autoGenerate = true)
    var uid: Int? = null
}





