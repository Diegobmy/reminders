package com.ragabuza.personalreminder.util

import android.view.View
import android.view.ViewTreeObserver


/**
 * Created by diego.moyses on 2/21/2018.
 */


fun <T> View.waitForUpdate(r: () -> T) {
    this.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            this@waitForUpdate.viewTreeObserver.removeGlobalOnLayoutListener(this)
            r.invoke()
        }
    })
}