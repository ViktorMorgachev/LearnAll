package com.sedi.viktor.learnAll.ui.show_words

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.WordItemDatabase
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.models.WordItemRoomModel
import com.sedi.viktor.learnAll.extensions.invisible
import com.sedi.viktor.learnAll.extensions.visible
import com.sedi.viktor.learnAll.ui.dialogs.MessageBox
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.words_activity.*

class ShowWordsActivity : AppCompatActivity() {


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

        recycler_view.adapter = WordsRepositoryAdapter(items)

        val gridLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = gridLayoutManager

    }

    private fun cardsConvert(cards: ArrayList<WordItemRoomModel>) {

        // TODO бегаем по всему списку и конветируем данные
        // После обновляем список
        runOnUiThread {

            if (cards.size == 0) {
                parent_empty_view.visible()
            } else parent_empty_view.invisible()

        }

    }

    private fun setupViews() {
        appToolBar.apply {
            setTitle("Все слова")
            hideBackButton()
            onActionClick {

            }
        }
    }

    override fun onResume() {
        super.onResume()


    }
}