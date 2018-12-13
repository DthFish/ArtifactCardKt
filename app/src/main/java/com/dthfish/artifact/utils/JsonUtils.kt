package com.dthfish.artifact.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException

/**
 * Description
 * Author DthFish
 * Date  2018/12/12.
 */
object JsonUtils {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    inline fun <reified T> parseJson(jsonStr: String) = JsonUtils.parseJson(jsonStr, T::class.java)
    inline fun <reified T> toJson(obj: T?) = JsonUtils.toJson(obj, T::class.java)

    @JvmStatic
    fun <T> parseJson(jsonStr: String, clazz: Class<T>): T? {
        return try {
            moshi.adapter(clazz).fromJson(jsonStr)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun <T> toJson(obj: T?, clazz: Class<T>): String? {
        return try {
            moshi.adapter(clazz).toJson(obj)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}