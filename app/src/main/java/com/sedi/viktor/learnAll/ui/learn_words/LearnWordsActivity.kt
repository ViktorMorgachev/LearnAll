package com.sedi.viktor.learnAll.ui.learn_words

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.DatabaseHelper
import com.sedi.viktor.learnAll.data.WordItemDatabase
import com.sedi.viktor.learnAll.data.interfaces.IActionCard
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.models.WordItemRoomModel
import com.sedi.viktor.learnAll.ui.BaseActivity
import com.sedi.viktor.learnAll.ui.dialogs.MessageBox

class LearnWordsActivity : BaseActivity() {

    object Starter {
        fun start(activity: BaseActivity) {
            val intent = Intent(activity, LearnWordsActivity::class.java)
            activity.startActivity(intent)
        }

    }

    companion object {
        private var cards: ArrayList<WordItem> = ArrayList()
        private lateinit var mode: Mode
    }

    private var db: WordItemDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.learn_word_activity)

        db = WordItemDatabase.invoke(this)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        DatabaseHelper.asynkGetWords(db!!, object : IActionCard {
            override fun onError(exception: Exception) {
                runOnUiThread {
                    MessageBox.show(
                        this@LearnWordsActivity,
                        "Ошибка извлечения",
                        exception.message ?: "Обратитесь к разработчику", this@LearnWordsActivity
                    )
                }
            }

            override fun onComplete(
                data: WordItemRoomModel?,
                collectionData: ArrayList<WordItemRoomModel>?
            ) {
                if (collectionData != null)
                    for (wordItem in collectionData) {
                        cards.add(
                            DatabaseHelper.convertRoomModelToWordItem(wordItem)
                        )
                    }
            }

        })
    }


    enum class Mode {
        Native,
        Other,
        Random
    }


}