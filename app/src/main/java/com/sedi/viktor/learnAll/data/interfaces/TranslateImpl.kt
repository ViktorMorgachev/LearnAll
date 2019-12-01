package com.sedi.viktor.learnAll.data.interfaces

import androidx.lifecycle.LifecycleOwner

interface TranslateImpl {
    fun translate(
        nativeLanguage: String,
        otherLanguage: String,
        text: String,
        tranlateResponceCallback: TranslateResponseCallbackImpl,
        lifecycleOwner: LifecycleOwner
    )

}