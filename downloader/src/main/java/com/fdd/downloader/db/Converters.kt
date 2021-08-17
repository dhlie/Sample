package com.fdd.downloader.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/11 17:15
 * Description:
 *
 */
internal class Converters {

    private val gson = Gson()

    @TypeConverter
    fun mapToString(map: Map<String, String>?): String? {
        return if (map.isNullOrEmpty()) null else gson.toJson(map)
    }

    @TypeConverter
    fun stringToMap(string: String?): Map<String, String>? {
        return gson.fromJson(string, object : TypeToken<Map<String, String>>() {}.type)
    }

}