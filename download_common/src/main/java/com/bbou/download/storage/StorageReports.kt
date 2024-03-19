/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.storage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Environment
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import com.bbou.download.common.R
import java.io.File

/**
 * Storage reports
 */
object StorageReports {

    // G E T

    /**
     * Get download directories as names and values
     *
     * @param context context
     * @return pair of names and values
     */
    fun getStyledDownloadNamesValues(context: Context): Pair<Array<out CharSequence>, Array<String>> {
        val names: MutableList<CharSequence> = ArrayList()
        val values: MutableList<String> = ArrayList()
        val dirs: MutableList<File> = ArrayList()
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
        dirs.addAll(listOf(*context.getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)))
        @Suppress("DEPRECATION")
        dirs.addAll(listOf(*context.externalMediaDirs))
        for (dir in dirs) {
            val value = dir.absolutePath
            val name = SpannableStringBuilder()
            ReportUtils.appendHeader(name, "Download").append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value)
            names.add(name)
            values.add(value)
        }

        // convert to array
        val entries = names.toTypedArray<CharSequence>()
        val entryValues = values.toTypedArray<String>()
        return entries to entryValues
    }

    /**
     * Get cache directories as names (user-friendly styled display) and values (dir absolute path)
     *
     * @param context context
     * @return pair of names (user-friendly styled display) and values (dir absolute path)
     */
    @SuppressLint("ObsoleteSdkInt")
    @JvmStatic
    fun getStyledCachesNamesValues(context: Context): Pair<Array<out CharSequence>, Array<String>> {
        val names: MutableList<CharSequence> = ArrayList()
        val values: MutableList<String> = ArrayList()

        var dir = context.externalCacheDir
        if (dir != null) {
            val value = dir.absolutePath
            val name = SpannableStringBuilder()
            ReportUtils.appendHeader(name, "External cache")
                .append(' ')
                .append(StorageUtils.storageFreeAsString(value.toString()))
                .append('\n')
                .append(value)
            names.add(name)
            values.add(value)
        }

        dir = context.cacheDir
        if (dir != null) {
            val value = dir.absolutePath
            val name = SpannableStringBuilder()
            ReportUtils.appendHeader(name, "Cache").append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value)
            names.add(name)
            values.add(value)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            var i = 1
            for (dir2 in context.externalCacheDirs) {
                if (dir2 == null) {
                    continue
                }
                val value = dir2.absolutePath
                val name = SpannableStringBuilder()
                ReportUtils.appendHeader(name, "External cache[" + i++ + "]").append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value)
                names.add(name)
                values.add(value)
            }
        }

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (dir != null) {
            val value = dir.absolutePath
            val name = SpannableStringBuilder()
            ReportUtils.appendHeader(name, "Download").append(' ').append(StorageUtils.storageFreeAsString(value.toString())).append('\n').append(value)
            names.add(name)
            values.add(value)
        }

        // convert to array
        val entries = names.toTypedArray<CharSequence>()
        val entryValues = values.toTypedArray<String>()
        return entries to entryValues
    }

    /**
     * Get storage directories as names (user-friendly styled display) and values (dir absolute path)
     *
     * @param context context
     * @return pair of names (user-friendly styled display) and values (dir absolute path)
     */
    @JvmStatic
    fun getStyledStorageDirectoriesNamesValues(context: Context): Pair<Array<out CharSequence>, Array<String>> {
        val names: MutableList<CharSequence> = ArrayList()
        val values: MutableList<String> = ArrayList()
        val dirs = StorageUtils.getSortedStorageDirectories(context)
        for (dir in dirs) {
            if (dir.status != 0) {
                continue
            }

            // name
            names.add(dir.dir.type.toDisplay() + ' ' + StorageUtils.storageFreeAsString(dir.dir.value.toString()))

            // value
            values.add(dir.dir.file.absolutePath)
        }
        return names.toTypedArray<CharSequence>() to values.toTypedArray<String>()
    }

    /**
     * Get storage dirs names and values
     *
     * @param context context
     * @return pair of names and values
     */
    @JvmStatic
    fun getStyledStorageDirectoriesShortNamesValues(context: Context): Pair<Array<out CharSequence>, Array<String>> {
        val names: MutableList<CharSequence> = ArrayList()
        val values: MutableList<String> = ArrayList()
        val dirs = StorageUtils.getSortedStorageDirectories(context)
        for (dir in dirs) {
            if (dir.status != 0) {
                continue
            }
            // name
            names.add(dir.toShortString())

            // value
            values.add(dir.dir.file.absolutePath)
        }
        return names.toTypedArray<CharSequence>() to values.toTypedArray<String>()
    }

    // R E P O R T S

    /**
     * Report on external storage
     *
     * @param context context
     * @return report
     */
    fun reportStorageDirectories(context: Context): CharSequence {
        val sb = StringBuilder()
        var i = 1
        val dirs: List<StorageUtils.StorageDirectory> = StorageUtils.getSortedStorageDirectories(context)
        for (dir in dirs) {
            sb.append(i++)
            sb.append(' ')
            sb.append('-')
            sb.append(' ')
            sb.append(dir.toLongString())
            sb.append(' ')
            sb.append(if (dir.fitsIn(context)) "Fits in" else "Does not fit in")
            sb.append('\n')
            sb.append('\n')
        }
        return sb
    }

    /**
     * Report on external storage
     *
     * @param context context
     * @return report
     */
    fun reportExternalStorage(context: Context): CharSequence {
        val storages: Map<FormatUtils.StorageType, Array<File>> = StorageUtils.getExternalStorages(context)
        val physical = storages[FormatUtils.StorageType.PRIMARY_PHYSICAL]
        val emulated = storages[FormatUtils.StorageType.PRIMARY_EMULATED]
        val secondary = storages[FormatUtils.StorageType.SECONDARY]
        val sb = StringBuilder()
        if (physical != null) {
            sb.append("primary physical:\n")
            for (f in physical) {
                val s = f.absolutePath
                sb.append(s)
                sb.append(' ')
                sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sb.append(' ')
                    try {
                        sb.append(if (Environment.isExternalStorageEmulated(f)) "emulated" else "not-emulated")
                    } catch (_: Throwable) {

                    }
                }
                sb.append('\n')
            }
        }
        if (emulated != null) {
            sb.append("primary emulated:\n")
            for (f in emulated) {
                val s = f.absolutePath
                sb.append(s)
                sb.append(' ')
                sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sb.append(' ')
                    try {
                        sb.append(if (Environment.isExternalStorageEmulated(f)) "emulated" else "not-emulated")
                    } catch (_: Throwable) {
                    }
                }
                sb.append('\n')
            }
        }
        if (secondary != null) {
            sb.append("secondary:\n")
            for (f in secondary) {
                val s = f.absolutePath
                sb.append(s)
                sb.append(' ')
                sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sb.append(' ')
                    try {
                        sb.append(if (Environment.isExternalStorageEmulated(f)) "emulated" else "not-emulated")
                    } catch (_: Throwable) {
                    }
                }
                sb.append('\n')
            }
        }
        return sb.toString()
    }

    /**
     * Report directories
     *
     * @param context context
     * @return directories report
     */
    fun reportStyledDirs(context: Context): CharSequence {
        val sb = SpannableStringBuilder()
        appendStyledDir(sb, "files dir", context.filesDir)
        appendStyledDir(sb, "cache dir", context.cacheDir)
        appendStyledDir(sb, "obb dir", context.obbDir)
        sb.append('\n')

        // external files
        var i = 1
        for (dir in ContextCompat.getExternalFilesDirs(context, null)) {
            appendStyledDir(sb, "external files dir [" + i++ + ']', dir)
        }

        // external caches
        i = 1
        for (dir in ContextCompat.getExternalCacheDirs(context)) {
            appendStyledDir(sb, "external cache dir [" + i++ + ']', dir)
        }

        // obbs
        i = 1
        for (dir in ContextCompat.getObbDirs(context)) {
            appendStyledDir(sb, "external obb dir [" + i++ + ']', dir)
        }
        return sb
    }

    /**
     * Report on storage dirs
     *
     * @param context context
     * @return report
     */
    fun reportStyledStorageDirectories(context: Context): CharSequence {
        val sb = SpannableStringBuilder()
        val dirs: List<StorageUtils.StorageDirectory> = StorageUtils.getSortedStorageDirectories(context)
        var first = true
        for (dir in dirs) {
            if (first) {
                first = false
            } else {
                sb.append('\n')
            }
            sb.append(dirToStyledString(context, dir))
            sb.append(' ')
            //sb.append(styledFitsIn(dir));
            //sb.append(' ');
            //if (dir.fitsIn())
            run {
                //sb.append('|');
                //sb.append(' ');
                sb.append(dirStatusToStyledString(dir))
            }
        }
        return sb
    }

    /**
     * Report on external storage
     *
     * @param context context
     * @return report
     */
    fun reportStyledExternalStorage(context: Context): CharSequence {
        val storages: Map<FormatUtils.StorageType, Array<File>> = StorageUtils.getExternalStorages(context)
        val physical = storages[FormatUtils.StorageType.PRIMARY_PHYSICAL]
        val emulated = storages[FormatUtils.StorageType.PRIMARY_EMULATED]
        val secondary = storages[FormatUtils.StorageType.SECONDARY]
        val sb = SpannableStringBuilder()
        if (physical != null) {
            ReportUtils.appendImage(context, sb, R.drawable.ic_storage_intern)
            sb.append(' ')
            ReportUtils.appendWithSpans(sb, " primary physical ", BackgroundColorSpan(ReportUtils.storageTypeBackColor), ForegroundColorSpan(ReportUtils.storageTypeForeColor), RelativeSizeSpan(ReportUtils.ENLARGE))
            sb.append('\n')
            for (f in physical) {
                val s = f.absolutePath
                ReportUtils.appendWithSpans(sb, s, ReportUtils.spans(ReportUtils.storageValueBackColor, ReportUtils.storageValueForeColor, StyleSpan(Typeface.ITALIC)))
                sb.append('\n')
                sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)))
                sb.append(' ')
                try {
                    sb.append(if (Environment.isExternalStorageEmulated(f)) "emulated" else "not-emulated")
                } catch (_: Throwable) {
                }
                sb.append('\n')
            }
        }
        if (emulated != null) {
            ReportUtils.appendImage(context, sb, R.drawable.ic_storage_extern_primary)
            sb.append(' ')
            ReportUtils.appendWithSpans(sb, " primary emulated ", BackgroundColorSpan(ReportUtils.storageTypeBackColor), ForegroundColorSpan(ReportUtils.storageTypeForeColor), RelativeSizeSpan(ReportUtils.ENLARGE))
            sb.append('\n')
            for (f in emulated) {
                val s = f.absolutePath
                ReportUtils.appendWithSpans(sb, s, ReportUtils.spans(ReportUtils.storageValueBackColor, ReportUtils.storageValueForeColor, StyleSpan(Typeface.ITALIC)))
                sb.append('\n')
                sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)))
                sb.append(' ')
                try {
                    sb.append(if (Environment.isExternalStorageEmulated(f)) "emulated" else "not-emulated")
                } catch (_: Throwable) {
                }
                sb.append('\n')
            }
        }
        if (secondary != null) {
            ReportUtils.appendImage(context, sb, R.drawable.ic_storage_extern_secondary)
            sb.append(' ')
            sb.append(' ')
            ReportUtils.appendWithSpans(sb, " secondary ", BackgroundColorSpan(ReportUtils.storageTypeBackColor), ForegroundColorSpan(ReportUtils.storageTypeForeColor), RelativeSizeSpan(ReportUtils.ENLARGE))
            sb.append('\n')
            for (f in secondary) {
                val s = f.absolutePath
                ReportUtils.appendWithSpans(sb, s, ReportUtils.spans(ReportUtils.storageValueBackColor, ReportUtils.storageValueForeColor, StyleSpan(Typeface.ITALIC)))
                sb.append('\n')
                sb.append(StorageUtils.mbToString(StorageUtils.storageCapacity(s)))
                sb.append(' ')
                try {
                    sb.append(if (Environment.isExternalStorageEmulated(f)) "emulated" else "not-emulated")
                } catch (_: Throwable) {
                }
                sb.append('\n')
            }
        }
        return sb
    }

    private fun appendStyledDir(sb: SpannableStringBuilder, header: CharSequence, dir: File?): SpannableStringBuilder {
        dir?.let {
            ReportUtils.appendHeader(sb, header).append(' ').append(StorageUtils.storageFreeAsString(dir)).append('\n').append(dir.absolutePath).append('\n')
        }
        return sb
    }

    // S T Y L E D   R E P O R T S

    /**
     * Convert name-value paris to styled report
     *
     * @param directories name-value pairs
     * @return styled report
     */
    @SafeVarargs
    fun namesValuesToReportStyled(vararg directories: Pair<Array<out CharSequence>, Array<String>>): CharSequence {
        val sb = SpannableStringBuilder()
        for (namesValues in directories) {
            val names = namesValues.first
            for (name in names) {
                sb.append(name)
                sb.append('\n')
            }
            sb.append('\n')
        }
        return sb
    }

    // D I R S

    /**
     * Styled status string
     *
     * @param dir storage directory
     * @return styled string
     */
    private fun dirStatusToStyledString(dir: StorageUtils.StorageDirectory): CharSequence {
        val sb = SpannableStringBuilder()
        val status: CharSequence = dir.status()
        val statusOk = "Ok" == status.toString()
        ReportUtils.appendWithSpans(sb, "Status: $status", ReportUtils.spans(if (statusOk) ReportUtils.dirOkBackColor else ReportUtils.dirFailBackColor, if (statusOk) ReportUtils.dirOkForeColor else ReportUtils.dirFailForeColor))
        return sb
    }

    /**
     * Dir type to icon id
     *
     * @param type dir type
     * @return res id
     */
    private fun dirTypeToIconId(type: StorageUtils.DirType): Int {
        val resId = when (type) {
            StorageUtils.DirType.AUTO -> R.drawable.ic_storage_auto
            StorageUtils.DirType.APP_INTERNAL -> R.drawable.ic_storage_intern
            StorageUtils.DirType.APP_EXTERNAL_PRIMARY -> R.drawable.ic_storage_extern_primary
            StorageUtils.DirType.APP_EXTERNAL_SECONDARY -> R.drawable.ic_storage_extern_secondary
            StorageUtils.DirType.PUBLIC_EXTERNAL_PRIMARY, StorageUtils.DirType.PUBLIC_EXTERNAL_SECONDARY -> R.drawable.ic_storage_extern_public
        }
        return resId
    }

    /**
     * Styled string
     *
     * @param context context
     * @param dir     storage directory
     * @return styled string
     */
    private fun dirToStyledString(context: Context, dir: StorageUtils.StorageDirectory): CharSequence {

        val sb = SpannableStringBuilder()

        // icon
        ReportUtils.appendImage(context, sb, dirTypeToIconId(dir.dir.type))
        sb.append(' ')
        // type
        ReportUtils.appendWithSpans(sb, ' ' + dir.dir.type.toDisplay() + ' ', BackgroundColorSpan(ReportUtils.dirTypeBackColor), ForegroundColorSpan(ReportUtils.dirTypeForeColor), RelativeSizeSpan(ReportUtils.ENLARGE))
        sb.append('\n')
        // value
        ReportUtils.appendWithSpans(sb, dir.dir.getTaggedValue(), ReportUtils.spans(ReportUtils.dirValueBackColor, ReportUtils.dirValueForeColor, StyleSpan(Typeface.ITALIC)))
        sb.append('\n')
        // status
        val suitable = dir.status == 0 && dir.fitsIn(context)
        ReportUtils.appendWithSpans(
            sb, StorageUtils.mbToString(dir.free), ReportUtils.spans(
                if (suitable) ReportUtils.dirOkBackColor else ReportUtils.dirFailBackColor,
                if (suitable) ReportUtils.dirOkForeColor else ReportUtils.dirFailForeColor
            )
        )
        return sb
    }
}
