package com.sedi.viktor.learnAll.data.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sedi.viktor.learnAll.BR


class WordItem (
    var primaryKey: Int = 0,
    private var learned: Boolean = false,
    private var otherName: String = "",
    private var nativeName: String = "",
    private var favourite: Boolean = false,
    private var cardStateNative: CardState = CardState(),
    private var cardStateOther: CardState = CardState()
): BaseObservable() {


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

    @Bindable
    fun setLearned(isLearned : Boolean){
        learned = isLearned
        notifyPropertyChanged(BR.learned)
    }


}