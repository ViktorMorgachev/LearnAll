package com.sedi.viktor.learnAll.ui.edit_word

import android.os.Bundle
import android.text.Editable
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

class EditWordActivity : AppCompatActivity(), LifecycleOwner, TranslateResponseCallbackImpl {


    override fun onSuccess(response: String) {

        when (direction) {
            Direction.TO_NATIVE -> initOthers(response)
            Direction.TO_OTHER -> initNative(response)
        }

    }

    private fun initNative(response: String) {

        et_word_native.setText(response)
        et_card_native.setText(response)

    }

    private fun initOthers(response: String) {


    }

    override fun onFaillure(e: Exception) {
        if (e.message != null)
            Log.e("LearnAll", e.message!!)
    }

    companion object {
        lateinit var direction: Direction
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
        et_word_native.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                direction = Direction.TO_OTHER

                et_card_native.setText(s.toString())

                yandexTranslater.translate(
                    "ru",
                    "cs",
                    s.toString(),
                    this@EditWordActivity,
                    this@EditWordActivity
                )
            }
        })

        et_word_other.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                direction = Direction.TO_NATIVE

                et_card_other.setText(s.toString())

                yandexTranslater.translate(
                    "cs",
                    "ru",
                    s.toString(),
                    this@EditWordActivity,
                    this@EditWordActivity
                )
            }
        })
    }

    fun SaveWord(view: View) {

    }

    enum class Direction {
        TO_OTHER,
        TO_NATIVE
    }

}