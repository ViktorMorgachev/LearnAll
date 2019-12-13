package com.sedi.viktor.learnAll.data.models


class WordItem {

    var learned: Boolean = false
        set(value) {
            field = value
        }

    var otherName: String = ""
        set(value) {
            field = value

        }
    var nativeName: String = ""
        set(value) {
            field = value

        }


    var cardStateOther: CardState = CardState()
        set(value) {
            field = value
        }


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