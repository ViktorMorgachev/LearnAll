package com.sedi.viktor.learnAll.data.models

import com.sedi.viktor.learnAll.Color


/**
 * Состояние каждой карточки, стиль, фон, цвет текста
 * */
class CardState(
    var backColor: String = Color.GRAY.name,
    var style: String = "",
    var textColor: String = Color.BLACK.name
) {
    fun copy(): CardState {
        return CardState(this.backColor, this.style, this.textColor)
    }
}