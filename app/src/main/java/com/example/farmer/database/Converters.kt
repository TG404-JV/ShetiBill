package com.example.farmer.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromIntList(value: MutableList<Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toIntList(value: String): MutableList<Int> {
        val listType = object : TypeToken<MutableList<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
