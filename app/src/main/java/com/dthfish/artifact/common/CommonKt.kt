@file:Suppress("unused")

package com.dthfish.artifact.common

import android.util.Log
import java.io.*

/**
 * Description
 * Author DthFish
 * Date  2018/12/7.
 */
var isDebug = true

fun log(value: Any?, tag: String = "KotlinDemo") = if (isDebug) Log.d(tag, value.toString()) else 0

fun Any?.log(tag: String = "KotlinDemo") = if (isDebug) Log.d(tag, this.toString()) else null

fun object2ByteArray(value: Any?): ByteArray? {

    return if (value == null) {
        null
    } else {
        var bos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            bos = ByteArrayOutputStream()
            oos = ObjectOutputStream(bos)
            oos.writeObject(value)
            bos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            oos?.close()
            bos?.close()
        }
    }
}

fun Any?.o2b() = object2ByteArray(this)

fun <T> byteArray2object(value: ByteArray?): T? {

    return if (value == null) {
        null
    } else {
        var bis: ByteArrayInputStream? = null
        var ois: ObjectInputStream? = null
        try {
            bis = ByteArrayInputStream(value)
            ois = ObjectInputStream(bis)
            val readObject = ois.readObject()
            readObject as T
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            ois?.close()
            bis?.close()
        }
    }
}

fun <T> ByteArray?.b2o() = byteArray2object<T>(this)



