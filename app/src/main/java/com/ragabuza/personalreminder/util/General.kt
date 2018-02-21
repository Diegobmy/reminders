package com.ragabuza.personalreminder.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.Window
import android.view.Window.ID_ANDROID_CONTENT
import android.util.DisplayMetrics




/**
 * Created by diego.moyses on 2/21/2018.
 */
class General {
    companion object {
        fun statusBarHeight(context: Activity): Int {
            val dm = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(dm)
            return dm.heightPixels - context.window.decorView?.findViewById<View>(android.R.id.content)?.measuredHeight!!
        }
    }
}