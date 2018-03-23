package com.ragabuza.personalreminder.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import com.ragabuza.personalreminder.R
import android.view.View


/**
 * Created by diego.moyses on 1/2/2018.
 */
class PasswordAdapter(val context: Context, private val listener: PasswordResult) {

    interface PasswordResult {
        fun onSetPassword(password: String, fingerprint: Boolean)
        fun onCancel(ignore: Boolean)
    }


    fun show(password: String, biometric: Boolean) {

        val dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.password_configuration_dialog, null)

        val save = view.findViewById<TextView>(R.id.btPasswordSave)
        val cancel = view.findViewById<TextView>(R.id.btPasswordCancel)
        val passwordField = view.findViewById<EditText>(R.id.etPasswordSet)
        val confirmPasswordField = view.findViewById<EditText>(R.id.etPasswordConfirm)

        val biometricSw = view.findViewById<Switch>(R.id.swFingerprint)
//        val passwordField = view.findViewById<EditText>(R.id.etPasswordSet)

        biometricSw.isChecked = biometric
        passwordField.setText(password)

        passwordField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (passwordField.text.length >= 4) {
                    confirmPasswordField.visibility = View.VISIBLE
                    confirmPasswordField.setText("")
                    confirmPasswordField.error = null
                } else
                    confirmPasswordField.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        save.setOnClickListener {
            if (passwordField.text.length >= 4 && confirmPasswordField.text.toString() == passwordField.text.toString()) {
                listener.onSetPassword(passwordField.text.toString(), biometricSw.isChecked)
                dialog.dismiss()
            } else if (passwordField.text.length >= 4 && confirmPasswordField.visibility == View.GONE) {
                listener.onSetPassword(passwordField.text.toString(), biometricSw.isChecked)
                dialog.dismiss()
            } else if (confirmPasswordField.text.toString() != passwordField.text.toString()) {
                confirmPasswordField.error = context.getString(R.string.password_not_match)
                confirmPasswordField.requestFocus()
            } else {
                passwordField.error = context.getString(R.string.password_too_short)
                passwordField.requestFocus()
            }
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            listener.onCancel(passwordField.text.isNotEmpty() || password.isNotEmpty())
        }

        dialog.setContentView(view)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()


    }


}