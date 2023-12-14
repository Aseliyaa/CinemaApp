package com.example.cinemaapp.db

import androidx.room.TypeConverter

class Converter {
    @TypeConverter
    fun listToString(values: List<String>?) :String {
        val sb = StringBuilder()
        if (values != null) {
            for (v in values){
                sb.append(v).append(",")
            }
        }
        return sb.toString()
    }

    @TypeConverter
    fun fromStringToList(value: String): List<String> {
        return value.split(",")
    }
}