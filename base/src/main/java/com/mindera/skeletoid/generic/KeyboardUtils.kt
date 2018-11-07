package com.mindera.skeletoid.generic

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtils {

    /**
     * Hide keyboard. This can only be called from the Activity.
     * Using for example getActivity() from a Fragment WON'T work.
     */
    @JvmStatic
    fun hideKeyboard(activity: Activity?) {
        if (activity == null) {
            throw IllegalArgumentException("Activity cannot be null")
        }

        val imm = activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Hide keyboard. This can be called from everywhere (Fragments for example)
     *
     * @param context App context
     * @param view    A view
     */
    @JvmStatic
    fun hideKeyboardFrom(context: Context?, view: View?) {
        if (context == null) {
            throw IllegalArgumentException("Context cannot be null")
        }

        if (view == null) {
            throw IllegalArgumentException("View cannot be null")
        }

        val imm = context
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
