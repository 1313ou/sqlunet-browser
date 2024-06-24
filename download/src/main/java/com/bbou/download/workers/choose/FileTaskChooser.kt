/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers.choose

import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bbou.deploy.workers.FileTasks
import com.bbou.download.choose.Chooser.toRadioGroup
import com.bbou.download.common.R

/**
 * File async task chooser
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
@Suppress("UNUSED")
object FileTaskChooser {

    /**
     * Copy data base from selected file in container
     *
     * @param activity     activity
     * @param databasePath database path
     * @param directories  directory names-values array
     */
    @SafeVarargs

    fun copyFromFile(activity: FragmentActivity, databasePath: String, directories: Pair<Array<out CharSequence>, Array<String>>) {
        // collect sources
        val input = toRadioGroup(activity, directories.first, directories.second)
        if (input == null) {
            Toast.makeText(activity, R.string.no_datapack, Toast.LENGTH_SHORT).show()
            return
        }

        // display sources
        AlertDialog.Builder(activity)
            .setTitle(R.string.action_copy_datapack_from_file)
            .setMessage(R.string.hint_copy_from_file)
            .setView(input)
            .setPositiveButton(R.string.action_ok) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                val childCount = input.childCount
                for (i in 0 until childCount) {
                    val radioButton = input.getChildAt(i) as RadioButton
                    if (radioButton.id == input.checkedRadioButtonId) {
                        val sourceFile = radioButton.tag.toString()
                        if (!activity.isFinishing && !activity.isDestroyed) {
                            FileTasks.launchCopy(activity, sourceFile, databasePath, null)
                        }
                    }
                }
            }
            .setNegativeButton(R.string.action_cancel) { _: DialogInterface?, _: Int -> }
            .show()
    }

    /**
     * Unzip data base from selected file in container
     *
     * @param activity     activity
     * @param databasePath database path
     * @param directories  directory names-values array
     */
    @SafeVarargs

    fun unzipFromArchive(activity: FragmentActivity, databasePath: String, directories: Pair<Array<out CharSequence>, Array<String>>) {
        // collect sources
        val input = toRadioGroup(activity, directories.first, directories.second)
        if (input == null) {
            Toast.makeText(activity, R.string.no_datapack, Toast.LENGTH_SHORT).show()
            return
        }

        // display sources
        AlertDialog.Builder(activity)
            .setTitle(R.string.action_unzip_datapack_from_archive)
            .setMessage(R.string.hint_unzip_from_archive)
            .setView(input)
            .setPositiveButton(R.string.action_ok) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                val childCount = input.childCount
                for (i in 0 until childCount) {
                    val radioButton = input.getChildAt(i) as RadioButton
                    if (radioButton.id == input.checkedRadioButtonId) {
                        val sourceFile = radioButton.tag.toString()
                        if (!activity.isFinishing && !activity.isDestroyed) {
                            FileTasks.launchUnzip(activity, sourceFile, databasePath, null)
                        }
                    }
                }
            }
            .setNegativeButton(R.string.action_cancel) { _: DialogInterface?, _: Int -> }
            .show()
    }

    /**
     * Unzip data base from selected file in container
     *
     * @param activity     activity
     * @param databasePath database path
     * @param directories  directory names-values array
     */
    @SafeVarargs

    fun unzipEntryFromArchive(activity: FragmentActivity, databasePath: String, directories: Pair<Array<out CharSequence>, Array<String>>) {
        // collect sources
        val archiveInput1 = toRadioGroup(activity, directories.first, directories.second)
        if (archiveInput1 == null) {
            Toast.makeText(activity, R.string.no_datapack, Toast.LENGTH_SHORT).show()
            return
        }

        // assemble composite input
        val defaultDatapackZipEntry = activity.getString(R.string.default_download_datapack_zipentry)
        val input = activity.layoutInflater.inflate(R.layout.zip_input, null) as LinearLayout
        val entryInput = input.findViewById<EditText>(R.id.zip_entry)
        entryInput.setText(defaultDatapackZipEntry)
        entryInput.setSelection(defaultDatapackZipEntry.length)
        input.addView(archiveInput1)

        // display sources
        AlertDialog.Builder(activity)
            .setTitle(R.string.action_unzip_datapack_from_archive)
            .setMessage(R.string.hint_unzip_from_archive)
            .setView(input)
            .setPositiveButton(R.string.action_ok) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                val zipEntry = entryInput.text.toString()
                if (zipEntry.isNotEmpty()) {
                    val childCount = input.childCount
                    for (i in 0 until childCount) {
                        val radioButton = input.getChildAt(i) as RadioButton
                        if (radioButton.id == archiveInput1.checkedRadioButtonId) {
                            val sourceFile = radioButton.tag.toString()
                            if (!activity.isFinishing && !activity.isDestroyed) {
                                FileTasks.launchUnzip(activity, sourceFile, zipEntry, databasePath, null)
                            }
                        }
                    }
                }
            }
            .setNegativeButton(R.string.action_cancel) { _: DialogInterface?, _: Int -> }
            .show()
    }

    /**
     * Copy data base from selected file in container
     *
     * @param activity    activity
     * @param directories directory names-values array
     */
    @SafeVarargs

    fun md5FromFile(activity: FragmentActivity, directories: Pair<Array<out CharSequence>, Array<String>>) {
        // collect sources
        val input = toRadioGroup(activity, directories.first, directories.second)
        if (input == null) {
            Toast.makeText(activity, R.string.no_datapack, Toast.LENGTH_SHORT).show()
            return
        }

        // display sources
        AlertDialog.Builder(activity)
            .setTitle(R.string.action_md5_ask)
            .setMessage(R.string.hint_md5_of_file)
            .setView(input)
            .setPositiveButton(R.string.action_ok) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                val childCount = input.childCount
                for (i in 0 until childCount) {
                    val radioButton = input.getChildAt(i) as RadioButton
                    if (radioButton.id == input.checkedRadioButtonId) {
                        val sourceFile = radioButton.tag.toString()
                        if (!activity.isFinishing && !activity.isDestroyed) {
                            FileTasks.launchMd5(activity, sourceFile, null)
                        }
                    }
                }
            }
            .setNegativeButton(R.string.action_cancel) { _: DialogInterface?, _: Int -> }
            .show()
    }
}
