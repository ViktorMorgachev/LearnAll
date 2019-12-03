package com.sedi.viktor.learnAll.ui.edit_word

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
        var notListen: Boolean = false
        var direction = Direction.DEFAULT
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

}