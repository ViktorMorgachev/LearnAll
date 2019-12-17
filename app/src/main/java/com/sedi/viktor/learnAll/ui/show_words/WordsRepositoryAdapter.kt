package com.sedi.viktor.learnAll.ui.show_words

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.databinding.ItemWordBinding

class WordsRepositoryAdapter(private val items: List<WordItem>) :
    RecyclerView.Adapter<WordsRepositoryAdapter.WordsHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WordsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWordBinding.inflate(inflater, parent, false)
        return WordsHolder(binding.root)
    }


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: WordsHolder, position: Int) {
        holder.binding!!.card = items[position]
    }

    inner class WordsHolder : RecyclerView.ViewHolder {
        constructor(itemView: View) : super(itemView) {
            this.binding = DataBindingUtil.bind<ItemWordBinding>(itemView)
            binding!!.executePendingBindings()
        }

        var binding: ItemWordBinding?
    }

}