package com.example.cinemaapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.cinemaapp.domain.FilmItem

@Dao
interface FilmItemDao {
    @Query("SELECT * FROM films_db")
    fun getAll(): List<FilmItem>

    @Query("SELECT * FROM films_db WHERE userId = :userId")
    fun getById(userId: String): List<FilmItem>

    @Query("UPDATE films_db SET uid = uid - 1 WHERE uid > :deletedUid")
    fun updateFilmsUid(deletedUid: Int?)

    @Insert
    fun addFilm(film: FilmItem)

    @Insert
    fun addAll(films: List<FilmItem>)

    @Query("DELETE FROM films_db WHERE id = :id AND userId = :userId")
    fun deleteFilm(id: Int?, userId: String?)

    @Delete
    fun deleteFilm(film: FilmItem)

    @Query("DELETE FROM films_db")
    fun deleteAll()
}