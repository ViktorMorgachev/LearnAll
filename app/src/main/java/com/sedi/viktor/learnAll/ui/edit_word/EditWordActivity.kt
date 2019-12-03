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
            Direction.TO_OTHER -> initOthers(result)
            Direction.TO_NATIVE -> initNative(result)
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
        if (e.message != null)
            Log.e("LearnAll", e.message!!)
    }

    companion object {
        var direction = Direction.DEFAULT
        const val RC_HANDLE_RECORD_AUDIO_PERMISSION = 4
        const val REQ_CODE_SPEECH_INPUT = 5;
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

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        et_word_native.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                iv_translate_native.isEnabled = !TextUtils.isEmpty(et_word_native.text)

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

        et_card_other.text = et_word_other.text.toString()

        direction = Direction.TO_NATIVE

        yandexTranslater.translate(
            "cs",
            "ru",
            et_word_other.text.toString(),
            this@EditWordActivity,
            this@EditWordActivity
        )


        view.isEnabled = false


    }


    fun OnTranslateNative(view: View) {

        et_card_native.text = et_word_native.text.toString()

        direction = Direction.TO_OTHER

        yandexTranslater.translate(
            "ru",
            "cs",
            et_word_native.text.toString(),
            this@EditWordActivity,
            this@EditWordActivity
        )


        view.isEnabled = false


    }

    enum class Direction {
        TO_OTHER,
        TO_NATIVE,
        DEFAULT
    }

    fun onVoiceInputNative(view: View) {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                this,
                R.string.no_recognition_available,
                Toast.LENGTH_LONG
            ).show()
        }
        direction = Direction.TO_NATIVE
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

        direction = Direction.TO_OTHER
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

            when (direction) {


                Direction.TO_NATIVE -> {
                    et_card_native.text =
                        data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
                }
                Direction.TO_OTHER -> {
                    et_card_other.text =
                        data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
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
            Direction.TO_NATIVE -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru")
            Direction.TO_OTHER -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "cs")
        }

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажи что хотел")
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
    }

}