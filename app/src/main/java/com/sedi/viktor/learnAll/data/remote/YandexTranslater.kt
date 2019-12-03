package com.sedi.viktor.learnAll.data.remote

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.interfaces.TranslateImpl
import com.sedi.viktor.learnAll.data.interfaces.TranslateResponseCallbackImpl
import okhttp3.Response
import java.io.IOException

class YandexTranslater(val context: Context) : TranslateImpl {

    private var networkRequestCallback: NetworkManager.networkRequestCallback? = null
    private var lifecycleOwner: LifecycleOwner? = null

    override fun translate(
        nativeLanguage: String,
        otherLanguage: String,
        text: String,
        tranlateResponceCallback: TranslateResponseCallbackImpl,
        lifecycleOwner: LifecycleOwner
    ) {

        this.lifecycleOwner = lifecycleOwner


        if (networkRequestCallback == null) {
            initNetworkCallback(tranlateResponceCallback)
        }

        link =
            link + "&key=" + context.resources.getString(R.string.yandex_translate_key) + "&lang=$nativeLanguage-$otherLanguage"

        NetworkManager.Me.instance.makePostRequest(
            link,
            networkRequestCallback,
            text
        )

    }

    private fun initNetworkCallback(
        tranlateResponceCallBack: TranslateResponseCallbackImpl
    ) {
        networkRequestCallback = object : NetworkManager.networkRequestCallback {
            override fun onSucess(responce: Response) {
                if (lifecycleOwner!!.lifecycle.currentState == Lifecycle.State.RESUMED)
                    tranlateResponceCallBack.onSuccess(responce.body!!.string())
            }

            override fun onError(e: IOException) {
                tranlateResponceCallBack.onFaillure(e)
            }
        }
    }

    private var link = "https://translate.yandex.net/api/v1.5/tr.json/translate?"


}
