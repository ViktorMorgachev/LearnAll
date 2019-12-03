package com.sedi.viktor.learnAll.data.remote

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http2.Header
import java.io.IOException


class NetworkManager {

    private val okHttpClient = OkHttpClient()

    object Me {
        val instance = NetworkManager()
    }

    fun makePostRequest(
        url: String,
        networkRequestCallback: networkRequestCallback?,
        text: String
    ) {

        val mediaType = text.toMediaTypeOrNull()
        val body = "text=${text}".toRequestBody(mediaType)

        val request = Request.Builder().apply {
            url(url)
            addHeader("Content-Type", "application/x-www-form-urlencoded")
            post(body)
        }.build()



        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                networkRequestCallback?.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                networkRequestCallback?.onSucess(response)
            }

        })
    }

    fun makeGetRequest(url: String, networkRequestCallback: networkRequestCallback) {

    }


    interface networkRequestCallback {
        fun onSucess(responce: Response)
        fun onError(e: IOException)
    }

    class RequestBodyBuilder {

        fun getMediaType(string: String): MediaType? = string.toMediaTypeOrNull()

        fun getBody(mediaType: MediaType, text: String): RequestBody = text.toRequestBody(mediaType)

        fun getHeader(type: String, value: String): Header = Header(type, value)

    }
}