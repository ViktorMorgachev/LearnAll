package com.sedi.viktor.learnAll.ui.edit_word

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.interfaces.TranslateResponseCallbackImpl
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.remote.YandexTranslater
import kotlinx.android.synthetic.main.word_edit_layout_activity.*
import org.json.JSONException
import org.json.JSONObject


class EditWordActivity : AppCompatActivity(), LifecycleOwner, TranslateResponseCallbackImpl {


    override fun onSuccess(response: String) {

        iv_edit_native.isEnabled = true
        iv_edit_other.isEnabled = true

        val jObject = JSONObject(response)
        val jArray = jObject.getJSONArray("text")

        var result: String = ""
        for (i in 0 until jArray.length()) {
            try {
                result += jArray.getString(i)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        when (direction) {
            Direction.TRANSLATE_TO_OTHER -> initOthers(result)
            Direction.TRANSLATE_TO_NATIVE -> initNative(result)
        }


    }


    private fun initNative(response: String) {

        iv_translate_native.isEnabled = true
        et_word_native.post { et_word_native.setText(response) }
        et_card_native.post { et_card_native.setText(response) }

    }


    private fun initOthers(response: String) {

        iv_translate_other.isEnabled = true
        et_word_other.post { et_word_other.setText(response) }
        et_card_other.post { et_card_other.setText(response) }

    }

    override fun onFaillure(e: Exception) {

        iv_edit_native.isEnabled = true
        iv_edit_other.isEnabled = true

        if (e.message != null)
            Log.e("LearnAll", e.message!!)
    }

    companion object {
        var direction = Direction.DEFAULT
        const val RC_HANDLE_RECORD_AUDIO_PERMISSION = 4
        const val REQ_CODE_SPEECH_INPUT = 5
    }


    var wordItem = WordItem("", "")
    lateinit var yandexTranslater: YandexTranslater


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.word_edit_layout_activity)

        initViewListeners()

        yandexTranslater = YandexTranslater(this)

    }

    private fun initViewListeners() {

        et_word_other.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                iv_translate_other.isEnabled = !TextUtils.isEmpty(et_word_other.text)
                if (direction == Direction.SPEAK_OTHER) {
                    translateOther()
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        et_word_native.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                iv_translate_native.isEnabled = !TextUtils.isEmpty(et_word_native.text)
                if (direction == Direction.SPEAK_NATIVE) {
                    translateNative()
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    fun SaveWord(view: View) {

    }

    fun OnTranslateOther(view: View) {
        translateOther()
        view.isEnabled = false
    }

    private fun translateOther() {

        iv_edit_native.isEnabled = false
        iv_edit_other.isEnabled = false

        et_card_other.text = et_word_other.text.toString()

        direction = Direction.TRANSLATE_TO_NATIVE

        yandexTranslater.translate(
            "cs",
            "ru",
            et_word_other.text.toString(),
            this@EditWordActivity,
            this@EditWordActivity
        )
    }


    fun OnTranslateNative(view: View) {

        translateNative()

        view.isEnabled = false


    }

    private fun translateNative() {

        iv_edit_native.isEnabled = false
        iv_edit_other.isEnabled = false

        et_card_native.text = et_word_native.text.toString()

        direction = Direction.TRANSLATE_TO_OTHER

        yandexTranslater.translate(
            "ru",
            "cs",
            et_word_native.text.toString(),
            this@EditWordActivity,
            this@EditWordActivity
        )
    }


    fun onVoiceInputNative(view: View) {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                this,
                R.string.no_recognition_available,
                Toast.LENGTH_LONG
            ).show()
        }
        direction = Direction.SPEAK_NATIVE
        checkVoicePermissions()

    }


    private fun checkVoicePermissions() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_RECORD_AUDIO_PERMISSION)
        }
    }


    fun onVoiceInputOther(view: View) {


        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                this,
                R.string.no_recognition_available,
                Toast.LENGTH_LONG
            ).show()
        }

        direction = Direction.SPEAK_OTHER
        checkVoicePermissions()

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode != RC_HANDLE_RECORD_AUDIO_PERMISSION)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_HANDLE_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Maybe Start Speech recognithion
                startSpeechInput()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {


            var text = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
            when (direction) {
                Direction.SPEAK_NATIVE -> {
                    et_card_native.text = text
                    et_word_native.setText(text)
                }
                Direction.SPEAK_OTHER -> {
                    et_card_other.text = text
                    et_word_other.setText(text)
                }
                else -> return
            }
        }
    }

    private fun startSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )


        when (direction) {
            Direction.SPEAK_NATIVE -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru")
            Direction.SPEAK_OTHER -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "cs")
        }

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажи что хотел")
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
    }

    enum class Direction {
        SPEAK_OTHER,
        SPEAK_NATIVE,
        TRANSLATE_TO_OTHER,
        TRANSLATE_TO_NATIVE,
        DEFAULT
    }

}