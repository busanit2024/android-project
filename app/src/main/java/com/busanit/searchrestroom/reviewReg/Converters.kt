package com.busanit.searchrestroom.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.busanit.searchrestroom.reviewReg.FilterOptionState

class Converters {

    // List<FilterOptionState> to JSON string
    @TypeConverter
    fun fromFilterOptionStateList(list: List<FilterOptionState>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    // JSON string to List<FilterOptionState>
    @TypeConverter
    fun toFilterOptionStateList(data: String): List<FilterOptionState> {
        val gson = Gson()
        val listType = object : TypeToken<List<FilterOptionState>>() {}.type
        return gson.fromJson(data, listType)
    }
}
