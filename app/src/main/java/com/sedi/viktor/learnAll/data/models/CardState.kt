package com.sedi.viktor.learnAll.data.models

import io.realm.RealmModel

/**
 * Состояние каждой карточки, стиль, фон, цвет текста
 * */
class CardState(val fontColor: String = "", val style: String = "", val TextColor: String = "") :
    RealmModel {
    fun copy(): CardState {
        return CardState(this.fontColor, this.style, this.TextColor)
    }
}