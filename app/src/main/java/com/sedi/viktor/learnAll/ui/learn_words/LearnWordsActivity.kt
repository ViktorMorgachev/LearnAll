package com.sedi.viktor.learnAll.ui.learn_words

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.DatabaseHelper
import com.sedi.viktor.learnAll.data.WordItemDatabase
import com.sedi.viktor.learnAll.data.interfaces.IActionCard
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.models.WordItemRoomModel
import com.sedi.viktor.learnAll.ui.BaseActivity
import com.sedi.viktor.learnAll.ui.dialogs.MessageBox

class LearnWordsActivity : BaseActivity(), LearnWordFragment.LearnCallback {


    // Callbacks
    override fun onNextCard() {

    }

    override fun onRotateCard() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLearnedCard() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFavouriteCard() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    object Starter {
        fun start(activity: BaseActivity) {
            val intent = Intent(activity, LearnWordsActivity::class.java)
            activity.startActivity(intent)
        }

    }

    companion object {
        private lateinit var fragmentManager: FragmentManager
        private var cards: ArrayList<WordItem> = ArrayList()
        // TODO привязать это значение к биндингу
        private var mode: Mode = Mode.Other
        private lateinit var fragmentTransaction: FragmentTransaction
    }

    private var db: WordItemDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.learn_word_activity)

        db = WordItemDatabase.invoke(this)

        fragmentTransaction = supportFragmentManager.beginTransaction()

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