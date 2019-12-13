package com.sedi.viktor.learnAll.data

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
            }
            return wordItemRoomModel
        }

        fun convertRoomModelToWordItem(wordItemRoomModel: WordItemRoomModel): WordItem? {
            return null
        }
    }

}