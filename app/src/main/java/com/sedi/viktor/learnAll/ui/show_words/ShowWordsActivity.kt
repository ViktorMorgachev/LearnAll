package com.sedi.viktor.learnAll.ui.show_words

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.extensions.invisible
import com.sedi.viktor.learnAll.extensions.visible
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.words_activity.*

class ShowWordsActivity : AppCompatActivity() {


    lateinit var items: ArrayList<WordItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.words_activity)
        setupViews()

        recycler_view.adapter = WordsRepositoryAdapter(items)

        val gridLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = gridLayoutManager

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
        if (items.size == 0)
            parent_empty_view.visible() else parent_empty_view.invisible()
    }
}