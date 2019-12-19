package com.sedi.viktor.learnAll.ui.show_words

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.WordItemDatabase
import com.sedi.viktor.learnAll.data.models.CardState
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.models.WordItemRoomModel
import com.sedi.viktor.learnAll.extensions.gone
import com.sedi.viktor.learnAll.extensions.invisible
import com.sedi.viktor.learnAll.extensions.visible
import com.sedi.viktor.learnAll.ui.BaseActivity
import com.sedi.viktor.learnAll.ui.dialogs.MessageBox
import com.sedi.viktor.learnAll.ui.edit_word.EditWordActivity
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.words_activity.*

class ShowWordsActivity : BaseActivity(), WordsRepositoryAdapter.onClickCallback {

    // Callbacks
    override fun onMenu(view: ImageView) {
        toast("Меню")
    }

    override fun onDelete(wordItem: WordItem) {
        toast("Удалить")
    }

    override fun onEdit(wordItem: WordItem) {
        toast("Изменить")
    }


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


        val gridLayoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = gridLayoutManager
    }


    private fun cardsConvert(card_items: ArrayList<WordItemRoomModel>) {


        for (wordItem in card_items) {
            cards.add(
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
                recycler_view.adapter = WordsRepositoryAdapter(cards, this)
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
                showPopupMenu(appToolBar.getMenuItem())
            }
        }


    }

    // TODO изменение реализации, передавать уже готовое меню
    private fun showPopupMenu(targetView: View) {


        val popupMenu = PopupMenu(this, targetView)
        popupMenu.inflate(R.menu.popup_main_menu_card)

        popupMenu.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.menu_learn -> {
                    toast("Учить")
                    true
                }
                R.id.menu_all_cards -> {
                    toast("Обновить")
                    true
                }
                R.id.menu_add_card -> {
                    startActivity(Intent(this, EditWordActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onResume() {
        super.onResume()
        parent_empty_view.gone()
        // TODO тут нужно анимацию предразгрузки слов с БД
        Thread(getWordsRunnable).start()

    }
}