package com.sedi.viktor.learnAll.ui.show_words

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sedi.viktor.learnAll.databinding.ItemWordLayoutBinding
import com.sedi.viktor.learnAll.models.WordItem

class WordsRepositoryAdapter(private val items: List<WordItem>) :
    RecyclerView.Adapter<WordsRepositoryAdapter.WordsHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WordsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWordLayoutBinding.inflate(inflater, parent, false)
        return WordsHolder(binding.root)
    }


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: WordsHolder, position: Int) {
        holder.binding!!.word = items[position]
    }

    inner class WordsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = DataBindingUtil.bind<ItemWordLayoutBinding>(itemView)
    }

}