package com.ragabuza.personalreminder.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.DialogAdapter
import com.ragabuza.personalreminder.adapter.OpDialogInterface
import kotlinx.android.synthetic.main.activity_reminder.*

/**
 * Created by diego.moyses on 1/12/2018.
 */
class NewReminder : AppCompatActivity(), OpDialogInterface {
    override fun contactCall(text: CharSequence) {
        etExtra.setText(text)
    }

    override fun other(text: CharSequence) {
        etConditionExtra.setText(text)
    }

    override fun wifiCall(text: CharSequence) {
        etCondition.setText(text)
    }

    override fun blueCall(text: CharSequence) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        val extras = intent.extras

        val condition = extras.getString("condition")
        if (condition != null) etCondition.setText(condition)

        val type = extras.getString("type")

        etCondition.hint = type

        if (type == "Simple"){
            etCondition.visibility = View.GONE
            etConditionExtra.visibility = View.GONE
        } else if (type == "Horário"){
            etConditionExtra.visibility = View.GONE
        }

        etCondition.keyListener = null
        etCondition.setOnFocusChangeListener { _, b ->
            if (b) DialogAdapter(this, "W").show()
        }
        etConditionExtra.keyListener = null
        etConditionExtra.setOnFocusChangeListener { _, b ->
            if (b) DialogAdapter(this, "OWEB").show()
        }


        ibContacts.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_CONTACTS), 0)
            } else
            DialogAdapter(this, "CON").show()
        }

        val regex = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")
        etExtra.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().matches(regex))
                    etExtra.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)
                else
                    etExtra.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

}