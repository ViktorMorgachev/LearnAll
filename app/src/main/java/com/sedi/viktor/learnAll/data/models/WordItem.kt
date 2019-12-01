package com.sedi.viktor.learnAll.data.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sedi.viktor.learnAll.BR


class WordItem : BaseObservable {

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

    constructor(otherName: String, nativeName: String) :
            super() {
        this.otherName = otherName
        this.nativeName = nativeName
    }


}