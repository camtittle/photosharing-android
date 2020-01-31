package com.camtittle.photosharing.ui

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

object KeyboardUtils {

    fun hide(activity: Activity?) {
        activity?.also {
            (it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).also { imm ->
                imm.hideSoftInputFromWindow(
                    it.currentFocus?.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }

}