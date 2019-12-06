package com.sedi.viktor.learnAll.ui.show_words

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.models.CardState
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.databinding.WordsActivityBinding
import kotlinx.android.synthetic.main.words_activity.*

class ShowWordsActivity : AppCompatActivity() {


    private val items = listOf(
        WordItem("Girl", "Девочка", CardState(), CardState())
    )

    lateinit var binding: WordsActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.words_activity)

        recycler_view.adapter = WordsRepositoryAdapter(items)
        recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


    }


}