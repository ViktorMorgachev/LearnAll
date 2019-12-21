package com.sedi.viktor.learnAll.data.interfaces

import com.sedi.viktor.learnAll.data.models.WordItemRoomModel

interface IActionCard {
    fun onError(exception: Exception)
    /**Возвращает либо коллекцию данных, либо определённый обьект*/
    fun onComplete(
        data: WordItemRoomModel? = null,
        collectionData: ArrayList<WordItemRoomModel>? = null
    )
}