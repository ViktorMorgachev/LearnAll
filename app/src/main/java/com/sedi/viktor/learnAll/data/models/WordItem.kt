package com.sedi.viktor.learnAll.data.models


class WordItem(
    var learned: Boolean = false, var otherName: String = "", var nativeName: String = "",
    var cardStateNative: CardState = CardState(), var cardStateOther: CardState = CardState()
) {
    constructor() : this(false, "", "", CardState(), CardState())
}