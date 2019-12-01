package com.sedi.viktor.learnAll.data.interfaces

interface TranslateResponseCallbackImpl {

    fun onSuccess(response: String)
    fun onFaillure(e : Exception)

}