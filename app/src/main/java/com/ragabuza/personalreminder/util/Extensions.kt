package com.ragabuza.personalreminder.util

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan

/**
 * Created by diego.moyses on 2/8/2018.
 */

private fun Context.getTextIcon(string: String, int: Int): SpannableString {
    val ss = SpannableString("# $string")
    val d = resources.getDrawable(int)
    d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
    ss.setSpan(ImageSpan(d, ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return ss
}