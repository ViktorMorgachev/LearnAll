package com.sedi.viktor.learnAll.data.models

import androidx.room.Entity


/**
 * Состояние каждой карточки, стиль, фон, цвет текста
 * */
@Entity
class CardState(val backColor: String = "", val style: String = "", val textColor: String = "") {
    fun copy(): CardState {
        return CardState(this.backColor, this.style, this.textColor)
    }
}