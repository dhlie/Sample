package com.dhl.base.utils

import com.google.gson.Gson
import java.lang.Exception
import java.lang.reflect.Type

/**
 *
 * Author: duanhaoliang
 * Create: 2021/4/30 10:11
 * Description:
 *
 */
object Gsons {

    val gson = Gson()

    fun <T> fromJson(json: String?, classOfT: Class<T>): T? {
        return try {
            gson.fromJson(json, classOfT)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> fromJson(json: String?, type: Type): T? {
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun toJson(obj: Any?): String? {
        if (obj == null) {
            return null
        }
        return try {
            gson.toJson(obj)
        } catch (e: Exception) {
            null
        }
    }

}