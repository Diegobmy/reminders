package com.ragabuza.personalreminder.ui

import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.DrawerLayout
import android.text.*
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.android.gms.location.places.ui.PlacePicker
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.*
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Favorite
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.receivers.LocationReceiver
import com.ragabuza.personalreminder.util.Constants
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.REMINDER
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_CONDITION
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_REMINDER
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_TYPE
import kotlinx.android.synthetic.main.action_item_filter.*
import kotlinx.android.synthetic.main.activity_reminder_list.*
import kotlinx.android.synthetic.main.drawer_header.*
import java.util.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.ragabuza.personalreminder.util.waitForUpdate


class ReminderList : ActivityBase(), OpDialogInterface, ReminderAdapter.ReminderClickCallback, FavoriteDialogAdapter.FavoriteSelectCallback {
    override fun favoriteCall(favorite: Favorite) {
        editIntent.putExtra(FIELD_CONDITION, favorite.condition)
        editIntent.putExtra(FIELD_TYPE, favorite.type)
        startActivity(editIntent)
    }

    override fun getType(): Boolean {
        return seeOld
    }

    override fun requestRefresh() {
        refreshList()
    }

    override fun finishedLoading() {}

    private var killIt = false

    override fun view(reminder: Reminder) {
        val inte = Intent(this, ReminderViewer::class.java)
        inte.putExtra(REMINDER, reminder)
        killIt = true
        startActivity(inte)
    }

    val PLACE_PICKER_REQUEST = 1
    var seeOld = false

    override fun delete(reminder: Reminder) {
        val dao = ReminderDAO(this)
        dao.del(reminder)
        dao.close()
        shared.setLastDeleted(reminder)
        fabLastDeleted.visibility = View.VISIBLE
        refreshList()
    }

    override fun closed(tag: String?) {
        llClipOptions.visibility = View.GONE
        editIntent.removeExtra(FIELD_REMINDER)
    }

    override fun edit(reminder: Reminder) {
        val inte = Intent(this, NewReminder::class.java)
        inte.putExtra(REMINDER, reminder)
        startActivity(inte)
    }

    override fun timeCall(date: Calendar, tag: String?) {
        editIntent.putExtra(FIELD_CONDITION, date.time.toString())
        editIntent.putExtra(FIELD_TYPE, Reminder.TIME)
        startActivity(editIntent)
    }

    override fun contactCall(text: CharSequence, tag: String?) {}

    override fun other(text: CharSequence, tag: String?) {}

    override fun wifiCall(text: CharSequence, tag: String?) {
        editIntent.putExtra(FIELD_CONDITION, text)
        editIntent.putExtra(FIELD_TYPE, Reminder.WIFI)
        startActivity(editIntent)
    }

    override fun blueCall(text: CharSequence, tag: String?) {
        editIntent.putExtra(FIELD_CONDITION, text)
        editIntent.putExtra(FIELD_TYPE, Reminder.BLUETOOTH)
        startActivity(editIntent)
    }

    private fun locationCall(location: String) {
        editIntent.putExtra(FIELD_CONDITION, location)
        editIntent.putExtra(FIELD_TYPE, Reminder.LOCATION)
        startActivity(editIntent)
    }

    private fun simpleCall() {
        editIntent.putExtra(FIELD_CONDITION, "")
        editIntent.putExtra(FIELD_TYPE, Reminder.SIMPLE)
        startActivity(editIntent)
    }

    private var adapter = ReminderAdapter(this, mutableListOf())
    private lateinit var clip: CharSequence
    var selectedFolder = ""
    private var folderAdapter: FolderSpinnerAdapter? = null

    var myMenu: Menu? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)



        menu?.findItem(R.id.folder)?.isVisible = shared.getFolders().isNotEmpty()

        if (shared.isFirstTime()) {
            myMenu = menu
            menu?.findItem(R.id.folder)?.isVisible = true
        }

        val spinnerItem = menu?.findItem(R.id.folder)
        val spinner = spinnerItem?.actionView as Spinner
        spinner.background = null
        spinner.setPopupBackgroundDrawable(null)
        spinner.setPadding(0, 0, 0, 0)


        val folderList = mutableListOf<String>("Todos", "Sem pasta")
        folderList.addAll(shared.getFolders())
        folderAdapter = FolderSpinnerAdapter(this, R.layout.spinner_folder_item, folderList, spinner, object : FolderSpinnerAdapter.FolderSpinnerCallback {
            override fun onClick(folder: String) {
                when (folder) {
                    "Todos" -> {
                        supportActionBar!!.title = if (seeOld) getString(R.string.oldReminders) else getString(R.string.remindersActivityTitle)
                        adapter.doFilter(folder = "*")
                        selectedFolder = "*"
                    }
                    "Sem pasta" -> {
                        supportActionBar!!.title = "Sem pasta"
                        adapter.doFilter(folder = "")
                        selectedFolder = ""
                    }
                    else -> {
                        supportActionBar!!.title = folder
                        adapter.doFilter(folder = folder)
                        selectedFolder = folder
                    }
                }
            }
        })

        spinner.adapter = folderAdapter

        val menuItem = menu.findItem(R.id.filter)
        val actionView = MenuItemCompat.getActionView(menuItem)
        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }

        fillInfo()

        return true
    }

    fun fillInfo() {
        val dao = ReminderDAO(this)
        tvNumberOfRemindersNew?.text = "${dao.countNew()} ${getString(R.string.number_reminders_new)}."
        tvNumberOfRemindersOld?.text = "${dao.countOld()} ${getString(R.string.number_reminders_old)}."
        dao.close()
    }

    private lateinit var reminders: List<Reminder>
    private lateinit var editIntent: Intent

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            adapter.closeAllItems()
            drawer_layout.openDrawer(GravityCompat.START)
            return true
        }
        if (item?.itemId == R.id.folder) {
            adapter.closeAllItems()
            return true
        }
        if (item?.itemId == R.id.filter) {
            drawer_layout.closeDrawer(GravityCompat.START)
            btClipboard.visibility = View.GONE
            llClipOptions.visibility = View.GONE
            if (llFilters.visibility == View.VISIBLE) {
                fabMenu.close(true)
                ivFilterIconOn.visibility = View.GONE
                ivFilterIconOff.visibility = View.VISIBLE
                llFilters.visibility = View.GONE
                if ((adapter.hasFilters() || !etFilterString.text.isNullOrBlank()) && tvFilterActive != null) {
                    tvFilterActive.visibility = View.VISIBLE
                }
            } else {
                fabMenu.close(true)
                ivFilterIconOff.visibility = View.GONE
                ivFilterIconOn.visibility = View.VISIBLE
                llFilters.visibility = View.VISIBLE
                if (tvFilterActive != null) {
                    tvFilterActive.visibility = View.GONE
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_list)


        fabMenu.setClosedOnTouchOutside(true)

        setupDrawer()

        editIntent = Intent(this, NewReminder::class.java)

        supportActionBar!!.title = getString(R.string.remindersActivityTitle)


        lvRemind.emptyView = tvEmpty


        if (shared.isFirstTime())
            startPresentation()

        fabMenu.setOnClickListener { adapter.closeAllItems() }
        lvRemind.setOnItemClickListener { _, _, _, _ ->
            adapter.closeAllItems()
        }

        fabBluetooth.setOnClickListener {
            editIntent.removeExtra(FIELD_REMINDER)
            DialogAdapter(this, this, DialogAdapter.BLUETOOTH).show()
        }
        fabWifi.setOnClickListener {
            editIntent.removeExtra(FIELD_REMINDER)
            DialogAdapter(this, this, DialogAdapter.WIFI).show()
        }
        fabTime.setOnClickListener {
            editIntent.removeExtra(FIELD_REMINDER)
            DialogAdapter(this, this, DialogAdapter.TIME).show()
        }
        fabLocation.setOnClickListener {
            editIntent.removeExtra(FIELD_REMINDER)
            val builder = PlacePicker.IntentBuilder()
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
        }

        fabSimple.setOnClickListener {
            editIntent.removeExtra(FIELD_REMINDER)
            simpleCall()
        }
        fabFav.setOnClickListener {
            editIntent.removeExtra(FIELD_REMINDER)
            FavoriteDialogAdapter(this, shared.getFavorites(), this).show()
        }


        btClipboard.setOnClickListener {
            btClipboard.visibility = View.GONE
            llClipOptions.visibility = View.VISIBLE
        }
        ibClipBluetooth.setOnClickListener {
            editIntent.putExtra(FIELD_REMINDER, clip.toString())
            DialogAdapter(this, this, DialogAdapter.BLUETOOTH).show()
        }
        ibClipWifi.setOnClickListener {
            editIntent.putExtra(FIELD_REMINDER, clip.toString())
            DialogAdapter(this, this, DialogAdapter.WIFI).show()
        }
        ibClipTime.setOnClickListener {
            editIntent.putExtra(FIELD_REMINDER, clip.toString())
            DialogAdapter(this, this, DialogAdapter.TIME).show()
        }
        ibClipLocation.setOnClickListener {
            editIntent.putExtra(FIELD_REMINDER, clip.toString())
            val builder = PlacePicker.IntentBuilder()
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
        }
        ibClipSimple.setOnClickListener {
            editIntent.putExtra(FIELD_REMINDER, clip.toString())
            simpleCall()
        }
        ibClipFav.setOnClickListener {
            editIntent.putExtra(FIELD_REMINDER, clip.toString())
            FavoriteDialogAdapter(this, shared.getFavorites(), this).show()
        }

        fabLastDeleted.setOnClickListener {
            val dao = ReminderDAO(this)
            fabLastDeleted.visibility = View.GONE
            dao.add(shared.getLastDeleted())
            dao.close()
            refreshList()
        }

        setupFilter()

        val handler = Handler()
        val timedTask = object : Runnable {

            override fun run() {
                adapter.notifyDataSetChanged()
                handler.postDelayed(this, 1000 * 60)
            }
        }
        handler.post(timedTask)
    }

    private fun preparePresentation2() {

        val tutorialReminder = Reminder(0L, "", true, "Seu primeiro lembrete!", Reminder.WIFI, Reminder.IS, "Rede de Exemplo 1", "https://www.exemplo.com.br", "")

        val tutorialReminders = mutableListOf(
                tutorialReminder
        )
        val tutorialAdapter = ReminderAdapter(this, tutorialReminders)
        lvRemind.adapter = tutorialAdapter

        lvRemind.viewTreeObserver?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (lvRemind.getChildAt(0) != null) {
                    lvRemind.getChildAt(0).viewTreeObserver.removeGlobalOnLayoutListener(this)
                    startPresentation2(tutorialReminder)
                }
            }
        })
    }

    fun startPresentation3() {

        navigation_view.menu.findItem(R.id.RLock).isVisible = true
        (lvRemind.adapter as ReminderAdapter).closeAllItems()

        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val ParentWidth = displayMetrics.widthPixels

        val rect = Rect()
        myMenu?.findItem(R.id.filter)?.actionView?.getGlobalVisibleRect(rect)
        val homePosition = listOf(0, ParentWidth - rect.right + 8, rect.top, ParentWidth - rect.left + 8, rect.bottom)

        val info13 = InformationAdapter(this, "Meus parabéns você concluiu o tutorial do aplicativo e está pronto para usá-lo!")
                .setDismissListener {
                    shared.setFirstTime(false)
                }
        val info12 = InformationAdapter(this, "No menu de configurações também encontramos um atalho para revizitar este tutorial!")
                .setNext(info13)
                .setfocusView(drawer_layout)
        val info11 = InformationAdapter(this, "Configurações\nPermite gerenciar as configurações do aplicativo, algumas configurações que você vai encontrar são:\nFavoritos\nAparencia do App\nConfigurações de lembretes privados e senha")
                .setNext(info12)
                .setfocusView(drawer_layout)
        val info10 = InformationAdapter(this, "Pastas\nPermite gerenciar suas pastas.")
                .setNext(info11)
                .setfocusView(drawer_layout)
        val info9 = InformationAdapter(this, "Lembretes Privados\nPermite visualizar seus lembretes privados.")
                .setNext(info10)
                .setfocusView(drawer_layout)
        val info8 = InformationAdapter(this, "Lembretes Antigos\nPermite visualizar seus lembretes que foram marcados como feitos.")
                .setNext(info9)
                .setfocusView(drawer_layout)
        val info7 = InformationAdapter(this, "Lembretes Novos\nPermite visualizar seus lembretes que não foram marcados como feitos.")
                .setNext(info8)
                .setfocusView(drawer_layout)
        val info6 = InformationAdapter(this, "Neste menu temos as seguintes opções...")
                .setfocusView(drawer_layout)
                .setNext(info7)
        val info5 = InformationAdapter(this, "Também exite um menu lateral que permite a navegação para diversas partes do app, clique aqui para abri-lo.")
                .setNext(info6)
                .setCoordinates(homePosition)
                .setRequireMark()
                .setDismissListener {
                    drawer_layout.openDrawer(GravityCompat.START)
                }
        val info4 = InformationAdapter(this, "Neste menu de filtro você poderá selecionar lembretes com base no seu tipo, ou procurar por uma palavra chave!")
                .setNext(info5)
                .setDismissListener {
                    llFilters.visibility = View.GONE
                }
                .setSkip(InformationAdapter.RIGHT,InformationAdapter.TOP)
                .setSkipListener {
                    skipTutorial()
                }
        val info3 = InformationAdapter(this, "Filtro\nClicando neste icone irá abrir abrir um menu de filtro, tente você mesmo!")
                .setRequireMark()
                .setfocusView(myMenu?.findItem(R.id.filter)?.actionView)
                .setDismissListener {
                    llFilters.visibility = View.VISIBLE
                    llFilters.waitForUpdate {
                        info4.setfocusView(llFilters).show()
                    }
                }
        val info2 = InformationAdapter(this, "Pastas\nClicando neste icone você poderá abrir uma pasta de lembretes, assim poderá ver apenas o que é importante no momento!")
                .setNext(info3)
                .setfocusView(myMenu?.findItem(R.id.folder)?.actionView)
                .setSkip(InformationAdapter.LEFT,InformationAdapter.TOP)
                .expandView(40)
        val info1 = InformationAdapter(this, "Outra opção importante é a capacidade de achar os lembretes certos na hora certa!\nPara isso temos duas opções...")
                .setNext(info2)
                .setSkip(InformationAdapter.RIGHT,InformationAdapter.TOP)
                .setSkipListener {
                    skipTutorial()
                }

        info1.show()

    }

    fun startPresentation2(tutorialReminder: Reminder) {
        val info9 = InformationAdapter(this, "Ao clicar em visualizar será apresentado uma nova janela para se verificar detalhes sobre o lembrete, assim como opções relacionadas a ele.\nClique em visualizar para verificar essas opções.")
                .setfocusView(lvRemind.getChildAt(0).findViewById(R.id.rlView))
                .setRequireMark()
                .setDismissListener {
                    val tutorialIntent = Intent(this, ReminderViewer::class.java)
                    tutorialIntent.putExtra(REMINDER, tutorialReminder)
                    startActivityForResult(tutorialIntent, 666)
                }
        val info8 = InformationAdapter(this, "Visualizar")
                .setfocusView(lvRemind.getChildAt(0).findViewById(R.id.rlView))
                .setNext(info9)
        val info7 = InformationAdapter(this, "Editar")
                .setfocusView(lvRemind.getChildAt(0).findViewById(R.id.rlEdit))
                .setNext(info8)
        val info6 = InformationAdapter(this, "Deletar")
                .setfocusView(lvRemind.getChildAt(0).findViewById(R.id.rlDelete))
                .setNext(info7)
        val info5 = InformationAdapter(this, "No menu lateral temos as seguintes opções...")
                .setfocusView(lvRemind.getChildAt(0).findViewById(R.id.bottom_wrapper))
                .setTextPosition(InformationAdapter.CENTER, InformationAdapter.AFTER)
                .setNext(info6)
        val info4 = InformationAdapter(this, "Clicando no lembrete são reveladas mais opções.")
                .setRequireMark()
                .setfocusView(lvRemind.getChildAt(0).findViewById(R.id.llUpper))
                .setDismissListener {
                    (lvRemind.adapter as ReminderAdapter).openItem(0)
                    Handler().postDelayed({ info5.show() }, 100)
                }
        val info3 = InformationAdapter(this, "Usando este botão podemos definir o lembrete como terminado.")
                .setfocusView(lvRemind.getChildAt(0).findViewById(R.id.ivCheckbox))
                .setNext(info4)
        val info2 = InformationAdapter(this, "Nesta tela podemos verificar as informações básicas dele.")
                .setfocusView(lvRemind.getChildAt(0).findViewById(R.id.slReminders))
                .setNext(info3)
        val info1 = InformationAdapter(this, "Parabens você criou seu primeiro lembrete!")
                .setTextPosition(InformationAdapter.CENTER, InformationAdapter.AFTER)
                .setfocusView(lvRemind.getChildAt(0).findViewById(R.id.slReminders))
                .setNext(info2)

        info1.show()
    }


    private fun startPresentation() {

        lvRemind.adapter = ReminderAdapter(this, mutableListOf())

        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val Parentheight = displayMetrics.heightPixels
        val ParentWidth = displayMetrics.widthPixels

        val fabIconCoordinates = listOf(InformationAdapter.STARTFROMBOT, 34, 38, 226, 230)

        val tutorialDialog = DialogAdapter(this, this, DialogAdapter.TUTORIAL)

        val info13 = InformationAdapter(this, "Ao clicar na rede você será levado a uma nova tela para configurar o resto do seu lembrete.")
                .setTextPosition(InformationAdapter.CENTER, InformationAdapter.CENTER)
        val info12 = InformationAdapter(this, "Escolha esta rede de exemplo para prosseguir com o tutorial.")
                .setNext(info13)
                .setRequireMark()
                .setTextPosition(InformationAdapter.CENTER, InformationAdapter.BEFORE)
                .setSkipListener {
                    skipTutorial(tutorialDialog)
                }
        val info11 = InformationAdapter(this, "Ao selecionar o lembrete de WiFi, uma janela sera mostrada para se escolher a Rede desejada, outros tipos de lembrete iram mostrar diferentes janelas para selecionar sua condição.")
                .setTextPosition(InformationAdapter.CENTER, InformationAdapter.BEFORE)
                .setSkipListener {
                    skipTutorial(tutorialDialog)
                }
        val info10 = InformationAdapter(this, "Vamos começar aprendendo a criar um lembrete de Wifi, clique no icone para continuar.")
                .setfocusView(fabWifi)
                .setRequireMark()
                .setSkipListener {
                    skipTutorial(tutorialDialog)
                }
        val info9 = InformationAdapter(this, "Favoritos\nCria lembretes com condições pré definidas de Bluetooth, WiFi, Localização ou Tempo.")
                .setfocusView(fabFav)
                .setNext(info10)
        val info8 = InformationAdapter(this, "Simples\nNão gera notificações.")
                .setfocusView(fabSimple)
                .setNext(info9)
        val info7 = InformationAdapter(this, "Tempo\nNotifica em um horário pré definido.")
                .setfocusView(fabTime)
                .setNext(info8)
        val info6 = InformationAdapter(this, "Localização\nNotifica quando seu celular estiver em uma localização escolhida.")
                .setfocusView(fabLocation)
                .setNext(info7)
        val info5 = InformationAdapter(this, "WiFi\nNotifica quando uma rede WiFi escolhida estiver visível para o seu ceular.")
                .setfocusView(fabWifi)
                .setNext(info6)
        val info4 = InformationAdapter(this, "Bluetooth\nNotifica quando um dispositivo bluetooth escolhido for pareado com seu ceular.")
                .setfocusView(fabBluetooth)
                .setNext(info5)
        val info3 = InformationAdapter(this, "Este menu irá apresentar diversas opções de lembretes, por exemplo...")
                .setNext(info4)
                .setTextPosition(InformationAdapter.CENTER, InformationAdapter.BEFORE)
        val info2 = InformationAdapter(this, "Para criar um novo lembrete clique no icone abaixo.")
                .setCoordinates(fabIconCoordinates)
                .setNext(info3)
                .setRequireMark()
        val info1 = InformationAdapter(this, "Bem vindo ao NOMEDOAPP, com NOMEDOAPP você poderá criar lembretes para as mais diversas situações!\n\n Este tutorial irá lhe ensinar os básicos do aplicativo, caso não deseje ver o tutorial clique no alto da tela para pular o mesmo, clique em qualquer outro lugar para continuar.")
                .setNext(info2)
                .setSkip(InformationAdapter.RIGHT, InformationAdapter.TOP)

        info2.setDismissListener {
            fabMenu.open(true)
            val rect = Rect()
            fabBluetooth.getGlobalVisibleRect(rect)
            info3.setCoordinates(listOf(InformationAdapter.STARTFROMTOP, ParentWidth - 226, rect.top, ParentWidth - 34, Parentheight - 38))
        }

        info13.setDismissListener {
            tutorialDialog.mainDialog?.dismiss()
            fabMenu.close(true)
            val tutorialIntent = Intent(this, NewReminder::class.java)
            tutorialIntent.putExtra(FIELD_CONDITION, "Rede de Exemplo 1")
            tutorialIntent.putExtra(FIELD_TYPE, Reminder.WIFI)
            startActivityForResult(tutorialIntent, 777)
        }

        info10.setDismissListener {
            tutorialDialog.show()
            val view = tutorialDialog.mainView

            view?.waitForUpdate {
                val rect = Rect()
                val locations = IntArray(2)
                view.getLocationOnScreen(locations)
                val x = locations[0]
                val y = locations[1]
                view.getGlobalVisibleRect(rect)
                info11.setCoordinates(listOf(InformationAdapter.STARTFROMTOP, x, y - 25, rect.width() + x, rect.height() + y - 25)).show()
            }
        }

        info1.setSkipListener {
            skipTutorial(tutorialDialog)
        }

        info11.setDismissListener {

            val view = tutorialDialog.mainView

            view?.findViewById<ListView>(R.id.lv)?.waitForUpdate {
                val rect = Rect()

                val locations = IntArray(2)
                view.findViewById<ListView>(R.id.lv).getChildAt(0).getLocationOnScreen(locations)
                val x = locations[0]
                val y = locations[1]


                view.findViewById<ListView>(R.id.lv).getChildAt(0).getGlobalVisibleRect(rect)
                info12.setCoordinates(listOf(InformationAdapter.STARTFROMTOP, x, y - 25, rect.width() + x, rect.height() + y - 25)).show()
            }
        }

        info1.show()
    }

    private fun skipTutorial(tutorialDialog: DialogAdapter? = null) {
        tutorialDialog?.mainDialog?.dismiss()
        fabMenu.close(true)
        if (shared.getFavorites().isEmpty()) fabFav.visibility = View.GONE
        InformationAdapter(this, "Você pode revisitar este tutorial quando quiser no menu de configurações.").show()
        shared.setFirstTime(false)
        refreshList()
        clipShow()
        if (!shared.hasFavorites()) fabFav.visibility = View.GONE
    }

    private var ctrlBluetooth: Boolean = false
    private var ctrlWifi: Boolean = false
    private var ctrlTime: Boolean = false
    private var ctrlLocation: Boolean = false
    private var ctrlSimple: Boolean = false

    private fun setupFilter() {

        ibFilterBluetooth.setOnClickListener {
            ctrlBluetooth = !ctrlBluetooth
            ibFilterBluetooth.setImageResource(if (ctrlBluetooth) R.drawable.ic_bluetooth_selected else R.drawable.ic_bluetooth_white)
            adapter.doFilter(adapter.bluetoothFilter, ctrlBluetooth, etFilterString.text.toString())
        }

        ibFilterWifi.setOnClickListener {
            ctrlWifi = !ctrlWifi
            ibFilterWifi.setImageResource(if (ctrlWifi) R.drawable.ic_wifi_selected else R.drawable.ic_wifi_white)
            adapter.doFilter(adapter.wifiFilter, ctrlWifi, etFilterString.text.toString())
        }

        ibFilterLocation.setOnClickListener {
            ctrlLocation = !ctrlLocation
            ibFilterLocation.setImageResource(if (ctrlLocation) R.drawable.ic_location_selected else R.drawable.ic_location_white)
            adapter.doFilter(adapter.locationFilter, ctrlLocation, etFilterString.text.toString())
        }

        ibFilterTime.setOnClickListener {
            ctrlTime = !ctrlTime
            ibFilterTime.setImageResource(if (ctrlTime) R.drawable.ic_time_selected else R.drawable.ic_time_white)
            adapter.doFilter(adapter.timeFilter, ctrlTime, etFilterString.text.toString())
        }
        ibFilterSimple.setOnClickListener {
            ctrlSimple = !ctrlSimple
            ibFilterSimple.setImageResource(if (ctrlSimple) R.drawable.ic_simple_selected else R.drawable.ic_simple_white)
            adapter.doFilter(adapter.simpleFilter, ctrlSimple, etFilterString.text.toString())
        }

        ibFilterClear.setOnClickListener {
            ctrlBluetooth = false
            ibFilterBluetooth.setImageResource(R.drawable.ic_bluetooth_white)

            ctrlWifi = false
            ibFilterWifi.setImageResource(R.drawable.ic_wifi_white)

            ctrlTime = false
            ibFilterTime.setImageResource(R.drawable.ic_time_white)

            ctrlLocation = false
            ibFilterLocation.setImageResource(R.drawable.ic_location_white)

            ctrlSimple = false
            ibFilterSimple.setImageResource(R.drawable.ic_simple_white)

            etFilterString.text.clear()

            adapter.doFilter(clearAll = true)
        }

        etFilterString.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                adapter.doFilter(str = p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    override fun onPause() {
        hideUndo()
        super.onPause()
    }

    private fun setupDrawer() {
        this.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)



        drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
                fillInfo()
            }

            override fun onDrawerOpened(drawerView: View?) {
                fabMenu.close(true)
            }

            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerClosed(drawerView: View?) {}
        })

        navigation_view.menu.findItem(R.id.RLock).isVisible = shared.hasPassword()

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.RNew -> {
                    setViewNew()
                }
                R.id.ROld -> {
                    setViewOld()
                }
                R.id.RLock -> {
                    requestPassword()
                }
                R.id.RFolder -> {
                    val f = Intent(this, Folders::class.java)
                    startActivity(f)
                }
                R.id.config -> {
                    val i = Intent(this, SettingsActivity::class.java)
                    startActivity(i)
                }
            }
            menuItem.isChecked = true
            drawer_layout.closeDrawers()
            true
        }
    }

    private var vision: Boolean? = null

    private fun setViewOld() {
        val inAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.in_change_listview)
        val outAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.out_change_listview)
        hideUndo()
        seeOld = true
        if (!navigation_view.menu.getItem(1).isChecked)
            vision = true
        refreshList()
        btClipboard.visibility = View.GONE
        llClipOptions.visibility = View.GONE
        lvRemind.startAnimation(inAnimation)
        supportActionBar!!.title = getString(R.string.oldReminders)
        fabMenu.startAnimation(outAnimation)
        fabMenu.visibility = View.GONE
    }

    private fun setViewNew() {
        val inAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.in_change_listview)
        hideUndo()
        seeOld = false
        if (!navigation_view.menu.getItem(0).isChecked)
            vision = false
        refreshList()
        lvRemind.startAnimation(inAnimation)
        supportActionBar!!.title = getString(R.string.remindersActivityTitle)
        fabMenu.startAnimation(inAnimation)
        fabMenu.visibility = View.VISIBLE
    }

    private fun hideUndo() {
        if (fabLastDeleted.visibility == View.VISIBLE) {
            fabLastDeleted.visibility = View.GONE
            Toast.makeText(this, getString(R.string.recover_deleted), Toast.LENGTH_SHORT).show()
        }
    }

    override fun requestPasswordCallback(success: Boolean) {
        if (success) {
            val b = Intent(this, PrivateReminderList::class.java)
            startActivity(b)
            finish()
        }
    }

    fun refreshList() {
        val dao = ReminderDAO(this)
        reminders = if (!seeOld)
            dao.get()
        else
            dao.getOld()

        dao.close()
        val mutList = reminders.filter { it.folder != Constants.Other.PRIVATE_FOLDER }.toMutableList()
        mutList.sortBy { !it.done.contains("WAITING") }
        adapter = ReminderAdapter(this, mutList)
        lvRemind.adapter = adapter
    }

    override fun onBackPressed() {
        when (vision) {
            true -> {
                navigation_view.menu.getItem(0).isChecked = true
                setViewNew()
            }
            false -> {
                navigation_view.menu.getItem(1).isChecked = true
                setViewOld()
            }
            null -> super.onBackPressed()
        }
        vision = null
    }

    override fun onResume() {
        super.onResume()
        if (shared.isFirstTime()) return
        if (!shared.hasFavorites()) {
            fabFav.visibility = View.GONE
            ibClipFav.visibility = View.GONE
        }
        if (!killIt) {
            refreshList()
        }
        clipShow()
        resumeFiltersNFolders()
        killIt = false

        val intent = Intent(this, LocationReceiver::class.java)
        startService(intent)

    }

    private fun clipShow() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        llClipOptions.visibility = View.GONE

        clip = if (clipboard.primaryClip != null) clipboard.primaryClip.getItemAt(0).text else ""

        if (!clip.isEmpty() && clip.toString() != shared.getClip()) {
            shared.setClip(clip.toString())
            btClipboard.visibility = View.VISIBLE
            clip = clipboard.primaryClip.getItemAt(0).text
            val spannable = SpannableString("${getString(R.string.clipboard)}\n\"$clip\"")
            spannable.setSpan(StyleSpan(Typeface.ITALIC), getString(R.string.clipboard).length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            btClipboard.text = spannable
            btClipboard.ellipsize = TextUtils.TruncateAt.END
            btClipboard.maxLines = 3
            if (trans.extraIsLink(clip.toString()))
                btClipboard.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)
            else
                btClipboard.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            llClipOptions.visibility = View.GONE
            btClipboard.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PLACE_PICKER_REQUEST -> if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(data, this)
                locationCall("${place.latLng.latitude},${place.latLng.longitude}")
            }
            666 -> {
                startPresentation3()
            }
            777 -> {
                preparePresentation2()
            }
        }
    }


    private fun resumeFiltersNFolders() {
        if (ctrlBluetooth) adapter.doFilter(adapter.bluetoothFilter)
        if (ctrlWifi) adapter.doFilter(adapter.wifiFilter)
        if (ctrlLocation) adapter.doFilter(adapter.locationFilter)
        if (ctrlTime) adapter.doFilter(adapter.timeFilter)
        if (ctrlSimple) adapter.doFilter(adapter.simpleFilter)
        adapter.doFilter(str = etFilterString.text.toString())


        if (shared.getFolders().contains(selectedFolder))
            adapter.doFilter(folder = selectedFolder)
        else {
            supportActionBar!!.title = if (seeOld) getString(R.string.oldReminders) else getString(R.string.remindersActivityTitle)
            adapter.doFilter(folder = "*")
            selectedFolder = "*"
        }

        if (folderAdapter != null) {
            val folderList = mutableListOf<String>("Todos", "Sem pasta")
            folderList.addAll(shared.getFolders())
            folderAdapter?.options?.clear()
            folderAdapter?.options?.addAll(folderList)
        }
    }

}
