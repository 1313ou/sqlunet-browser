/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.settings

import android.content.Context
import android.graphics.Typeface
import android.os.Environment
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Pair
import org.sqlunet.settings.StorageUtils.getExternalStorages
import org.sqlunet.settings.StorageUtils.getSortedStorageDirectories
import org.sqlunet.settings.StorageUtils.mbToString
import org.sqlunet.settings.StorageUtils.storageCapacity
import org.sqlunet.settings.StorageUtils.storageFreeAsString
import org.sqlunet.style.Colors
import org.sqlunet.style.Factories.spans
import org.sqlunet.style.Report.appendHeader
import org.sqlunet.style.Report.appendImage
import org.sqlunet.style.Spanner.Companion.appendWithSpans
import org.sqlunet.xnet.R
import java.io.File

/**
 * Storage styling utilities
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object StorageReports {

    private const val ENLARGE = 1.2f

    /**
     * Styled string
     *
     * @param activityContext context
     * @param dir storage directory
     * @return styled string
     */
    private fun toStyledString(activityContext: Context, dir: StorageUtils.StorageDirectory): CharSequence {
        val sb = SpannableStringBuilder()
        // icon
        appendImage(activityContext, sb, toIconId(dir.dir.type))
        sb.append(' ')
        // type
        appendWithSpans(sb, ' '.toString() + dir.dir.type.toDisplay() + ' ', BackgroundColorSpan(Colors.dirTypeBackColor), ForegroundColorSpan(Colors.dirTypeForeColor), RelativeSizeSpan(ENLARGE))
        sb.append('\n')
        // value
        appendWithSpans(sb, dir.dir.taggedValue, spans(Colors.dirValueBackColor, Colors.dirValueForeColor, StyleSpan(Typeface.ITALIC)))
        sb.append('\n')
        // status
        val suitable = dir.status == 0 && dir.fitsIn(activityContext)
        appendWithSpans(
            sb, mbToString(dir.free), spans(
                if (suitable) Colors.dirOkBackColor else Colors.dirFailBackColor,
                if (suitable) Colors.dirOkForeColor else Colors.dirFailForeColor
            )
        )
        return sb
    }

    /**
     * Styled fits-in string
     *
     * @param context context
     * @param dir     storage directory
     * @return styled string
     */
    private fun styledFitsIn(context: Context, dir: StorageUtils.StorageDirectory): CharSequence {
        val sb = SpannableStringBuilder()
        val fitsIn = dir.fitsIn(context)
        appendWithSpans(sb, if (fitsIn) "Fits in" else "Does not fit in", spans(if (fitsIn) Colors.dirOkBackColor else Colors.dirFailBackColor, if (fitsIn) Colors.dirOkForeColor else Colors.dirFailForeColor))
        return sb
    }

    /**
     * Styled status string
     *
     * @param dir storage directory
     * @return styled string
     */
    private fun styledStatus(dir: StorageUtils.StorageDirectory): CharSequence {
        val sb = SpannableStringBuilder()
        val status = dir.status()
        val statusOk = "Ok" == status.toString()
        appendWithSpans(sb, "Status: $status", spans(if (statusOk) Colors.dirOkBackColor else Colors.dirFailBackColor, if (statusOk) Colors.dirOkForeColor else Colors.dirFailForeColor))
        return sb
    }

    /**
     * Dir type to icon id
     *
     * @param type dir type
     * @return res id
     */
    private fun toIconId(type: StorageUtils.DirType): Int {
        return when (type) {
            StorageUtils.DirType.AUTO -> R.drawable.ic_storage_auto
            StorageUtils.DirType.APP_INTERNAL -> R.drawable.ic_storage_intern
            StorageUtils.DirType.APP_EXTERNAL_PRIMARY -> R.drawable.ic_storage_extern_primary
            StorageUtils.DirType.APP_EXTERNAL_SECONDARY -> R.drawable.ic_storage_extern_secondary
            StorageUtils.DirType.PUBLIC_EXTERNAL_PRIMARY, StorageUtils.DirType.PUBLIC_EXTERNAL_SECONDARY -> R.drawable.ic_storage_extern_public
        }
    }

    // N A M E S - V A L U E S

    /**
     * Get storage directories as names and values
     *
     * @param context context
     * @return pair of names and values
     */
    fun getXStyledStorageDirectoriesNamesValues(context: Context): Pair<Array<CharSequence>, Array<CharSequence>> {
        val names: MutableList<CharSequence> = ArrayList()
        val values: MutableList<CharSequence> = ArrayList()
        val dirs = getSortedStorageDirectories(context)
        for (dir in dirs) {
            if (dir.status != 0) {
                continue
            }

            // name
            names.add(toStyledString(context, dir))

            // value
            values.add(dir.dir.value)
        }
        return Pair(names.toTypedArray<CharSequence>(), values.toTypedArray<CharSequence>())
    }

    /**
     * Get storage directories as names and values
     *
     * @param context context
     * @return pair of names and values
     */
    fun getStyledStoragesNamesValues(context: Context): Pair<Array<CharSequence>, Array<CharSequence>> {
        val names: MutableList<CharSequence> = ArrayList()
        val values: MutableList<CharSequence> = ArrayList()
        val dirs = getSortedStorageDirectories(context)
        for (dir in dirs) {
            if (dir.status != 0) {
                continue
            }

            // name
            names.add(dir.dir.type.toDisplay() + ' ' + storageFreeAsString(dir.dir.value.toString()))

            // value
            values.add(dir.dir.file.absolutePath)
        }
        return Pair(names.toTypedArray<CharSequence>(), values.toTypedArray<CharSequence>())
    }

    /**
     * Get storage dirs names and values
     *
     * @param context context
     * @return pair of names and values
     */
    fun getStyledStorageDirectoriesNamesValues(context: Context): Pair<Array<CharSequence>, Array<String>> {
        val names: MutableList<CharSequence> = ArrayList()
        val values: MutableList<String> = ArrayList()
        val dirs = getSortedStorageDirectories(context)
        var i = 1
        for (dir in dirs) {
            val name = SpannableStringBuilder()
            var value: String
            val type = dir.dir.type
            if (type == StorageUtils.DirType.AUTO || type == StorageUtils.DirType.APP_INTERNAL) {
                value = dir.dir.fSValue.toString()
                appendHeader(name, "Files").append(' ').append(type.toShortDisplay()).append(' ').append(storageFreeAsString(value)).append('\n').append(value)
            } else {
                value = dir.dir.value.toString()
                appendHeader(name, "External files[" + i++ + "]").append(' ').append(type.toDisplay()).append(' ').append(storageFreeAsString(value)).append('\n').append(value)
            }
            names.add(name)
            values.add(value)
        }
        return Pair(names.toTypedArray<CharSequence>(), values.toTypedArray<String>())
    }

    /**
     * Get cache directories as names and values
     *
     * @param context context
     * @return pair of names and values
     */
    fun getStyledCachesNamesValues(context: Context): Pair<Array<CharSequence>, Array<String>> {
        val names: MutableList<CharSequence> = ArrayList()
        val values: MutableList<String> = ArrayList()
        var name: SpannableStringBuilder
        var value: String

        // external
        var i = 1
        for (dir2 in context.externalCacheDirs) {
            if (dir2 == null) {
                continue
            }
            value = dir2.absolutePath
            name = SpannableStringBuilder()
            appendHeader(name, "External cache[" + i++ + "]").append(' ').append(storageFreeAsString(value)).append('\n').append(value)
            names.add(name)
            values.add(value)
        }

        // internal
        val dir = context.cacheDir
        if (dir != null) {
            value = dir.absolutePath
            name = SpannableStringBuilder()
            appendHeader(name, "Cache").append(' ').append(storageFreeAsString(value)).append('\n').append(value)
            names.add(name)
            values.add(value)
        }

        // convert to array
        val entries = names.toTypedArray<CharSequence>()
        val entryValues = values.toTypedArray<String>()
        return Pair(entries, entryValues)
    }

    /**
     * Get download directories as names and values
     *
     * @param context context
     * @return pair of names and values
     */
    fun getStyledDownloadNamesValues(context: Context): Pair<Array<CharSequence>, Array<String>> {
        val names: MutableList<CharSequence> = ArrayList()
        val values: MutableList<String> = ArrayList()
        var name: SpannableStringBuilder
        var value: String
        val dirs: MutableList<File> = ArrayList()
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
        dirs.addAll(listOf(*context.getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)))
        @Suppress("DEPRECATION")
        dirs.addAll(listOf(*context.externalMediaDirs))
        for (dir in dirs) {
            value = dir.absolutePath
            name = SpannableStringBuilder()
            appendHeader(name, "Download").append(' ').append(storageFreeAsString(value)).append('\n').append(value)
            names.add(name)
            values.add(value)
        }

        // convert to array
        val entries = names.toTypedArray<CharSequence>()
        val entryValues = values.toTypedArray<String>()
        return Pair(entries, entryValues)
    }

    // D I R

    private fun appendDir(sb: SpannableStringBuilder, header: CharSequence, dir: File?): SpannableStringBuilder {
        if (dir != null) {
            appendHeader(sb, header).append(' ').append(storageFreeAsString(dir)).append('\n').append(dir.absolutePath).append('\n')
        }
        return sb
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
        val dirs = getSortedStorageDirectories(context)
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
        val storages = getExternalStorages(context)
        val physical = storages[StorageUtils.StorageType.PRIMARY_PHYSICAL]
        val emulated = storages[StorageUtils.StorageType.PRIMARY_EMULATED]
        val secondary = storages[StorageUtils.StorageType.SECONDARY]
        val sb = StringBuilder()
        if (physical != null) {
            sb.append("primary physical:\n")
            for (f in physical) {
                val s = f.absolutePath
                sb.append(s)
                sb.append(' ')
                sb.append(mbToString(storageCapacity(s)))
                sb.append(' ')
                try {
                    sb.append(if (Environment.isExternalStorageEmulated(f)) "emulated" else "not-emulated")
                } catch (_: Throwable) {
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
                sb.append(mbToString(storageCapacity(s)))
                sb.append(' ')
                try {
                    sb.append(if (Environment.isExternalStorageEmulated(f)) "emulated" else "not-emulated")
                } catch (_: Throwable) {
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
                sb.append(mbToString(storageCapacity(s)))
                sb.append(' ')
                try {
                    sb.append(if (Environment.isExternalStorageEmulated(f)) "emulated" else "not-emulated")
                } catch (_: Throwable) {
                }
                sb.append('\n')
            }
        }
        return sb.toString()
    }

    // S T Y L E D   R E P O R T S

    @SafeVarargs
    fun namesValuesToReportStyled(vararg directories: Pair<Array<CharSequence>, Array<String>>): CharSequence {
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

    /**
     * Report on storage dirs
     *
     * @param context context
     * @return report
     */
    fun reportStyledStorageDirectories(context: Context): CharSequence {
        val sb = SpannableStringBuilder()
        val dirs = getSortedStorageDirectories(context)
        var first = true
        for (dir in dirs) {
            if (first) {
                first = false
            } else {
                sb.append('\n')
            }
            sb.append(toStyledString(context, dir))
            sb.append(' ')
            //sb.append(styledFitsIn(dir))
            //sb.append(' ')
            //if (dir.fitsIn())
            run {
                //sb.append('|')
                //sb.append(' ')
                sb.append(styledStatus(dir))
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
        val storages = getExternalStorages(context)
        val physical = storages[StorageUtils.StorageType.PRIMARY_PHYSICAL]
        val emulated = storages[StorageUtils.StorageType.PRIMARY_EMULATED]
        val secondary = storages[StorageUtils.StorageType.SECONDARY]
        val sb = SpannableStringBuilder()
        if (physical != null) {
            appendImage(context, sb, R.drawable.ic_storage_intern)
            sb.append(' ')
            appendWithSpans(sb, " primary physical ", BackgroundColorSpan(Colors.storageTypeBackColor), ForegroundColorSpan(Colors.storageTypeForeColor), RelativeSizeSpan(ENLARGE))
            sb.append('\n')
            for (f in physical) {
                val s = f.absolutePath
                appendWithSpans(sb, s, spans(Colors.storageValueBackColor, Colors.storageValueForeColor, StyleSpan(Typeface.ITALIC)))
                sb.append('\n')
                sb.append(mbToString(storageCapacity(s)))
                sb.append(' ')
                try {
                    sb.append(if (Environment.isExternalStorageEmulated(f)) "emulated" else "not-emulated")
                } catch (_: Throwable) {
                }
                sb.append('\n')
            }
        }
        if (emulated != null) {
            appendImage(context, sb, R.drawable.ic_storage_extern_primary)
            sb.append(' ')
            appendWithSpans(sb, " primary emulated ", BackgroundColorSpan(Colors.storageTypeBackColor), ForegroundColorSpan(Colors.storageTypeForeColor), RelativeSizeSpan(ENLARGE))
            sb.append('\n')
            for (f in emulated) {
                val s = f.absolutePath
                appendWithSpans(sb, s, spans(Colors.storageValueBackColor, Colors.storageValueForeColor, StyleSpan(Typeface.ITALIC)))
                sb.append('\n')
                sb.append(mbToString(storageCapacity(s)))
                sb.append(' ')
                try {
                    sb.append(if (Environment.isExternalStorageEmulated(f)) "emulated" else "not-emulated")
                } catch (_: Throwable) {
                }
                sb.append('\n')
            }
        }
        if (secondary != null) {
            appendImage(context, sb, R.drawable.ic_storage_extern_secondary)
            sb.append(' ')
            sb.append(' ')
            appendWithSpans(sb, " secondary ", BackgroundColorSpan(Colors.storageTypeBackColor), ForegroundColorSpan(Colors.storageTypeForeColor), RelativeSizeSpan(ENLARGE))
            sb.append('\n')
            for (f in secondary) {
                val s = f.absolutePath
                appendWithSpans(sb, s, spans(Colors.storageValueBackColor, Colors.storageValueForeColor, StyleSpan(Typeface.ITALIC)))
                sb.append('\n')
                sb.append(mbToString(storageCapacity(s)))
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

    /**
     * Report directories
     *
     * @param context context
     * @return directories report
     */
    fun reportStyledDirs(context: Context): CharSequence {
        val sb = SpannableStringBuilder()
        appendDir(sb, "files dir", context.filesDir)
        appendDir(sb, "cache dir", context.cacheDir)
        appendDir(sb, "obb dir", context.obbDir)
        sb.append('\n')

        // external files
        var i = 1
        for (dir in context.getExternalFilesDirs(null)) {
            appendDir(sb, "external files dir [" + i++ + ']', dir)
        }

        // external caches
        i = 1
        for (dir in context.externalCacheDirs) {
            appendDir(sb, "external cache dir [" + i++ + ']', dir)
        }

        // obbs
        i = 1
        for (dir in context.obbDirs) {
            appendDir(sb, "external obb dir [" + i++ + ']', dir)
        }
        return sb
    }
}
