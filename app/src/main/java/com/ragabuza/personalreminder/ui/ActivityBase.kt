package com.ragabuza.personalreminder.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.ragabuza.personalreminder.util.ReminderTranslation
import com.ragabuza.personalreminder.util.Shared

/**
 * Created by diego.moyses on 2/6/2018.
 */
@SuppressLint("Registered")
open class ActivityBase : AppCompatActivity(){
    lateinit var trans: ReminderTranslation
    lateinit var shared: Shared

    override fun onCreate(savedInstanceState: Bundle?) {
        shared = Shared(this)
        trans = ReminderTranslation(this)
        super.onCreate(savedInstanceState)
    }
}