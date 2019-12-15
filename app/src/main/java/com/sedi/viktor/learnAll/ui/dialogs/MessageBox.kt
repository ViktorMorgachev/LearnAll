package com.sedi.viktor.learnAll.ui.dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class MessageBox {

    companion object {
        fun show(context: Activity, title: String, text: String, lifecycleOwner: LifecycleOwner) {
            val dialod = AlertDialog.Builder(context).apply {
                setTitle(title)
                setMessage(text)
                setPositiveButton(
                    context.resources.getString(android.R.string.ok),
                    null
                )
            }
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED)
                dialod.show()
        }
    }


}