package com.sedi.viktor.learnAll.data

import com.sedi.viktor.learnAll.data.interfaces.IActionCard
import com.sedi.viktor.learnAll.data.models.CardState
import com.sedi.viktor.learnAll.data.models.WordItem
import com.sedi.viktor.learnAll.data.models.WordItemRoomModel

class DatabaseHelper {

    companion object {
        fun convertWordItemToRoomModel(wordItem: WordItem): WordItemRoomModel {
            val wordItemRoomModel = WordItemRoomModel().apply {
                primaryKey = wordItem.primaryKey
                learned = wordItem.getLearned()
                nativeName = wordItem.getNativeName()
                otherName = wordItem.getOtherName()
                cardNativeBackGround = wordItem.getCardStateNative().getBackColor()
                cardNativeTextColor = wordItem.getCardStateNative().getTextColor()
                cardOtheTextColor = wordItem.getCardStateOther().getTextColor()
                cardOtherBackground = wordItem.getCardStateOther().getBackColor()
                favourite = wordItem.getFavourite()
            }
            return wordItemRoomModel
        }

        fun convertRoomModelToWordItem(wordItemRoomModel: WordItemRoomModel): WordItem {

            return WordItem(
                wordItemRoomModel.primaryKey,
                wordItemRoomModel.learned,
                wordItemRoomModel.otherName,
                wordItemRoomModel.nativeName,
                wordItemRoomModel.favourite,
                CardState(
                    wordItemRoomModel.cardNativeBackGround,
                    wordItemRoomModel.cardNativeTextColor
                ),
                CardState(
                    wordItemRoomModel.cardOtherBackground,
                    wordItemRoomModel.cardOtheTextColor
                )
            )

        }

        fun asynkSaveOrUpdateWordItem(
            db: WordItemDatabase,
            wordItem: WordItem,
            iActionCard: IActionCard
        ) {
            Thread(Runnable {

                Thread.currentThread().name = "Database Thread"

                if (db.wordItemDao().getByID(wordItem.primaryKey) != null) {
                    try {
                        db.wordItemDao().update(convertWordItemToRoomModel(wordItem))
                        iActionCard.onComplete()
                    } catch (e: Exception) {

                        if (e.message != null) {
                            iActionCard.onError(
                                Exception(
                                    e.message.plus(" Ошибка обновления")
                                )
                            )
                        } else iActionCard.onError(
                            Exception(
                                " Ошибка обновления"
                            )
                        )
                    }

                } else {
                    try {
                        db.wordItemDao().insert(convertWordItemToRoomModel(wordItem))
                        iActionCard.onComplete()
                    } catch (e: Exception) {

                        if (e.message != null) {
                            iActionCard.onError(
                                Exception(
                                    e.message.plus(" Ошибка вставки")
                                )
                            )
                        } else iActionCard.onError(
                            Exception(
                                " Ошибка вставки"
                            )
                        )
                    }

                }

            }).start()

        }


        fun asynkGetWords(db: WordItemDatabase, iActionCard: IActionCard) {
            Thread(Runnable {

                Thread.currentThread().name = "Database Thread"

                try {
                    val items = db.wordItemDao().getAll()
                    iActionCard.onComplete(null, items as ArrayList<WordItemRoomModel>)
                } catch (e: Exception) {
                    if (e.message != null) {
                        iActionCard.onError(
                            Exception(e.message)
                        )
                    } else iActionCard.onError(
                        Exception("Ошибка получения слов")
                    )
                }


            }).start()
        }

        fun asynkDeleteWordItem(
            db: WordItemDatabase,
            wordItem: WordItem,
            iActionCard: IActionCard
        ) {

            Thread(Runnable {

                try {
                    db.wordItemDao().delete(
                        convertWordItemToRoomModel(
                            wordItem
                        )
                    )
                    iActionCard.onComplete()
                } catch (e: Exception) {
                    if (e.message != null) {
                        iActionCard.onError(
                            Exception(e.message)
                        )
                    } else iActionCard.onError(
                        Exception("Ошибка удаления слова")
                    )
                }


            }).start()


        }

    }

}