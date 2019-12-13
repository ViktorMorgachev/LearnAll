package com.sedi.viktor.learnAll.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class WordItemRoomModel {

    @PrimaryKey(autoGenerate = true)
    public val primaryKey: Int = 0


    public var learned: Boolean = false
        set(value) {
            field = value
        }


    @ColumnInfo(name = "other_name")
    public var otherName: String = ""
        set(value) {
            field = value

        }

    @ColumnInfo(name = "native_name")
    var nativeName: String = ""
        set(value) {
            field = value

        }
    @ColumnInfo(name = "card_native_back_color")
    var cardNativeBackGround: String = ""
        set(value) {
            field = value
        }

    @ColumnInfo(name = "card_other_back_color")
    var cardOtherBackGround: String = ""
        set(value) {
            field = value
        }
    @ColumnInfo(name = "card_other_text_color")
    var cardOtheTextColor: String = ""
        set(value) {
            field = value
        }
    @ColumnInfo(name = "card_native_text_color")
    var cardNativeTextColor: String = ""
        set(value) {
            field = value
        }


}