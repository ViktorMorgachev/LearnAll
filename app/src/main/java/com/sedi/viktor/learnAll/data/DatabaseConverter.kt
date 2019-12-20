package com.sedi.viktor.learnAll.data

import com.sedi.viktor.learnAll.data.models.CardState
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.models.WordItemRoomModel

class DatabaseConverter {

    companion object {
        fun convertWordItemToRoomModel(wordItem: WordItem): WordItemRoomModel {
            val wordItemRoomModel = WordItemRoomModel().apply {
                learned = wordItem.learned
                nativeName = wordItem.nativeName
                otherName = wordItem.otherName
                cardNativeBackGround = wordItem.cardStateNative.backColor
                cardNativeTextColor = wordItem.cardStateNative.textColor
                cardOtheTextColor = wordItem.cardStateOther.textColor
                cardOtherBackGround = wordItem.cardStateOther.backColor
                favourite = wordItem.favourite
            }
            return wordItemRoomModel
        }

        fun convertRoomModelToWordItem(wordItemRoomModel: WordItemRoomModel): WordItem {

            return WordItem(
                wordItemRoomModel.learned,
                wordItemRoomModel.otherName,
                wordItemRoomModel.nativeName,
                wordItemRoomModel.favourite,
                CardState(
                    wordItemRoomModel.cardNativeBackGround,
                    wordItemRoomModel.cardNativeTextColor
                ),
                CardState(
                    wordItemRoomModel.cardOtherBackGround,
                    wordItemRoomModel.cardOtheTextColor
                )
            )

        }
    }

}