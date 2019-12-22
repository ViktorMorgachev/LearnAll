package com.sedi.viktor.learnAll.ui.show_words

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.DatabaseHelper
import com.sedi.viktor.learnAll.data.WordItemDatabase
import com.sedi.viktor.learnAll.data.interfaces.IActionCard
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.models.WordItemRoomModel
import com.sedi.viktor.learnAll.extensions.gone
import com.sedi.viktor.learnAll.extensions.visible
import com.sedi.viktor.learnAll.ui.BaseActivity
import com.sedi.viktor.learnAll.ui.dialogs.MessageBox
import com.sedi.viktor.learnAll.ui.edit_word.EditWordActivity
import com.sedi.viktor.learnAll.ui.learn_words.LearnWordsActivity
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.words_activity.*

class ShowWordsActivity : BaseActivity(), WordsRepositoryAdapter.onClickCallback {

    // Callbacks
    override fun onChangeFavorite(wordItem: WordItem) {
        this.selectedWordItem = wordItem
        DatabaseHelper.asynkSaveOrUpdateWordItem(db!!, wordItem, object : IActionCard {
            override fun onError(exception: Exception) {
                runOnUiThread {
                    MessageBox.show(
                        this@ShowWordsActivity,
                        "Ошибка обновления",
                        exception.message ?: "Обратитесь к разработчику", this@ShowWordsActivity
                    )
                }
                clearCard()

            }

            override fun onComplete(
                data: WordItemRoomModel?,
                collectionData: ArrayList<WordItemRoomModel>?
            ) {
                clearCard()
                runOnUiThread {
                    getWords()
                }
            }

        })
    }

    override fun onMenu(view: View, wordItem: WordItem) {
        this.selectedWordItem = wordItem
        showPopupMenu(getPopupMenCard(view))
    }


    private var db: WordItemDatabase? = null
    private lateinit var popupMenuListener: PopupMenu.OnMenuItemClickListener
    private var selectedWordItem: WordItem? = null

    companion object {
        private var cards: ArrayList<WordItem> = ArrayList()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = WordItemDatabase.invoke(this)

        setContentView(R.layout.words_activity)
        setupViews()
        initListeners()


        val gridLayoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = gridLayoutManager
    }

    private fun getWords() {
        cards.clear()

        DatabaseHelper.asynkGetWords(db!!, object : IActionCard {
            override fun onComplete(
                data: WordItemRoomModel?,
                collectionData: ArrayList<WordItemRoomModel>?
            ) {
                try {
                    if (collectionData != null)
                        cardsConvert(
                            collectionData
                        )
                } catch (e: Exception) {
                    runOnUiThread {
                        MessageBox.show(
                            this@ShowWordsActivity,
                            "Ошибка получения карточек",
                            e.message ?: "Обратитесь к разработчику", this@ShowWordsActivity
                        )
                    }

                }
            }

            override fun onError(exception: Exception) {

                runOnUiThread {
                    MessageBox.show(
                        this@ShowWordsActivity,
                        "Ошибка получения карточек",
                        exception.message ?: "Обратитесь к разработчику", this@ShowWordsActivity
                    )
                }

            }


        })
    }


    private fun initListeners() {
        popupMenuListener = PopupMenu.OnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_learn -> {
                    LearnWordsActivity.Starter.start(this)
                    true
                }
                R.id.menu_all_cards -> {
                    getWords()
                    true
                }
                R.id.menu_add_card -> {
                    EditWordActivity.Starter.start(this)
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

        if (selectedWordItem != null)
            EditWordActivity.Starter.start(this, selectedWordItem!!.copy())

    }

    private fun deleteCard() {

        if (selectedWordItem != null)
            DatabaseHelper.asynkDeleteWordItem(
                db!!,
                selectedWordItem!!.copy(),
                object : IActionCard {
                    override fun onError(exception: Exception) {
                        runOnUiThread {
                            MessageBox.show(
                                this@ShowWordsActivity,
                                "Ошибка удаления",
                                exception.message ?: "Обратитесь к разработчику",
                                this@ShowWordsActivity
                            )
                        }
                        clearCard()
                    }

                    override fun onComplete(
                        data: WordItemRoomModel?,
                        collectionData: ArrayList<WordItemRoomModel>?
                    ) {
                        clearCard()
                        runOnUiThread {
                            toast("Успешно удалено")
                            getWords()
                        }
                    }
                })

    }

    private fun getPopupMenCard(targetView: View): PopupMenu {
        val popupMenu = PopupMenu(this, targetView)
        popupMenu.inflate(R.menu.popup_edit_menu_card)
        return popupMenu
    }


    private fun cardsConvert(card_items: ArrayList<WordItemRoomModel>) {

        for (wordItem in card_items) {
            cards.add(
                DatabaseHelper.convertRoomModelToWordItem(wordItem)
            )
        }
        // После обновляем список
        runOnUiThread {

            if (cards.size == 0) {
                parent_empty_view.visible()
            } else {
                recycler_view.adapter = WordsRepositoryAdapter(cards, this)
                parent_empty_view.gone()
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
        getWords()
        parent_empty_view.gone()
    }

}