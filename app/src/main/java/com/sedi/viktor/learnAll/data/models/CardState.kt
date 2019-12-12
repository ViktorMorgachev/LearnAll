package com.sedi.viktor.learnAll.data.models

import androidx.room.Entity


/**
 * Состояние каждой карточки, стиль, фон, цвет текста
 * */
@Entity
class CardState(val fontColor: String = "", val style: String = "", val TextColor: String = "") {
    fun copy(): CardState {
        return CardState(this.fontColor, this.style, this.TextColor)
    }
}