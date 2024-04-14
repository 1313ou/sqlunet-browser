/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.choose

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RadioButton
import android.widget.RadioGroup
import java.io.File

/**
 * Chooser
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Chooser {

    /**
     * Collect files into radio group
     *
     * @param context context
     * @param directories directories
     * @return radio group
     */
    @SafeVarargs
    fun toRadioGroup(context: Context, directories: Array<String>): RadioGroup? {
        val names = directories.map { File(it).name }.toTypedArray()
        return toRadioGroup(context, names, directories)
    }

    /**
     * Collect files into radio group
     *
     * @param context context
     * @param names names
     * @param values values
     * @return radio group
     */
    @SuppressLint("SetTextI18n")

    @SafeVarargs
    fun toRadioGroup(context: Context, names: Array<out CharSequence>, values: Array<String>): RadioGroup? {
        val group = RadioGroup(context)
        var i = 0
        while (i < values.size && i < names.size) {

            val name = names[i]
            val value = values[i]
            val dir = File(value)
            if (dir.exists()) {

                // files in dir
                val files = dir.listFiles()
                if (files != null) {
                    for (file in files) {
                        if (!file.isDirectory && file.exists()) {
                            val radioButton = RadioButton(context)
                            radioButton.text = "${name}/${file.name}"
                            radioButton.tag = file.absolutePath
                            group.addView(radioButton)
                        }
                    }
                }
            }
            i++
        }
        return if (group.childCount == 0) null else group
    }
}
