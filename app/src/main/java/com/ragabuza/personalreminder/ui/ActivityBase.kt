package com.ragabuza.personalreminder.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.KeyguardManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import com.ragabuza.personalreminder.util.ReminderTranslation
import com.ragabuza.personalreminder.util.Shared
import android.view.ViewGroup
import android.view.View.MeasureSpec
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*
import com.multidots.fingerprintauth.FingerPrintAuthCallback
import com.multidots.fingerprintauth.FingerPrintAuthHelper
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.util.NotificationHelper


/**
 * Created by diego.moyses on 2/6/2018.
 */
@SuppressLint("Registered")
open class ActivityBase : AppCompatActivity() {
    lateinit var trans: ReminderTranslation
    lateinit var shared: Shared

    override fun onCreate(savedInstanceState: Bundle?) {
        shared = Shared(this)
        trans = ReminderTranslation(this)
        applyTheme()
        super.onCreate(savedInstanceState)
    }

    open fun applyTheme() {
        theme.applyStyle(shared.getTheme().theme, true)
    }

    fun ListView.readjustListView() {
        val listAdapter = this.adapter ?: return

        val desiredWidth = MeasureSpec.makeMeasureSpec(this.width, MeasureSpec.UNSPECIFIED)
        var totalHeight = 0
        var view: View? = null
        for (i in 0 until listAdapter.count) {
            view = listAdapter.getView(i, view, this)
            if (i == 0)
                view!!.layoutParams = ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

            view!!.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
            totalHeight += view.measuredHeight
        }
        val params = this.layoutParams
        params.height = totalHeight + this.dividerHeight * (listAdapter.count - 1)
        this.layoutParams = params
    }

    fun requestPassword() {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.password_dialog, null)
        val cancel = view.findViewById<TextView>(R.id.btPasswordCancel)
        val passwordField = view.findViewById<EditText>(R.id.etPasswordEnter)
        val fingerprintArea = view.findViewById<LinearLayout>(R.id.llFingerprint)
        val enterPin = view.findViewById<Button>(R.id.btEnterPIN)
        val fingerprintImage = view.findViewById<ImageView>(R.id.ivFingerPrint)

        val instructions = view.findViewById<TextView>(R.id.tvEnterPasswordInstructions)

        cancel.setOnClickListener {
            requestPasswordCallback(false)
            dialog.dismiss()
        }

        passwordField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (passwordField.text.length >= 4) {
                    if (shared.passwordIsCorrect(passwordField.text.toString())) {
                        requestPasswordCallback(true)
                        dialog.dismiss()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        dialog.setContentView(view)

        val mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, object : FingerPrintAuthCallback {
            override fun onNoFingerPrintHardwareFound() {}
            override fun onNoFingerPrintRegistered() {}
            override fun onBelowMarshmallow() {}

            override fun onAuthFailed(errorCode: Int, errorMessage: String?) {
                val animWrong = AnimationUtils.loadAnimation(applicationContext,
                        R.anim.wrong)
                fingerprintImage.startAnimation(animWrong)
            }

            override fun onAuthSuccess(cryptoObject: FingerprintManager.CryptoObject?) {
                requestPasswordCallback(true)
                dialog.dismiss()
            }

        })

        dialog.setOnDismissListener {
            mFingerPrintAuthHelper.stopAuth()
        }

        enterPin.setOnClickListener {
            mFingerPrintAuthHelper.stopAuth()
            passwordField.visibility = View.VISIBLE
            fingerprintArea.visibility = View.GONE
            instructions.text = getString(R.string.instructions_PIN)
        }


        if (shared.hasFingerprint()) {
            mFingerPrintAuthHelper.startAuth()
            passwordField.visibility = View.GONE
            fingerprintArea.visibility = View.VISIBLE
            instructions.text = getString(R.string.instructions_fingerprint)
        } else {
            passwordField.visibility = View.VISIBLE
            fingerprintArea.visibility = View.GONE
            instructions.text = getString(R.string.instructions_PIN)
        }

        dialog.show()
    }

    open fun requestPasswordCallback(success: Boolean){

    }

    override fun onPause() {
        super.onPause()
        if (shared.isFirstTime() && s){
            shared.setFirstTime(false)
            NotificationHelper(this).showNotificationRaw(0, "ola", "teste")
        }
    }

}