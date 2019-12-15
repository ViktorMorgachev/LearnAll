package com.sedi.viktor.learnAll.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.extensions.gone
import com.sedi.viktor.learnAll.extensions.invisible
import com.sedi.viktor.learnAll.extensions.visible
import kotlinx.android.synthetic.main.custom_toolbar.view.*

class CustomToolbar(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_toolbar, this, true)
    }

    fun setTitle(text: String) {
        tv_tittle.text = text
    }

    fun hideBackButton() = iv_back.gone()

    fun showBackButton() = iv_back.invisible()

    fun onBackClick(action: (() -> Unit) = {}) {
        iv_back.visible()
        iv_back.setOnClickListener { action() }
    }

    fun onActionClick(action: (() -> Unit) = {}) {
        iv_menu_options.visible()
        iv_menu_options.setOnClickListener { action() }
    }
}