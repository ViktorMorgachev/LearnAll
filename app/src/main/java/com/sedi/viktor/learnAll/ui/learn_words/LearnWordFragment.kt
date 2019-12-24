package com.sedi.viktor.learnAll.ui.learn_words

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.data.models.WordItem

class LearnWordFragment : Fragment() {

    private lateinit var learnListener: LearnCallback

    private lateinit var wordItem: WordItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.learn_item, container, false)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LearnCallback) {
            learnListener = context
        } else {
            Log.e("LearnAll", "Context must be implementing LearnCallback")
        }
    }

    interface LearnCallback {
        fun onNextCard()
        fun onRotateCard()
        fun onLearnedCard()
        fun onFavouriteCard()
    }
}