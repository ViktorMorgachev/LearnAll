package com.sedi.viktor.learnAll.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.custom.CustomColorPickerItem
import com.sedi.viktor.learnAll.ui.edit_word.listeners.ChangeColorListener
import kotlinx.android.synthetic.main.color_picker_dialog.view.*


class DialogColorChooser :
    AlertDialog.Builder, DialogInterface {

    private var changeColorListener: ChangeColorListener
    private var colors: List<Int>

    constructor(
        context: Context?,
        changeColorListener: ChangeColorListener,
        colors: List<Int>
    ) : super(context) {

        this.colors = colors
        this.changeColorListener = changeColorListener

        setView(getView(context, colors))

    }

    private lateinit var clickItemCallback: onClickItemCallback

    private fun getView(context: Context?, colors: List<Int>): View {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.color_picker_dialog, null)


        initListeners()

        var customColorPickerItem: CustomColorPickerItem

        val parser = view.resources.getXml(R.xml.item_selected_color)
        try {
            parser.next()
            parser.nextTag()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val attr = Xml.asAttributeSet(parser)
        val count = attr.attributeCount


        if (count < 0) return view


        for (i in 0 until colors.size) {

            customColorPickerItem = CustomColorPickerItem(context, attr)
            customColorPickerItem.setBackgroundIconColor(colors[i])

            // Вешаем слушателей
            customColorPickerItem.onClickListener(clickItemCallback, i)
            view.parent_root.addView(customColorPickerItem)
        }

        view.parent_root.invalidate()

        return view

    }

    private fun initListeners() {
        clickItemCallback = object : onClickItemCallback {
            override fun onClicked(visibility: Int, colorPosition: Int) {
                if (visibility == View.VISIBLE)
                    changeColorListener.onColorChanged(colors[colorPosition])
            }

        }
    }

    private fun clickedItem(i: Int) {

    }


    override fun dismiss() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    interface onClickItemCallback {
        fun onClicked(visibility: Int, colorPosition: Int)
    }

}