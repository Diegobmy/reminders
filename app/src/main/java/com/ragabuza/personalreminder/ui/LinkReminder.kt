package com.ragabuza.personalreminder.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ragabuza.personalreminder.R
import kotlinx.android.synthetic.main.activity_link.*
import android.content.DialogInterface
import android.os.Build
import android.support.v7.app.AlertDialog
import android.view.Window
import android.view.WindowManager


/**
 * Created by diego.moyses on 1/2/2018.
 */
class LinkReminder : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link)


        val urlString = "${intent.getStringExtra(Intent.EXTRA_SUBJECT)} \n\n ${intent.getStringExtra(Intent.EXTRA_TEXT)}"
        tvLink.text = urlString

    }

    override fun onBackPressed() {
        finish()
    }

}