package com.ragabuza.personalreminder.adapter

import java.util.*

/**
 * Created by diego.moyses on 1/12/2018.
 */

interface OpDialogInterface {
    fun wifiCall(text: CharSequence)
    fun blueCall(text: CharSequence)
    fun timeCall(date: Calendar)
    fun other(text: CharSequence)
    fun contactCall(text: CharSequence)
    fun closed()
}