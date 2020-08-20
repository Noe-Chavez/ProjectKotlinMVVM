package com.disoftware.utils

import android.os.SystemClock
import android.util.ArrayMap
import java.util.concurrent.TimeUnit

class RateLimiter<in KEY>(timeOut: Int, timeUnit: TimeUnit) {
    private val timesTaps = ArrayMap<KEY, Long>()
    private val timeOut = timeUnit.toMillis(timeOut.toLong())

    @Synchronized
    fun shouldFetch(key: KEY): Boolean {
        val lastFeched: Long? = timesTaps[key]
        val now: Long = now()

        if (lastFeched == null) { // Nunca se han solicitado los datos.
            timesTaps[key] = now
            return true
        }

        if (now - lastFeched > timeOut) {
            timesTaps[key]
            return true
        }
        return false
    }

    private fun now() = SystemClock.uptimeMillis()

    @Synchronized
    fun reset(key: KEY) = timesTaps.remove(key)

}