package com.sedi.viktor.learnAll.ui.edit_word

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.sedi.viktor.learnAll.Color
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.DatabaseHelper
import com.sedi.viktor.learnAll.data.WordItemDatabase
import com.sedi.viktor.learnAll.data.interfaces.IActionCard
import com.sedi.viktor.learnAll.data.interfaces.TranslateResponseCallbackImpl
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.models.WordItemRoomModel
import com.sedi.viktor.learnAll.data.remote.YandexTranslater
import com.sedi.viktor.learnAll.databinding.WordEditLayoutActivityBinding
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


    //Data
    private var binding: WordEditLayoutActivityBinding? = null


    // Overide and callbacks
    override fun onColorChanged(color: Color) {

        when (modifyingCard) {
            ModifyingCard.NATIVE_CARD -> changeNativeColor(color)
            ModifyingCard.OTHER_CARD -> changeOtherColor(color)
            else -> TODO()
        }

    }

    override fun onTextStyleChanged(textStyle: TextStyle) {

    }

    override fun onSuccess(response: String) {

        runOnUiThread {
            iv_edit_native.isEnabled = true
            iv_edit_other.isEnabled = true
        }


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

        runOnUiThread {
            iv_edit_native.isEnabled = true
            iv_edit_other.isEnabled = true
        }


        if (e.message != null)
            Log.e("LearnAll", e.message!!)
    }


    object Starter {
        fun start(activity: BaseActivity) {
            val intent = Intent(activity, EditWordActivity::class.java)
            activity.startActivity(intent)
        }

        fun start(activity: BaseActivity, data: WordItem) {
            val intent = Intent(activity, EditWordActivity::class.java)
            wordItem = data
            activity.startActivity(intent)
        }
    }


    companion object {
        private const val RC_HANDLE_RECORD_AUDIO_PERMISSION = 4
        private const val REQ_CODE_SPEECH_INPUT = 5
        private var wordItem = WordItem()
        private lateinit var nativeTextChangeListener: TextWatcher
        private lateinit var otherTextChangeListener: TextWatcher
    }


    private var modifyingCard = ModifyingCard.NONE
    private var modifyingItem = ModifyingItem.NONE
    private var direction = Direction.DEFAULT
    private var db: WordItemDatabase? = null
    private lateinit var yandexTranslater: YandexTranslater


    private fun changeOtherColor(color: Color?) {

        when (modifyingItem) {
            ModifyingItem.CARD -> {
                wordItem.getCardStateOther().setBackColor(color!!.name)
            }
            ModifyingItem.TEXT -> {
                wordItem.getCardStateOther().setTextColor(color!!.name)
            }
            ModifyingItem.NONE -> TODO()
        }


    }

    private fun changeNativeColor(color: Color?) {

        when (modifyingItem) {
            ModifyingItem.CARD -> {
                wordItem.getCardStateOther().setBackColor(color!!.name)
            }
            ModifyingItem.TEXT -> {
                wordItem.getCardStateNative().setTextColor(color!!.name)
            }
            ModifyingItem.NONE -> TODO()
        }


    }


    private fun initNative(response: String) {

        wordItem.setNativeName(response)
        binding?.executePendingBindings()

        runOnUiThread {
            iv_translate_native.isEnabled = true
            iv_translate_other.isEnabled = true
        }

    }


    private fun initOthers(response: String) {

        sayText(response)

        wordItem.setOtherName(response)

        runOnUiThread {
            iv_translate_other.isEnabled = true
            iv_translate_native.isEnabled = true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.word_edit_layout_activity
        )
        binding?.card = wordItem
        binding?.executePendingBindings()

        db = WordItemDatabase.invoke(this)

        if (textToSpeech == null)
            initTTS()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        initViewListeners()
        setupViews()
        yandexTranslater = YandexTranslater(this)

    }


    private fun setupViews() {

        appToolBar.apply {
            setTitle("Редактор карточки")
            showBackButton()
            onActionClick {
                toast("На главную")
            }
            onBackClick {
                onBackPressed()
            }
        }

    }


    private fun initViewListeners() {


        otherTextChangeListener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                iv_translate_other.isEnabled = !TextUtils.isEmpty(et_word_other.text)

                wordItem.setOtherName(tv_card_other.text.toString())

                if (direction == Direction.SPEAK_OTHER && !TextUtils.isEmpty(wordItem.getOtherName())) {
                    translateOther()
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

        nativeTextChangeListener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                iv_translate_native.isEnabled = !TextUtils.isEmpty(et_word_native.text)

                wordItem.setNativeName(tv_card_native.text.toString())

                if (direction == Direction.SPEAK_NATIVE && !TextUtils.isEmpty(wordItem.getNativeName())) {
                    translateNative()
                }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

        et_word_other.addTextChangedListener(otherTextChangeListener)
        et_word_native.addTextChangedListener(nativeTextChangeListener)

    }


    fun OnTranslateOther(view: View) {
        translateOther()
        view.isEnabled = false
    }

    private fun translateOther() {

        runOnUiThread {
            iv_edit_native.isEnabled = false
            iv_edit_other.isEnabled = false
        }

        wordItem.setOtherName(et_word_other.text.toString())
        direction = Direction.TRANSLATE_TO_NATIVE
        yandexTranslater.translate(
            "en",
            "ru",
            wordItem.getOtherName(),
            this@EditWordActivity,
            this@EditWordActivity
        )
    }


    fun OnTranslateNative(view: View) {

        translateNative()
        view.isEnabled = false

    }

    private fun translateNative() {

        runOnUiThread {
            iv_edit_native.isEnabled = false
            iv_edit_other.isEnabled = false
        }

        wordItem.setNativeName(et_word_native.text.toString())

        direction = Direction.TRANSLATE_TO_OTHER

        yandexTranslater.translate(
            "ru",
            "en",
            wordItem.getNativeName(),
            this@EditWordActivity,
            this@EditWordActivity
        )
    }


    @SuppressWarnings
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


    @SuppressWarnings
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
                    wordItem.setNativeName(text)
                }
                Direction.SPEAK_OTHER -> {
                    wordItem.setOtherName(text)
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
            else -> {//TODO
            }
        }

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажи что хотел")
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
    }


    private fun showPopupMenu(targetView: View) {
        val popupMenu = PopupMenu(this, targetView)
        popupMenu.inflate(R.menu.popup_edit_card)

        popupMenu.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.menu_edit_card_color -> showChangeColorDialog(ModifyingItem.CARD)
                R.id.menu_edit_text_color -> showChangeColorDialog(ModifyingItem.TEXT)
                else -> false
            }
        }
        popupMenu.show()

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

    public fun SaveWord(view: View) {


        if (TextUtils.isEmpty(wordItem.getNativeName()) || TextUtils.isEmpty(wordItem.getOtherName())) return



        DatabaseHelper.asynkSaveOrUpdateWordItem(db!!, wordItem.copy(), object : IActionCard {
            override fun onComplete(
                data: WordItemRoomModel?,
                collectionData: ArrayList<WordItemRoomModel>?
            ) {
                runOnUiThread {
                    toast("Успешно сохранено", Toast.LENGTH_LONG)
                    wordItem = WordItem()
                }
            }

            override fun onError(exception: Exception) {
                runOnUiThread {
                    MessageBox.show(
                        this@EditWordActivity,
                        "Ошибка добавления",
                        exception.message ?: "Обратитесь к разработчику", this@EditWordActivity
                    )
                }
            }

        })


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

    override fun onDestroy() {
        super.onDestroy()
        wordItem = WordItem()
    }
}