package com.sedi.viktor.learnAll.ui.edit_word

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import com.sedi.viktor.learnAll.Color
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.convertColorStringToInt
import com.sedi.viktor.learnAll.data.DatabaseConverter
import com.sedi.viktor.learnAll.data.WordItemDatabase
import com.sedi.viktor.learnAll.data.interfaces.TranslateResponseCallbackImpl
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.remote.YandexTranslater
import com.sedi.viktor.learnAll.ui.BaseActivity
import com.sedi.viktor.learnAll.ui.dialogs.DialogColorChooser
import com.sedi.viktor.learnAll.ui.dialogs.MessageBox
import com.sedi.viktor.learnAll.ui.edit_word.listeners.ChangeColorListener
import com.sedi.viktor.learnAll.ui.edit_word.listeners.ChangeStyleListener
import kotlinx.android.synthetic.main.word_edit_layout_activity.*
import org.json.JSONException
import org.json.JSONObject
import java.time.format.TextStyle


class EditWordActivity : BaseActivity(), LifecycleOwner, TranslateResponseCallbackImpl,
    ChangeColorListener, ChangeStyleListener {


    companion object {
        private const val RC_HANDLE_RECORD_AUDIO_PERMISSION = 4
        private const val REQ_CODE_SPEECH_INPUT = 5
        private var wordItem = WordItem()

        private var saveWordRunnable: Runnable? = null
        private val handler = Handler()
    }


    private var modifyingCard = ModifyingCard.NONE
    private var modifyingItem = ModifyingItem.NONE
    private var direction = Direction.DEFAULT
    private var db: WordItemDatabase? = null
    private lateinit var alertDialog: AlertDialog
    private lateinit var yandexTranslater: YandexTranslater


    // Ovveride and callbacks
    override fun onColorChanged(color: Color) {


        when (modifyingCard) {
            ModifyingCard.NATIVE_CARD -> changeNativeColor(color)
            ModifyingCard.OTHER_CARD -> changeOtherColor(color)
            else -> TODO()
        }


    }

    private fun changeOtherColor(color: Color?) {

        when (modifyingItem) {
            ModifyingItem.CARD -> {
                iv_word_other.setBackgroundColor(getColor(color!!.color))
                wordItem.cardStateOther.backColor = color.name
            }
            ModifyingItem.TEXT -> {
                tv_card_other.setTextColor(getColor(color!!.color))
                wordItem.cardStateOther.textColor = color.name
            }
            ModifyingItem.NONE -> TODO()
        }


    }

    private fun changeNativeColor(color: Color?) {

        when (modifyingItem) {
            ModifyingItem.CARD -> {
                iv_word_native.setBackgroundColor(getColor(color!!.color))
                wordItem.cardStateNative.backColor = color.name
            }
            ModifyingItem.TEXT -> {
                tv_card_native.setTextColor(getColor(color!!.color))
                wordItem.cardStateNative.textColor = color.name
            }
        }


    }

    override fun onTextStyleChanged(textStyle: TextStyle) {

    }

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
            else -> TODO()
        }


    }

    override fun onFaillure(e: Exception) {

        iv_edit_native.isEnabled = true
        iv_edit_other.isEnabled = true

        if (e.message != null)
            Log.e("LearnAll", e.message!!)
    }


    private fun initNative(response: String) {

        wordItem.nativeName = response

        iv_translate_native.isEnabled = true
        et_word_native.post { et_word_native.setText(response) }
        tv_card_native.post { tv_card_native.setText(response) }

    }


    private fun initOthers(response: String) {

        sayText(response)

        wordItem.otherName = response

        iv_translate_other.isEnabled = true
        et_word_other.post { et_word_other.setText(response) }
        tv_card_other.post { tv_card_other.setText(response) }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = WordItemDatabase.invoke(this)


        if (saveWordRunnable == null) {
            saveWordRunnable = Runnable {
                Thread.currentThread().name = "Database Thread"
                try {
                    db!!.wordItemDao()
                        .insert(DatabaseConverter.convertWordItemToRoomModel(wordItem))
                    runOnUiThread {
                        toast("Успешно сохранено", Toast.LENGTH_LONG)
                        resetState()
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        MessageBox.show(
                            this,
                            "Ошибка добавления",
                            e.message ?: "Обратитесь к разработчику", this
                        )
                    }

                }

            }
        }

        if (textToSpeech == null)
            initTTS()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        setContentView(R.layout.word_edit_layout_activity)
        iv_word_native.post { iv_edit_native.setBackgroundColor(Color.DEFAULT.color) }
        iv_word_other.post { iv_edit_other.setBackgroundColor(Color.DEFAULT.color) }

        initViewListeners()

        yandexTranslater = YandexTranslater(this)

    }

    private fun initViewListeners() {

        et_word_other.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                iv_translate_other.isEnabled = !TextUtils.isEmpty(et_word_other.text)
                if (direction == Direction.SPEAK_OTHER && !TextUtils.isEmpty(et_word_other.text)) {

                    wordItem.otherName = tv_card_other.text.toString()

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
                if (direction == Direction.SPEAK_NATIVE && !TextUtils.isEmpty(tv_card_native.text)) {

                    wordItem.nativeName = tv_card_native.text.toString()

                    translateNative()
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


    }


    fun OnTranslateOther(view: View) {
        translateOther()
        view.isEnabled = false
    }

    private fun translateOther() {

        iv_edit_native.isEnabled = false
        iv_edit_other.isEnabled = false

        tv_card_other.text = et_word_other.text.toString()

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



        tv_card_native.text = et_word_native.text.toString()

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
                    tv_card_native.text = text
                    et_word_native.setText(text)
                }
                Direction.SPEAK_OTHER -> {
                    tv_card_other.text = text
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


    fun showPopupMenu(targetView: View) {
        val popupMenu = PopupMenu(this, targetView)
        popupMenu.inflate(R.menu.popup_edit_card)

        popupMenu.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.menu_edit_card_color -> showChangeColorDialog(ModifyingItem.CARD)
                R.id.menu_edit_text_color -> showChangeColorDialog(ModifyingItem.TEXT)
                R.id.menu_edit_text_font -> showEditFontdialog()
                else -> false
            }
        }
        popupMenu.show()

    }

    private fun showEditFontdialog(): Boolean {

        alertDialog = AlertDialog.Builder(this).create()

        return false
    }

    private fun showChangeColorDialog(modifyingItem: ModifyingItem): Boolean {

        this.modifyingItem = modifyingItem

        val dialogColorChooser = DialogColorChooser(
            this,
            this
        )

        dialogColorChooser.setTitle(R.string.select_color)
        dialogColorChooser.setPositiveButton(
            android.R.string.ok
        ) { _, _ ->
            dialogColorChooser.dismiss()
        }


        dialogColorChooser.setOnDismissListener { onDismisedColorChoosedDialog() }

        dialogColorChooser.show()

        return false
    }

    fun SaveWord(view: View) {
        if (TextUtils.isEmpty(et_word_other.text)) return
        //  handler.post(saveWordRunnable)

        Thread(saveWordRunnable).start()

    }

    private fun resetState() {

        wordItem = WordItem()

        et_word_other.setText(wordItem.otherName)
        tv_card_other.text = wordItem.nativeName
        et_word_native.setText(wordItem.nativeName)
        tv_card_native.text = wordItem.nativeName
        modifyingItem = ModifyingItem.NONE
        direction = Direction.DEFAULT
        modifyingCard = ModifyingCard.NONE



        iv_edit_native.setBackgroundColor(
            convertColorStringToInt(
                wordItem.cardStateNative.backColor,
                defaultColor = Color.GRAY.color
            )
        )
        iv_edit_other.setBackgroundColor(
            convertColorStringToInt(
                wordItem.cardStateOther.backColor,
                defaultColor = Color.GRAY.color
            )
        )

        // Осталось дописать сброс цвета для текста
        tv_card_other.setTextColor(
            convertColorStringToInt(
                wordItem.cardStateNative.textColor,
                defaultColor = Color.BLACK.color
            )
        )
        tv_card_native.setTextColor(
            convertColorStringToInt(
                wordItem.cardStateOther.textColor,
                defaultColor = Color.BLACK.color
            )
        )


    }

    private fun onDismisedColorChoosedDialog() {
        modifyingCard = ModifyingCard.NONE
        modifyingItem = ModifyingItem.NONE
    }


    fun OnEditCardOther(view: View) {

        modifyingCard = ModifyingCard.OTHER_CARD
        showPopupMenu(view)
    }

    fun OnEditCardNative(view: View) {


        modifyingCard = ModifyingCard.NATIVE_CARD
        showPopupMenu(view)

    }

    fun sayText(response: String) {
        val utteranceId = this.hashCode().toString()
        val speechStatus =
            textToSpeech?.speak(response, TextToSpeech.QUEUE_FLUSH, null, utteranceId)

    }


    enum class ModifyingCard {
        NATIVE_CARD,
        OTHER_CARD,
        NONE
    }

    enum class Direction {
        SPEAK_OTHER,
        SPEAK_NATIVE,
        TRANSLATE_TO_OTHER,
        TRANSLATE_TO_NATIVE,
        DEFAULT
    }

    enum class ModifyingItem {
        CARD,
        TEXT,
        NONE
    }


}