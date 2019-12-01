package com.sedi.viktor.learnAll.data.remote

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.interfaces.TranslateImpl
import com.sedi.viktor.learnAll.data.interfaces.TranslateResponseCallbackImpl
import com.sedi.viktor.learnAll.data.models.PostObjects.TranslateJson
import okhttp3.Response
import java.io.IOException

class YandexTranslater(val context: Context) : TranslateImpl {

    private var networkRequestCallback: NetworkManager.networkRequestCallback? = null
    private var gson = Gson()
    private var lifecycleOwner: LifecycleOwner? = null

    override fun translate(
        nativeLanguage: String,
        otherLanguage: String,
        text: String,
        tranlateResponceCallback: TranslateResponseCallbackImpl,
        lifecycleOwner: LifecycleOwner
    ) {

        this.lifecycleOwner = lifecycleOwner
        val translateJson =
            TranslateJson(
                context.resources.getString(R.string.yandex_translate_key),
                text, "$nativeLanguage-$otherLanguage"
            )

        if (networkRequestCallback == null) {
            initNetworkCallback(tranlateResponceCallback)
        }

        NetworkManager.Me.instance.makeRequestPost(
            link,
            gson.toJson(translateJson),
            networkRequestCallback!!
        )

    }

    private fun initNetworkCallback(
        tranlateResponceCallBack: TranslateResponseCallbackImpl
    ) {
        networkRequestCallback = object : NetworkManager.networkRequestCallback {
            override fun onSucess(responce: Response) {
                if (lifecycleOwner!!.lifecycle.currentState == Lifecycle.State.RESUMED)
                    tranlateResponceCallBack.onSuccess(responce.body().string())
            }

            override fun onError(e: IOException) {
                tranlateResponceCallBack.onFaillure(e)
            }
        }
    }

    private val link = "https://translate.yandex.net/api/v1.5/tr.json/translate"


}
