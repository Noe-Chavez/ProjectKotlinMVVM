package com.disoftware.db

import android.util.Log
import androidx.room.TypeConverter
import java.lang.NumberFormatException

object GithubTypeConverters {
    /**
     * Convertir lista de id de tipo string a lista de id de tipo int.
     */
    @TypeConverter
    @JvmStatic
    fun stringToIntList(data: String?): List<Int>? {
        return data?.let {
            it.split(",").map {
                try {
                    it.toInt()
                } catch (ex: NumberFormatException) {
                    Log.d("TAG1", "No se puede convertit a numero.")
                    null
                }
            }?.filterNotNull()
        }
    }

    /**
     * Convertir lista de id de tipo int a lista de id de tipo String.
     */
    @TypeConverter
    @JvmStatic
    fun intListToString(ints: List<Int>?): String? {
        return ints?.joinToString { "," }
    }

}