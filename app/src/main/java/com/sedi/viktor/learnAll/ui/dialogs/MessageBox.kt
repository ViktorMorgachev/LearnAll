package com.sedi.viktor.learnAll.ui.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog

class MessageBox {

    companion object {
        fun show(context: Context, title: String, text: String) {

            val dialod = AlertDialog.Builder(context).apply {
                setTitle(title)
                setMessage(text)
                setPositiveButton(
                    context.resources.getString(android.R.string.ok),
                    null
                )
            }

        }
    }


}