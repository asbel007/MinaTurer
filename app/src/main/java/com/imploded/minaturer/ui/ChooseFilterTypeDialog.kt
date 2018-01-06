package com.imploded.minaturer.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.imploded.minaturer.R

interface OnDialogInteraction {
    fun onPositiveClick(selectedIndex: Int)
    fun onNegativeClick()
}

class ChooseFilterTypeDialog : DialogFragment() {

    private var selectedIndex = FilterByLine
    private lateinit var dialogInteraction: OnDialogInteraction

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = AlertDialog.Builder(activity)

        dialog.setTitle(getString(R.string.select_filtering))
        dialog.setPositiveButton(getString(R.string.ok), { sender, _ ->
            dialogInteraction.onPositiveClick(selectedIndex)
            sender.dismiss()
        })
        dialog.setNegativeButton(getString(R.string.cancel), { sender, _ ->
            dialogInteraction.onNegativeClick()
            sender.cancel()
        })

        val list = listOf(getString(R.string.line), getString(R.string.line_direction))
        val position = selectedIndex

        val cs = list.toTypedArray<CharSequence>()
        dialog.setSingleChoiceItems(cs, position, { _, which ->
            selectedIndex = which
        })

        return dialog.create()
    }

    companion object {
        val FilterByLine = 0
        val FilterByLineAndDirection = 1
    }
}