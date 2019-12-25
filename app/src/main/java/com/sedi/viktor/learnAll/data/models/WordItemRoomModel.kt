package com.sedi.viktor.learnAll.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class WordItemRoomModel : Any() {

    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0
    var learned: Boolean = false
    @ColumnInfo(name = "other_name")
    var otherName: String = ""
    @ColumnInfo(name = "native_name")
    var nativeName: String = ""
    @ColumnInfo(name = "card_native_back_color")
    var cardNativeBackGround: String = ""
    @ColumnInfo(name = "card_other_back_color")
    var cardOtherBackground: String = ""
    @ColumnInfo(name = "card_other_text_color")
    var cardOtheTextColor: String = ""
    @ColumnInfo(name = "card_native_text_color")
    var cardNativeTextColor: String = ""
    @ColumnInfo(name = "card_favourite")
    var favourite: Boolean = false


}