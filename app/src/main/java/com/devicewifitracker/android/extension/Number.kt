package com.devicewifitracker.android.extension

import com.devicewifitracker.android.App
import java.lang.StringBuilder

/**
 * Number extension methods.
 *
 * @author guolin
 * @since 2020/9/24
 */

/**
 * Convert dp to px.
 */
val Int.dp: Int
    get() {
        val scale = App.context.resources.displayMetrics.density
        return (this * scale + 0.5).toInt()
    }

val Float.dp: Float
    get() {
        val scale = App.context.resources.displayMetrics.density
        return (this * scale + 0.5).toFloat()
    }

val Double.dp: Double
    get() {
        val scale = App.context.resources.displayMetrics.density
        return this * scale + 0.5
    }

/**
 * Convert a number to a numeric string.
 * e.g. 12365 wil be converted into 12,365
 */
fun Int.toNumericString(): String {
    val chars = toString().toCharArray()
    chars.reverse()
    val builder = StringBuilder()
    chars.forEachIndexed { index, c ->
        if (index != 0 && index % 3 == 0 && c != '-') {
            builder.append(",")
        }
        builder.append(c)
    }
    return builder.reverse().toString()
}