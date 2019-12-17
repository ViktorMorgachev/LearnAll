package com.sedi.viktor.learnAll.ui.show_words

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.WordItemDatabase
import com.sedi.viktor.learnAll.data.models.CardState
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.models.WordItemRoomModel
import com.sedi.viktor.learnAll.extensions.invisible
import com.sedi.viktor.learnAll.extensions.visible
import com.sedi.viktor.learnAll.ui.BaseActivity
import com.sedi.viktor.learnAll.ui.dialogs.MessageBox
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.words_activity.*

class ShowWordsActivity : BaseActivity() {


    private lateinit var items: ArrayList<WordItem>
    private var db: WordItemDatabase? = null

    companion object {
        private var getWordsRunnable: Runnable? = null
        private var cards: ArrayList<WordItem> = ArrayList()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = WordItemDatabase.invoke(this)

        setContentView(R.layout.words_activity)
        setupViews()


        if (getWordsRunnable == null) {
            getWordsRunnable = Runnable {
                Thread.currentThread().name = "Database Thread"
                try {
                    cardsConvert(
                        db!!.wordItemDao()
                            .getAll() as ArrayList<WordItemRoomModel>
                    )


                } catch (e: Exception) {
                    runOnUiThread {
                        MessageBox.show(
                            this,
                            "Ошибка получения карточек",
                            e.message ?: "Обратитесь к разработчику", this
                        )
                    }

                }

            }
        }


        val gridLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = gridLayoutManager

    }

    private fun cardsConvert(cards: ArrayList<WordItemRoomModel>) {

        val newCards = ArrayList<WordItem>()

        for (wordItem in cards) {
            newCards.add(
                WordItem(
                    wordItem.learned,
                    wordItem.otherName,
                    wordItem.nativeName, false,
                    CardState(wordItem.cardNativeBackGround, wordItem.cardNativeTextColor),
                    CardState(wordItem.cardOtherBackGround, wordItem.cardOtheTextColor)
                )
            )
        }

        // После обновляем список
        runOnUiThread {

            if (cards.size == 0) {
                parent_empty_view.visible()
            } else {
                recycler_view.adapter = WordsRepositoryAdapter(newCards)
                parent_empty_view.invisible()
            }

        }

    }

    private fun setupViews() {
        appToolBar.apply {
            setTitle("Все слова")
            onBackClick {
                toast("На главную")
            }
            onActionClick {
                toast("Назад")
            }
        }
    }

    override fun onResume() {
        super.onResume()


    }
}