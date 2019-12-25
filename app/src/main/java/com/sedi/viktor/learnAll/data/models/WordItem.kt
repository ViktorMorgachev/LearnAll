package com.sedi.viktor.learnAll.data.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sedi.viktor.learnAll.BR


class WordItem(
    var primaryKey: Int = 0,
    private var learned: Boolean = false,
    private var otherName: String = "",
    private var nativeName: String = "",
    private var favourite: Boolean = false,
    private var cardStateNative: CardState = CardState(),
    private var cardStateOther: CardState = CardState()
) : BaseObservable() {


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
    fun setLearned(isLearned: Boolean) {
        learned = isLearned
        notifyPropertyChanged(BR.learned)
    }

    @Bindable
    fun setOtherName(name: String) {
        otherName = name
        notifyPropertyChanged(BR.otherName)
    }


    @Bindable
    fun setNativeName(name: String) {
        nativeName = name
        notifyPropertyChanged(BR.nativeName)
    }

    @Bindable
    fun setFavourite(isFavourite : Boolean){
        favourite = isFavourite
        notifyPropertyChanged(BR.favourite)
    }


    fun getLearned() : Boolean = learned
    fun getOtherName() : String = otherName
    fun getNativeName() : String = nativeName
    fun getFavourite() : Boolean = favourite
    fun getCardStateNative() : CardState = cardStateNative
    fun getCardStateOther() : CardState = cardStateOther





}