/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers.choose

import android.content.DialogInterface
import android.text.SpannableStringBuilder
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bbou.deploy.workers.MD5
import com.bbou.download.choose.Chooser
import com.bbou.download.storage.ReportUtils.appendHeader
import com.bbou.download.storage.StorageReports
import com.bbou.download.storage.StorageUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import com.bbou.download.common.R as CommonR
import org.sqlunet.core.R as CoreR

/**
 * Md5 async task
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object MD5Chooser {

    /**
     * MD5 from selected file in one of storage directories or caches
     *
     * @param activity activity
     */
    fun md5(activity: FragmentActivity) {
        val nameValues1 = StorageReports.getStyledStorageDirectoriesShortNamesValues(activity)
        val nameValues2 = StorageReports.getStyledCachesNamesValues(activity)
        val nameValues = StorageUtils.mergeNamesValues(nameValues1, nameValues2)
        md5(activity, nameValues)
    }

    /**
     * MD5 from selected file in one of storage directories or caches
     *
     * @param directories directory names-values arrays
     */
    fun md5(activity: FragmentActivity, directories: Pair<Array<out CharSequence>, Array<String>>) {
        val radioGroup = Chooser.toRadioGroup(activity, directories.first, directories.second)
        md5(activity, radioGroup)
    }

    /**
     * MD5 from selected file in one of storage directories or caches
     *
     * @param directories directory values array
     */
    fun md5(activity: FragmentActivity, directories: Array<String>) {
        val radioGroup = Chooser.toRadioGroup(activity, directories)
        md5(activity, radioGroup)
    }

    /**
     * MD5 from selected file in one of storage directories or caches
     *
     * @param radioGroup radioGroup
     */
    private fun md5(activity: FragmentActivity, radioGroup: RadioGroup?) {

        if (radioGroup == null) {
            Toast.makeText(activity, CommonR.string.no_datapack, Toast.LENGTH_SHORT).show()
            return
        }

        // display targets
        MaterialAlertDialogBuilder(activity, CoreR.style.MyM3AlertDialogOverlay)
            .setTitle(CommonR.string.action_md5_ask)
            .setMessage(CommonR.string.hint_md5_of_file)
            .setView(radioGroup)
            .setPositiveButton(CommonR.string.action_ok) { dialog: DialogInterface, _ ->

                // selected
                dialog.dismiss()
                val childCount = radioGroup.childCount
                for (i in 0 until childCount) {
                    val radioButton = radioGroup.getChildAt(i) as RadioButton
                    if (radioButton.id == radioGroup.checkedRadioButtonId) {

                        val sourceFile = radioButton.tag.toString()
                        if (File(sourceFile).exists()) {

                            // process target
                            MD5.md5(activity, sourceFile) { computedResult: String? ->

                                if (!activity.isFinishing && !activity.isDestroyed) {

                                    // format result
                                    val sb = SpannableStringBuilder()
                                        .appendHeader(activity.getString(CommonR.string.md5_computed))
                                        .append('\n')
                                        .append(computedResult ?: activity.getString(CommonR.string.status_task_failed))

                                    // display result
                                    MD5.md5Dialog(activity, sb, sourceFile)
                                }
                            }
                        } else {
                            MaterialAlertDialogBuilder(activity, CoreR.style.MyM3AlertDialogOverlay)
                                .setTitle(sourceFile)
                                .setMessage(activity.getString(CommonR.string.status_error_no_file))
                                .show()
                        }
                    }
                }
            }
            .setNegativeButton(CommonR.string.action_cancel) { _, _ -> }
            .show()
    }
}
