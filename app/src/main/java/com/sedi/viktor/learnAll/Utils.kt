package com.sedi.viktor.learnAll

import androidx.annotation.NonNull
import androidx.annotation.Nullable

fun convertColorToString(color: Color): String = color.name

fun convertColorIntToColor(color: Int): Color? {

    val colors = Color.values()
    var result: Color? = null
    for (i in colors.indices) {
        if (colors[i].color == color)
            result = colors[i]
    }
    return result

}

fun convertColorStringToInt(color: String, @NonNull defaultColor: Int): Int {

    val colors = Color.values()
    var result: Int? = null
    for (i in colors.indices) {
        if (colors[i].name == color)
            result = colors[i].color
    }

    return result ?: defaultColor

}
