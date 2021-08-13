package com.dhl.base.downloader.db

import androidx.room.TypeConverter
import com.dhl.base.utils.Gsons
import com.google.gson.reflect.TypeToken

/**
 *
 * Author: duanhaoliang
 * Create: 2021/8/11 17:15
 * Description:
 *
 */
class Converters {

    @TypeConverter
    fun mapToString(map: Map<String, String>?): String? {
        return Gsons.toJson(map)
    }

    @TypeConverter
    fun stringToMap(string: String?): Map<String, String>? {
        return Gsons.fromJson(string, object : TypeToken<Map<String, String>>() {}.type)
    }

}