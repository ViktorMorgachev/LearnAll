package com.sedi.viktor.learnAll.ui.show_words

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.databinding.ItemWordLayoutBinding
import com.sedi.viktor.learnAll.models.WordItem

class WordsRepositoryAdapter(val items: List<WordItem>) :
    RecyclerView.Adapter<WordsRepositoryAdapter.WordsHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = WordsHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_word_layout,
            parent,
            false
        )
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: WordsHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class WordsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvOther = itemView.findViewById<TextView>(R.id.tv_other_name)
        val tvNativeName = itemView.findViewById<TextView>(R.id.tv_native_name)
        lateinit var  binding: ItemWordLayoutBinding

        fun bind(item: WordItem) {

            tvOther.text = item.otherName
            tvNativeName.text = item.nativeName

        }

    }

}