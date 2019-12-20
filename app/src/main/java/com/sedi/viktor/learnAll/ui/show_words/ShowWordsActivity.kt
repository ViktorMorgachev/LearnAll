package com.sedi.viktor.learnAll.ui.show_words

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.DatabaseConverter
import com.sedi.viktor.learnAll.data.WordItemDatabase
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
    override fun onChangeFavorite(wordItem: WordItem) {
        this.selectedWordItem = wordItem
        Thread(updateCardRunnable).start()
    }

    override fun onMenu(view: View, wordItem: WordItem) {
        this.selectedWordItem = wordItem
        showPopupMenu(getPopupMenCard(view))
    }


    private var db: WordItemDatabase? = null
    private lateinit var popupMenuListener: PopupMenu.OnMenuItemClickListener
    private var selectedWordItem: WordItem? = null

    companion object {
        private var updateCardRunnable: Runnable? = null
        private var deleteWordRunnable: Runnable? = null
        private var getWordsRunnable: Runnable? = null
        private var cards: ArrayList<WordItem> = ArrayList()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = WordItemDatabase.invoke(this)

        setContentView(R.layout.words_activity)
        setupViews()
        initListeners()
        initRunnables()
        getWords()

        val gridLayoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = gridLayoutManager
    }

    private fun getWords() {
        cards.clear()
        Thread(getWordsRunnable).start()
    }

    private fun initRunnables() {

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

        if (deleteWordRunnable == null) {
            deleteWordRunnable = Runnable {
                Thread.currentThread().name = "Database Thread"
                try {

                    if (selectedWordItem != null) {
                        db!!.wordItemDao().delete(
                            DatabaseConverter.convertWordItemToRoomModel(
                                selectedWordItem!!
                            )
                        )
                        clearCard()
                        runOnUiThread {
                            toast("Успешно удалено")
                            getWords()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        MessageBox.show(
                            this,
                            "Ошибка удаления",
                            e.message ?: "Обратитесь к разработчику", this
                        )
                    }
                    clearCard()

                }

            }
        }

        if (updateCardRunnable == null) {
            updateCardRunnable = Runnable {
                Thread.currentThread().name = "Database Thread"
                try {

                    if (selectedWordItem != null) {
                        db!!.wordItemDao().update(
                            DatabaseConverter.convertWordItemToRoomModel(
                                selectedWordItem!!
                            )
                        )
                        clearCard()
                        runOnUiThread {
                            getWords()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        MessageBox.show(
                            this,
                            "Ошибка обновления",
                            e.message ?: "Обратитесь к разработчику", this
                        )
                    }
                    clearCard()

                }

            }
        }


    }

    private fun initListeners() {
        popupMenuListener = PopupMenu.OnMenuItemClickListener {
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
                R.id.menu_delete_card -> {
                    deleteCard()
                    true
                }
                R.id.menu_change_card -> {
                    editCard()
                    true
                }

                else -> false
            }
        }
    }

    private fun editCard() {

        // Запускаем активность c передачей ей карточки
        clearCard()

    }

    private fun deleteCard() {
        Thread(deleteWordRunnable).start()
    }

    private fun getPopupMenCard(targetView: View): PopupMenu {
        val popupMenu = PopupMenu(this, targetView)
        popupMenu.inflate(R.menu.popup_edit_menu_card)
        return popupMenu
    }


    private fun cardsConvert(card_items: ArrayList<WordItemRoomModel>) {

        for (wordItem in card_items) {
            cards.add(
                DatabaseConverter.convertRoomModelToWordItem(wordItem)
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
                val popupMenu = PopupMenu(context, getMenuItem())
                popupMenu.inflate(R.menu.popup_main_menu_card)
                showPopupMenu(popupMenu)
            }
        }
    }


    private fun showPopupMenu(popupMenu: PopupMenu) {
        popupMenu.setOnMenuItemClickListener(popupMenuListener)
        popupMenu.show()
    }

    private fun clearCard() {
        selectedWordItem = null
    }

    override fun onResume() {
        super.onResume()
        parent_empty_view.gone()
    }
}