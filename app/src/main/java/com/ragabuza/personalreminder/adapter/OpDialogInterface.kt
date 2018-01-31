package com.ragabuza.personalreminder.adapter

import java.util.*

/**
 * Created by diego.moyses on 1/12/2018.
 */

interface OpDialogInterface {
    fun wifiCall(text: CharSequence, tag: String?)
    fun blueCall(text: CharSequence, tag: String?)
    fun timeCall(date: Calendar, tag: String?)
    fun other(text: CharSequence, tag: String?)
    fun contactCall(text: CharSequence, tag: String?)
    fun closed(tag: String?)
}