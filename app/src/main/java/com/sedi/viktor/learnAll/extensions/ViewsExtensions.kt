package com.sedi.viktor.learnAll.extensions

import android.view.View
import android.widget.EditText

fun View.setIsVisible(visible: Boolean) {
    if (visible) visible() else gone()
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun EditText.getStringText() = text.toString()

fun EditText.cursorToEnd() {
    setSelection(getStringText().length)
}