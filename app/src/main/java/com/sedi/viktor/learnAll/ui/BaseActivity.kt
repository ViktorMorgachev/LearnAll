package com.sedi.viktor.learnAll.ui

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class BaseActivity : AppCompatActivity() {
    protected var textToSpeech: TextToSpeech? = null

    fun initTTS() {
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                val ttsLang = textToSpeech?.setLanguage(Locale("cs"))
                if (ttsLang == TextToSpeech.LANG_MISSING_DATA) {
                    toast("TTS Language is unsupported")

                }
            } else toast("TTS initialization failed")
        })
    }

    protected fun toast(text: String, toastDuration: Int = Toast.LENGTH_SHORT) {

        when (toastDuration) {
            Toast.LENGTH_SHORT -> Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.let {
            it.stop()
            it.shutdown()
        }

    }
}