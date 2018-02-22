package com.ragabuza.personalreminder.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface.ITALIC
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.DialogAdapter
import com.ragabuza.personalreminder.adapter.OpDialogInterface
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Reminder
import kotlinx.android.synthetic.main.activity_reminder.*
import java.text.SimpleDateFormat
import java.util.*
import android.text.style.ImageSpan
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.google.android.gms.location.places.ui.PlacePicker
import com.ragabuza.personalreminder.util.*
import android.app.ProgressDialog
import android.widget.ArrayAdapter
import android.widget.Toast
import com.ragabuza.personalreminder.adapter.InformationAdapter
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.IS_OUT
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.KILL_IT
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.REMINDER
import com.ragabuza.personalreminder.util.Constants.Other.Companion.CONTACT_PREFIX
import com.ragabuza.personalreminder.util.Constants.Other.Companion.PRIVATE_FOLDER
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_CONDITION
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_EXTRA
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_REMINDER
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_TYPE
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.P_TABLE_NAME


/**
 * Created by diego.moyses on 1/12/2018.
 */
class NewReminder : ActivityBase(), OpDialogInterface {
    override fun finishedLoading() {
    }

    val PLACE_PICKER_REQUEST = 1

    override fun closed(tag: String?) {}

    var ID: Long = 0
    var cond: String = ""
    var contact = false

    override fun contactCall(text: CharSequence, tag: String?) {
        contact = false
        val ss = SpannableString(" $text")
        val d = resources.getDrawable(R.drawable.ic_contact)
        d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
        val span = ImageSpan(d, ImageSpan.ALIGN_BOTTOM)
        ss.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(StyleSpan(ITALIC), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(ForegroundColorSpan(resources.getColor(R.color.contactColor)), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        etExtra.setText(ss)
        etExtra.setSelection(ss.length)
        etExtra.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        contact = true
    }

    override fun other(text: CharSequence, tag: String?) {
        etConditionExtra.setText(text)
    }

    override fun timeCall(date: Calendar, tag: String?) {
        cond = date.time.toString()
        etCondition.setText(TimeString(date).getString(false))
    }

    override fun wifiCall(text: CharSequence, tag: String?) {
        cond = text.toString()
        etCondition.setText(text)
    }

    override fun blueCall(text: CharSequence, tag: String?) {
        cond = text.toString()
        etCondition.setText(text)
    }

    private var mapWidth: Int = 400
    private var mapHeight: Int = 200

    private var edition: Boolean = false

    private var fromPrivate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        if (shared.hasPassword()) llParticular.visibility = View.VISIBLE

        val hasFolders = shared.getFolders().isNotEmpty()

        val folders = mutableListOf<String>("Sem pasta")
        val spAdapter = ArrayAdapter<String>(baseContext, R.layout.simple_spinner, folders)

        if (hasFolders) {
            llFolder.visibility = View.VISIBLE
            folders.addAll(shared.getFolders())
            spFolder.adapter = spAdapter
        }


        val extras = intent.extras
        cond = extras.getString(FIELD_CONDITION, "")
        var type = extras.getString(FIELD_TYPE, "")

        if (shared.isFirstTime()) {
            llContainer.requestFocus()
            startPresentation()
        } else
            etReminder.requestFocus()

        etExtra.setText(extras.getString(FIELD_EXTRA, ""))

        if (trans.extraIsLink(extras.getString(FIELD_REMINDER, "")) && extras.getString(FIELD_EXTRA, "").isEmpty()) {
            etExtra.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)
            etExtra.setText(extras.getString(FIELD_REMINDER, ""))
        } else
            etReminder.setText(extras.getString(FIELD_REMINDER, ""))

        if (trans.extraIsLink(extras.getString(FIELD_EXTRA, "")))
            etExtra.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)

        etConditionExtra.setText(getString(R.string.when_is))

        if (extras.getBoolean(IS_OUT, false))
            etConditionExtra.setText(getString(R.string.when_isnot))

        val reminderEdited = extras.getParcelable<Reminder>(REMINDER)
        if (reminderEdited != null) {
            edition = true
            ID = reminderEdited.id
            type = reminderEdited.type
            etCondition.setText(reminderEdited.condition)
            cond = reminderEdited.condition
            etConditionExtra.setText(trans.toString(reminderEdited.rWhen))
            etReminder.setText(reminderEdited.reminder)
            etExtra.setText(reminderEdited.extra)
            when {
                trans.extraIsContact(reminderEdited.extra) -> contactCall(reminderEdited.extra, null)
                trans.extraIsLink(reminderEdited.extra) -> etExtra.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)
            }
            if (reminderEdited.folder == PRIVATE_FOLDER) {
                fromPrivate = true
                swParticular.isChecked = true
                llFolder.visibility = View.GONE
            } else {
                spFolder.setSelection(spAdapter.getPosition(reminderEdited.folder))
            }
        }

        supportActionBar?.title = trans.reminderType(type)

        if (type == Reminder.TIME) {
            val cal = Calendar.getInstance()
            val sdf = if (cond.length <= 5)
                SimpleDateFormat("HH:mm", Locale.getDefault())
            else
                SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
            cal.time = sdf.parse(cond)
            if (cond.length <= 5) {
                cal.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
                if (cal.get(Calendar.HOUR_OF_DAY) < Calendar.getInstance().get(Calendar.HOUR_OF_DAY) &&
                        cal.get(Calendar.MINUTE) < Calendar.getInstance().get(Calendar.MINUTE))
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                cond = cal.time.toString()
            }
            etCondition.setText(TimeString(cal).getString(false))
        } else if (type == Reminder.LOCATION) {

        } else
            etCondition.setText(cond)


        hintCondition.hint = when (type) {
            Reminder.WIFI -> "Rede WiFi"
            Reminder.BLUETOOTH -> "Dispositivo Bluetooth"
            Reminder.LOCATION -> "Local"
            Reminder.TIME -> "Horário"
            else -> "Condição"
        }

        when (type) {
            Reminder.SIMPLE -> {
                etCondition.visibility = View.GONE
                etConditionExtra.visibility = View.GONE
            }
            Reminder.TIME -> {
                etConditionExtra.visibility = View.GONE
            }
            Reminder.LOCATION -> {
                etCondition.visibility = View.GONE
                etConditionExtra.visibility = View.GONE
                mapView.visibility = View.VISIBLE
                getMap()
            }
        }

        if (type != Reminder.LOCATION) {
            etCondition.keyListener = null
            etCondition.setOnFocusChangeListener { _, b ->
                if (b) when (type) {
                    Reminder.WIFI -> DialogAdapter(this, this, DialogAdapter.WIFI).show()
                    Reminder.BLUETOOTH -> DialogAdapter(this, this, DialogAdapter.BLUETOOTH).show()
                    Reminder.TIME -> DialogAdapter(this, this, DialogAdapter.TIME).show()
                }
            }
            etCondition.setOnClickListener {
                when (type) {
                    Reminder.WIFI -> DialogAdapter(this, this, DialogAdapter.WIFI).show()
                    Reminder.BLUETOOTH -> DialogAdapter(this, this, DialogAdapter.BLUETOOTH).show()
                    Reminder.TIME -> DialogAdapter(this, this, DialogAdapter.TIME).show()
                }
            }
        } else {
            mapView.setOnClickListener {
                val builder = PlacePicker.IntentBuilder()
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
            }
        }
        etConditionExtra.keyListener = null
        etConditionExtra.setOnFocusChangeListener { _, b ->
            if (b) DialogAdapter(this, this, DialogAdapter.CHOICE_WEB).show()
        }
        etConditionExtra.setOnClickListener {
            DialogAdapter(this, this, DialogAdapter.CHOICE_WEB).show()
        }


        ibContacts.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 0)
            } else {
                DialogAdapter(this, this, DialogAdapter.CONTACTS).show()
            }
        }

        val killIt = extras.getBoolean(KILL_IT, false)

        btn_cancel.setOnClickListener {
            if (killIt)
                finishAndRemoveTaskCompat()
            else
                finish()
        }
        btn_save.setOnClickListener {
            if (etReminder.text.isEmpty()) {
                etReminder.error = getString(R.string.please_fill_reminder)
                return@setOnClickListener
            }
            val rWhen: String = if (type == Reminder.LOCATION) {
                trans.locationAddress(cond)
            } else
                trans.toCode(etConditionExtra.text.toString())
            val folder = when {
                !hasFolders -> ""
                spFolder.selectedItem.toString() == "Sem pasta" -> ""
                else -> spFolder.selectedItem.toString()
            }
            val reminder = Reminder(
                    ID,
                    "",
                    true,
                    etReminder.text.toString(),
                    type,
                    rWhen,
                    cond,
                    if (contact) "$CONTACT_PREFIX ${etExtra.text}" else etExtra.text.toString(),
                    if (swParticular.isChecked) PRIVATE_FOLDER else folder
            )
            val dao = ReminderDAO(this)
            if (ID == 0L)
                ID = dao.add(reminder)
            else
                dao.alt(reminder)
            dao.close()
            if (type == Reminder.TIME) setAlarm()
            if (killIt)
                finishAndRemoveTaskCompat()
            else
                finish()
        }

        swParticular.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestPassword()
            } else if (hasFolders) {
                llFolder.visibility = View.VISIBLE
            }
        }

        etExtra.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (trans.extraIsLink(p0.toString()))
                    etExtra.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)
                else
                    etExtra.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (contact) {
                    contact = false
                    if (p0 != null) {
                        if (p2 > p3)
                            etExtra.setText("")
                        else
                            etExtra.setText(p0.toString().substring(p0.length - 1 until p0.length))
                    } else
                        etExtra.setText("")
                    etExtra.setSelection(etExtra.text.length)
                    etExtra.inputType = InputType.TYPE_CLASS_TEXT
                }
            }
        })
    }

    private fun setAlarm() {
        val alarm = AlarmHelper(this)
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
        cal.time = sdf.parse(cond)
        if (edition)
            alarm.stopAlarm(ID)
        alarm.setAlarm(ID, cal.timeInMillis)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(data, this)
                locationCall("${place.latLng.latitude},${place.latLng.longitude}")
            }
        }
    }

    private fun locationCall(location: String) {
        cond = location
        getMap()
    }

    override fun requestPasswordCallback(success: Boolean) {
        if (!success)
            swParticular.isChecked = false
        else
            llFolder.visibility = View.GONE

    }

    private fun getMap() {
        val request = "https://maps.googleapis.com/maps/api/staticmap?markers=$cond&size=${mapWidth}x$mapHeight&key=${getString(R.string.google_maps_api)}"
        DownloadImage(mapView).execute(request)
    }

    override fun onPause() {
        if (fromPrivate) {
            val b = Intent(this, ReminderList::class.java)
            startActivity(b)
            finish()
        }
        super.onPause()
    }

    private fun startPresentation() {
        val info11 = InformationAdapter(this, "Caso contrário você pode salvar seu lembrete, clique em salvar para continuar.")
                .setfocusView(btn_save)
                .setRequireMark()
                .setDismissListener {
                    setResult(777)
                    finish()
                }
        val info10 = InformationAdapter(this, "Depois de terminar seu lembrete, caso não esteja satisfeito, você pode cancelar sua criação.")
                .setNext(info11)
                .setfocusView(btn_cancel)
        val info9 = InformationAdapter(this, "Aqui podemos selecionar se o lembrete fará parte de alguma pasta, pastas ajudam a separar os lembretes de diferentes assuntos e manter sua organização!")
                .setNext(info10)
                .setfocusView(llFolder)
        val info8 = InformationAdapter(this, "Aqui podemos definir o lembrete como privado, lembretes privados só podem ser visualizados caso você tenha a senha.\n\nOBS:Para ativar lembretes privados vá até as configurações.")
                .setfocusView(llParticular)
                .setNext(info9)
        val info7 = InformationAdapter(this, "Também podemos definir o lembrete como um link, para isso apenas digite o link no campo extra, definindo o extra como um link irá oferecer a opção de abrir no navegador ou compartilhar o link quando o lembrete for ativado ou visualidado.")
                .setNext(info8)
                .setfocusView(etExtra)
        val info6 = InformationAdapter(this, "Clicando neste botão podemos definir que o extra será um contato do seu celular, definindo o extra como um contato irá oferecer a opção de realizar uma ligação telefonica quando o lembrete for ativado ou visualidado.")
                .setNext(info7)
                .setfocusView(ibContacts)
                .setDismissListener {
                    etExtra.setText("https//:www.exemplo.com.br")
                    etExtra.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)
                }
        val info5 = InformationAdapter(this, "Este campo será uma informação extra ligada ao lembrete, por exemplo...")
                .setNext(info6)
                .setfocusView(etExtra)
        val info4 = InformationAdapter(this, "Este campo será o lembrete em si.")
                .setNext(info5)
                .setfocusView(etReminder)
                .setDismissListener { etReminder.setText("Seu primeiro lembrete!") }
                .setSkip(InformationAdapter.RIGHT, InformationAdapter.TOP)
                .setTextPosition(InformationAdapter.CENTER, InformationAdapter.BEFORE)
        val info3 = InformationAdapter(this, "Neste campo definimos a rede WiFi que irá ativar o lembrete.")
                .setNext(info4)
                .setfocusView(etCondition)
        val info2 = InformationAdapter(this, "Neste campo definimos se o lembrete vai ser disparado quando conectarmos ou desconectarmos da rede.")
                .setNext(info3)
                .setfocusView(etConditionExtra)
                .setSkip(InformationAdapter.LEFT, InformationAdapter.BOT)
                .setTextPosition(InformationAdapter.CENTER, InformationAdapter.AFTER)
        val info1 = InformationAdapter(this, "Esta é a tela de edição de lembrete, nela você pode configurar um lembrete novo ou editar um já existente.")
                .setNext(info2)
                .setSkip(InformationAdapter.RIGHT, InformationAdapter.TOP)
                .setSkipListener {
                    InformationAdapter(this, "Você pode revisitar este tutorial quando quiser no menu de configurações.").show()
                    shared.setFirstTime(false)
                    setResult(0)
                    finish()
                }

        llFolder.visibility = View.VISIBLE
        llParticular.visibility = View.VISIBLE

        info1.show()
    }

}