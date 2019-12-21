package com.sedi.viktor.learnAll.data.models


class WordItem(
    var primaryKey: Int = 0,
    var learned: Boolean = false,
    var otherName: String = "",
    var nativeName: String = "",
    var favourite: Boolean = false,
    var cardStateNative: CardState = CardState(),
    var cardStateOther: CardState = CardState()
) {
    constructor() : this(0, false, "", "", false, CardState(), CardState())

    fun copy(): WordItem = WordItem(
        primaryKey,
        learned,
        otherName,
        nativeName,
        favourite,
        cardStateNative,
        cardStateOther
    )

}