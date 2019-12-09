package com.sedi.viktor.learnAll.data.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sedi.viktor.learnAll.BR
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
class WordItem : BaseObservable, RealmModel {

    @PrimaryKey
    private val primaryKey: Long = 0

    @get:Bindable
    var otherName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.otherName)
        }

    @get:Bindable
    var nativeName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.nativeName)
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