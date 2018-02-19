package com.ragabuza.personalreminder.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.FolderAdapter
import com.ragabuza.personalreminder.util.Constants.Other.Companion.EMPTY_FOLDER
import kotlinx.android.synthetic.main.activity_folders.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import android.view.ViewGroup
import android.widget.LinearLayout


/**
 * Created by diego on 17/02/2018.
 */
class Folders : ActivityBase() {

    val folders = hashSetOf<String>()
    var editing = false
    var editingExisting = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folders)
        lvFolders.emptyView = tvEmpty
        folders.addAll(shared.getFolders())
        refreshList()
        supportActionBar?.title = getString(R.string.folders)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        llBeforeAdd.setOnClickListener {
            startEdition()
        }
    }

    private fun startEdition() {
        tilAfterAdd.visibility = View.VISIBLE
        llBeforeAdd.visibility = View.GONE
        editing = true
        etAfterAdd.requestFocus()
        val keyboard = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.showSoftInput(etAfterAdd, 0)
        etAfterAdd.setSelection(etAfterAdd.text.length)
        ivAddFolder.setOnClickListener {
            if (etAfterAdd.text.isNotEmpty() && !folders.contains(etAfterAdd.text.toString()) && etAfterAdd.text.length <= 13) {
                folders.add(etAfterAdd.text.toString())
                if (folders.size == 1) refreshList()
                tilAfterAdd.visibility = View.GONE
                llBeforeAdd.visibility = View.VISIBLE
                ivAddFolder.setOnClickListener(null)
                etAfterAdd.setText("")
                editing = false
                rezisePlus()
                etAfterAdd.error = null
            } else if (folders.contains(etAfterAdd.text.toString())) {
                etAfterAdd.error = getString(R.string.folder_already)
            } else if (etAfterAdd.text.isEmpty()) {
                etAfterAdd.error = getString(R.string.insert_folder_name)
            } else if (etAfterAdd.text.length > 13) {
                etAfterAdd.error = getString(R.string.folder_too_long)
            }
        }
    }

    fun rezisePlus(){
        if (adapter.count > 5) {
            refreshList()
            val item = adapter.getView(0, null, lvFolders)
            item.measure(0, 0)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (5.5 * item.measuredHeight).toInt())
            lvFolders.layoutParams = params
        }
    }
    fun reziseMinus(){
        refreshList()
        if (adapter.count <= 5) {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lvFolders.layoutParams = params
        }
    }

    override fun onBackPressed() {
        if (editing) {
            tilAfterAdd.visibility = View.GONE
            llBeforeAdd.visibility = View.VISIBLE
            ivAddFolder.setOnClickListener(null)
            etAfterAdd.setText("")
            etAfterAdd.error = null
            editing = false
            if (editingExisting.isNotEmpty()) {
                folders.add(editingExisting)
                editingExisting = ""
            }
        } else {
            shared.setFolders(folders)
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var adapter: FolderAdapter

    private fun refreshList() {
        adapter = FolderAdapter(this, folders, object : FolderAdapter.FolderClickListener {
            override fun delete(folder: String) {

                SweetAlertDialog(this@Folders, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.really_delete_folder, folder))
                        .setContentText(getString(R.string.cannot_be_undone))
                        .setConfirmText(getString(R.string.no_delete))
                        .setCancelText(getString(R.string.yes_delete))
                        .setCancelClickListener {
                            folders.remove(folder)
                            reziseMinus()
                            refreshList()
                            it.setTitleText(this@Folders.getString(R.string.deleted))
                                    .setContentText(this@Folders.getString(R.string.folder_deleted))
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        }.show()
            }

            override fun edit(folder: String) {
                folders.remove(folder)
                etAfterAdd.setText(folder)
                editingExisting = folder
                startEdition()
            }

        })
        lvFolders.adapter = adapter
    }
}