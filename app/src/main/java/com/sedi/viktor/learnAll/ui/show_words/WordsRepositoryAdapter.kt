package com.sedi.viktor.learnAll.ui.show_words

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.databinding.ItemWordBinding

class WordsRepositoryAdapter(
    private val items: List<WordItem>,
    private val clickCallback: WordsRepositoryAdapter.onClickCallback
) :
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

    inner class ClickHandler {
        fun onFavourite(view: ImageView) {
            (view as ImageView).setImageDrawable(
                view.resources.getDrawable(
                    R.drawable.ic_favorite,
                    view.context.theme
                )
            )
        }

        fun onNonFafourite(view: ImageView) {
            (view as ImageView).setImageDrawable(
                view.resources.getDrawable(
                    R.drawable.ic_favorite_border,
                    view.context.theme
                )
            )
        }

        fun onDelete(wordItem: WordItem) {
            clickCallback.onDelete(wordItem)
        }

        fun onEdit(wordItem: WordItem) {
            clickCallback.onEdit(wordItem)
        }

        fun onMenu(view: ImageView) {
            clickCallback.onMenu(view)
        }

    }


    interface onClickCallback {
        fun onDelete(wordItem: WordItem)
        fun onEdit(wordItem: WordItem)
        fun onMenu(view: ImageView)
    }


}