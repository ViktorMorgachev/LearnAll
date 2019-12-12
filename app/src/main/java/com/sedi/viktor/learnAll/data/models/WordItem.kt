package com.sedi.viktor.learnAll.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class WordItem {

    @PrimaryKey(autoGenerate = true)
    val primaryKey: Int = 0


    var learned: Boolean = false
        set(value) {
            field = value
        }


    @ColumnInfo(name = "other_name")
    var otherName: String = ""
        set(value) {
            field = value

        }

    @ColumnInfo(name = "native_name")
    var nativeName: String = ""
        set(value) {
            field = value

        }

    @ColumnInfo(name = "card_state_other")
    var cardStateOther: CardState = CardState()
        set(value) {
            field = value
        }

    @ColumnInfo(name = "card_state_native")
    var cardStateNative: CardState = CardState()
        set(value) {
            field = value
        }

    constructor(
        otherName: String,
        nativeName: String,
        cardStateNative: CardState,
        cardStateOther: CardState
    ) :
            super() {
        this.cardStateNative = cardStateNative
        this.cardStateOther = cardStateOther
        this.otherName = otherName
        this.nativeName = nativeName
    }

    constructor() {
        this.cardStateNative = CardState()
        this.cardStateOther = CardState()
        this.otherName = ""
        this.nativeName = ""
    }


}