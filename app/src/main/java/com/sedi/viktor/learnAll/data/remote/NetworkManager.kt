package com.sedi.viktor.learnAll.data.remote

import okhttp3.*
import java.io.IOException


class NetworkManager {

    val okHttpClient = OkHttpClient()
    val JSON = MediaType.parse("application/json; charset=utf-8")

    object Me {
        val instance = NetworkManager()
    }

    fun makeRequestPost(url: String, json: String, networkRequestCallback: networkRequestCallback) {
        val body = RequestBody.create(JSON, json)

        val request = Request.Builder().apply {
            url(url)
            post(body)
        }.build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                networkRequestCallback.onError(e!!)
            }

            override fun onResponse(call: Call?, response: Response?) {
                networkRequestCallback.onSucess(response!!)
            }

        })


    }

    fun makeRequestGet(url: String, networkRequestCallback: networkRequestCallback) {

    }


    interface networkRequestCallback {
        fun onSucess(responce: Response)
        fun onError(e: IOException)
    }
}