package com.sedi.viktor.learnAll

fun convertColorToString(color: Color): String = color.name

fun convertColorIntToColor(color: Int): Color {

    val colors = Color.values()
    var result = Color.DEFAULT
    for (i in colors.indices) {
        if (colors[i].color == color)
            result = colors[i]
    }
    return result


}