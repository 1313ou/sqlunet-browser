/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.storage

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.os.UserManager
import android.util.Log
import com.bbou.download.common.R
import com.bbou.download.preference.Settings
import java.io.File
import java.util.EnumMap
import java.util.Locale

/**
 * Storage utilities
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object StorageUtils {

    private const val TAG = "StorageUtils"

    /**
     * Datapack size
     */
    private var DATAPACK_SIZE_MB = Float.NaN

    // C O L L E C T

    /**
     * Get list of directories
     *
     * @param context context
     * @return list of storage directories
     */
    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getDirectories(context: Context): MutableList<Directory> {
        val result: MutableList<Directory> = ArrayList()
        var dir: File?

        // a p p - s p e c i f i c - p o s s i b l y   a d o p t e d
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dir = context.filesDir
            if (dir != null) {
                result.add(Directory(dir, DirType.AUTO))
            }
        }

        // a p p - s p e c i f i c
        // application-specific secondary external storage or primary external
        val dirs = context.getExternalFilesDirs(null)
        if (dirs.isNotEmpty()) {
            // preferably secondary storage (index >= 1)
            for (i in 1 until dirs.size) {
                dir = dirs[i]
                if (dir != null) {
                    result.add(Directory(dir, DirType.APP_EXTERNAL_SECONDARY))
                }
            }

            // primary storage (index == 0)
            dir = dirs[0]
            if (dir != null) {
                result.add(Directory(dir, DirType.APP_EXTERNAL_PRIMARY))
            }
        }

        // p u b l i c
        // top-level public external storage directory
        // obsolete

        // i n t e r n a l
        // internal private storage
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            dir = context.filesDir
            if (dir != null) {
                result.add(Directory(dir, DirType.APP_INTERNAL))
            }
        }
        return result
    }

    /**
     * Make list of storage directories from list of directories
     *
     * @param dirs list of directories
     * @return list of storage storages
     */
    private fun directories2StorageDirectories(dirs: Iterable<Directory>): MutableList<StorageDirectory> {
        val storages: MutableList<StorageDirectory> = ArrayList()
        for (dir in dirs) {

            // make path
            // true if and only if the directory was created
            val wasCreated = dir.file.mkdirs()

            // status and size
            val stats = storageStats(dir.file.absolutePath)
            val status = qualifies(dir.file, dir.type)
            storages.add(StorageDirectory(dir, stats[STORAGE_FREE], stats[STORAGE_OCCUPANCY], status))

            // restore
            if (wasCreated && dir.file.exists()) {
                dir.file.delete()
            }
        }
        return storages
    }

    /**
     * Get list of storage storages
     *
     * @param context context
     * @return list of storage storages
     */
    private fun getStorageDirectories(context: Context): MutableList<StorageDirectory> {
        val dirs = getDirectories(context)
        return directories2StorageDirectories(dirs)
    }

    /**
     * Get sorted list of storage directories
     *
     * @param context context
     * @return list of storage directories (desc-) sorted by size and type
     */
    fun getSortedStorageDirectories(context: Context): MutableList<StorageDirectory> {
        val storageDirectories = getStorageDirectories(context)
        storageDirectories.sort()
        return storageDirectories
    }

    // D I S C O V E R  S T O R A G E

    /**
     * Get external storage
     *
     * @param context context
     * @return map per type of external storage
     */
    fun getExternalStorages(context: Context): Map<FormatUtils.StorageType, Array<File>> {
        // result set of paths
        val dirs: MutableMap<FormatUtils.StorageType, Array<File>> = EnumMap(FormatUtils.StorageType::class.java)

        // p r i m a r y
        // primary emulated
        val primaryEmulated = discoverPrimaryEmulatedExternalStorage(context)
        primaryEmulated?.let { dirs[FormatUtils.StorageType.PRIMARY_EMULATED] = arrayOf(it) }

        // primary emulated
        val physicalEmulated = discoverPrimaryPhysicalExternalStorage()
        physicalEmulated?.let { dirs[FormatUtils.StorageType.PRIMARY_PHYSICAL] = arrayOf(it) }

        // s e c o n d a r y
        val secondaryStorages = discoverSecondaryExternalStorage()
        secondaryStorages?.let {
            if (it.isNotEmpty()) {
                dirs[FormatUtils.StorageType.SECONDARY] = secondaryStorages
            }
        }
        return dirs
    }

    /**
     * Discover primary emulated external storage directory
     *
     * @param context context
     * @return primary emulated external storage directory
     */
    private fun discoverPrimaryEmulatedExternalStorage(context: Context): File? {
        // primary emulated sdcard
        val emulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET")
        if (emulatedStorageTarget != null && emulatedStorageTarget.isNotEmpty()) {
            // device has emulated extStorage
            // external extStorage paths should have userId burned into them
            val userId: String = getUserId(context)

            // /extStorage/emulated/0[1,2,...]
            return if ( /*userId == null ||*/userId.isEmpty()) {
                File(emulatedStorageTarget)
            } else {
                File(emulatedStorageTarget + File.separatorChar + userId)
            }
        }
        return null
    }

    /**
     * Discover primary physical external storage directory
     *
     * @return primary physical external storage directory
     */
    private fun discoverPrimaryPhysicalExternalStorage(): File? {
        val externalStorage = System.getenv("EXTERNAL_STORAGE")
        // device has physical external extStorage; use plain paths.
        return if (externalStorage != null && externalStorage.isNotEmpty()) {
            File(externalStorage)
        } else null
    }

    /**
     * Discover secondary external storage directories
     *
     * @return secondary external storage directories
     */
    private fun discoverSecondaryExternalStorage(): Array<File>? {
        // all secondary sdcards (all except primary) separated by ":"
        var secondaryStoragesEnv = System.getenv("SECONDARY_STORAGE")
        if (secondaryStoragesEnv == null || secondaryStoragesEnv.isEmpty()) {
            secondaryStoragesEnv = System.getenv("EXTERNAL_SDCARD_STORAGE")
        }

        // addItem all secondary storages
        if (secondaryStoragesEnv != null && secondaryStoragesEnv.isNotEmpty()) {
            // all secondary sdcards split into array
            val paths = secondaryStoragesEnv.split(File.pathSeparator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val dirs: MutableList<File> = java.util.ArrayList()
            for (path in paths) {
                val dir = File(path)
                if (dir.exists()) {
                    dirs.add(dir)
                }
            }
            return dirs.toTypedArray<File>()
        }
        return null
    }

    // U S E R I D

    /**
     * User id
     *
     * @param context context
     * @return user id
     */
    private fun getUserId(context: Context): String {
        val manager = context.getSystemService(Context.USER_SERVICE) as UserManager
        val user = Process.myUserHandle()
        val userSerialNumber = manager.getSerialNumberForUser(user)
        // Log.d("USER", "userSerialNumber = " + userSerialNumber)
        return userSerialNumber.toString()
    }

    // Q U A L I F I E S

    /**
     * Whether the dir qualifies as storage
     *
     * @param dir directory
     * @return code if it qualifies
     */
    @Suppress("KotlinConstantConditions")
    @SuppressLint("ObsoleteSdkInt")
    private fun qualifies(dir: File?, type: DirType): Int {
        var status = 0

        // dir
        if (dir == null) {
            status = status or StorageDirectory.NULL_DIR
            return status
        }

        // state
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            when (type) {
                DirType.APP_EXTERNAL_SECONDARY, DirType.APP_EXTERNAL_PRIMARY, DirType.PUBLIC_EXTERNAL_PRIMARY, DirType.PUBLIC_EXTERNAL_SECONDARY -> try {
                    val state = Environment.getExternalStorageState(dir)
                    if (Environment.MEDIA_MOUNTED != state) {
                        Log.d(TAG, "Storage state of $dir: $state")
                        status = status or StorageDirectory.NOT_MOUNTED
                    }
                } catch (_: Throwable) {
                }

                DirType.APP_INTERNAL, DirType.AUTO -> {}
            }
        }

        // exists
        if (!dir.exists()) {
            status = status or StorageDirectory.NOT_EXISTS
        }

        // make sure it is a directory
        if (!dir.isDirectory) {
            status = status or StorageDirectory.NOT_DIR
        }

        // make sure it is a directory
        if (!dir.canWrite()) {
            status = status or StorageDirectory.NOT_WRITABLE
        }
        return status
    }

    // C A P A C I T Y

    /**
     * Index of free storage value
     */
    private const val STORAGE_FREE = 0

    /**
     * Index of capacity value
     */
    private const val STORAGE_CAPACITY = 1

    /**
     * Index of occupancy value
     */
    private const val STORAGE_OCCUPANCY = 2

    /**
     * Storage data at path
     *
     * @param path path
     * @return data
     */
    private fun storageStats(path: String): FloatArray {
        val stats = FloatArray(3)
        stats[STORAGE_FREE] = storageFree(path)
        stats[STORAGE_CAPACITY] = storageCapacity(path)
        stats[STORAGE_OCCUPANCY] = if (stats[STORAGE_CAPACITY] == 0f) 0f else 100f * ((stats[STORAGE_CAPACITY] - stats[STORAGE_FREE]) / stats[STORAGE_CAPACITY])
        return stats
    }

    /**
     * Get free storage
     *
     * @param context context
     * @param target directory or file system object
     * @return free storage report
     */
    @Suppress("unused")
    fun getFree(context: Context, target: String): String {
        val file = File(target)
        val dir: String? = if (file.isDirectory) file.absolutePath else file.parent
        dir?.let {
            val r = storageStats(it)
            val df = r[STORAGE_FREE]
            val dc = r[STORAGE_CAPACITY]
            val dp = r[STORAGE_OCCUPANCY]
            return context.getString(R.string.format_storage_data, dir, mbToString(df), mbToString(dc), dp)
        }
        return ""
    }

    /**
     * Free storage at path
     *
     * @param path path
     * @return free storage in megabytes
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun storageFree(path: String): Float {
        return try {
            val stat = StatFs(path)
            val bytes: Float = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                (stat.availableBlocksLong * stat.blockSizeLong).toFloat()
            } else {
                @Suppress("DEPRECATION")
                (stat.availableBlocks * stat.blockSize).toFloat()
            }
            bytes / (1024f * 1024f)
        } catch (_: Throwable) {
            Float.NaN
        }
    }

    /**
     * Free space for dir
     *
     * @param dir dir
     * @return free space as string
     */
    fun storageFreeAsString(dir: File): CharSequence {
        return storageFreeAsString(dir.absolutePath)
    }

    /**
     * Free space for dir
     *
     * @param dir dir
     * @return free space as string
     */
    fun storageFreeAsString(dir: String): CharSequence {
        return mbToString(storageFree(dir))
    }

    /**
     * Storage capacity at path
     *
     * @param path path
     * @return storage capacity in megabytes
     */
    @SuppressLint("ObsoleteSdkInt")
    fun storageCapacity(path: String?): Float {
        return try {
            val stat = StatFs(path)
            val bytes: Float = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                (stat.blockCountLong * stat.blockSizeLong).toFloat()
            } else {
                @Suppress("DEPRECATION")
                (stat.blockCount * stat.blockSize).toFloat()
            }
            bytes / (1024f * 1024f)
        } catch (_: Throwable) {
            Float.NaN
        }
    }

    // H U M A N - R E A D A B L E   B Y T E   C O U N T

    /**
     * Megabytes to string
     *
     * @param mb megabytes
     * @return string
     */
    fun mbToString(mb: Float): String {
        if (java.lang.Float.isNaN(mb)) {
            return "[N/A size]"
        }
        return if (mb > 1000f) {
            String.format(Locale.ENGLISH, "%.1f GB", mb / 1024f)
        } else {
            String.format(Locale.ENGLISH, "%.1f MB", mb)
        }
    }

    // T Y P E S

    /*
	 * Storage types
	 */
    /**
     * Directory type
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    enum class DirType {

        /**
         * Auto
         */
        AUTO,

        /**
         * App-external secondary
         */
        APP_EXTERNAL_SECONDARY,

        /**
         * App-external primary
         */
        APP_EXTERNAL_PRIMARY,

        /**
         * Public external secondary
         */
        PUBLIC_EXTERNAL_SECONDARY,

        /**
         * Public external primary
         */
        PUBLIC_EXTERNAL_PRIMARY,

        /**
         * App-internal
         */
        APP_INTERNAL;

        /**
         * Directory type to label
         *
         * @return display label
         */
        fun toDisplay(): String {
            return when (this) {
                AUTO -> "auto (internal or adopted)"
                APP_EXTERNAL_SECONDARY -> "secondary"
                APP_EXTERNAL_PRIMARY -> "primary"
                PUBLIC_EXTERNAL_PRIMARY -> "public primary"
                PUBLIC_EXTERNAL_SECONDARY -> "public secondary"
                APP_INTERNAL -> "internal"
            }
        }

        companion object {

            /**
             * Compare (sort by preference)
             *
             * @param type1 type 1
             * @param type2 type 2
             * @return order
             */
            fun compare(type1: DirType, type2: DirType): Int {
                val i1 = type1.ordinal
                val i2 = type2.ordinal
                return if (i1 < i2) -1 else if (i1 == i2) 0 else 1
            }
        }
    }

    /**
     * Directory with type
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    class Directory(
        /**
         * Directory (File object)
         */
        val file: File,
        /**
         * Directory type
         */
        val type: DirType,
    ) {

        /**
         * Directory path
         */
        val value: CharSequence
            get() = if (DirType.AUTO == type) {
                DirType.AUTO.toString()
            } else file.absolutePath

        /**
         * Get tagged value
         *
         * @return value, tagged with 'auto' in that case
         */
        fun getTaggedValue(): CharSequence {
            return if (DirType.AUTO == type) {
                DirType.AUTO.toString() + ':' + file.absolutePath
            } else file.absolutePath
        }
    }

    /**
     * Storage directory
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    class StorageDirectory(
        /**
         * Directory
         */
        val dir: Directory,
        /**
         * Free megabytes
         */
        val free: Float,
        /**
         * Occupancy
         */
        private val occupancy: Float,
        /**
         * Status
         */
        val status: Int,
    ) : Comparable<StorageDirectory> {

        override fun toString(): String {
            return String.format(Locale.ENGLISH, "%s %s %s %.1f%% %s", dir.type.toDisplay(), dir.file.absolutePath, mbToString(free), occupancy, status())
        }

        /**
         * Short string
         *
         * @return short string
         */
        fun toShortString(): CharSequence {
            return String.format(Locale.ENGLISH, "%s %s %s free", dir.type.toDisplay(), dir.file.absolutePath, mbToString(free))
        }

        /**
         * Long string
         *
         * @return long string
         */
        fun toLongString(): String {
            return String.format(Locale.ENGLISH, "%s\n%s\n%s free %.1f%% occupancy\n%s", dir.type.toDisplay(), dir.file.absolutePath, mbToString(free), occupancy, status())
        }

        /**
         * Equals
         */
        override fun equals(other: Any?): Boolean {
            if (other !is StorageDirectory) {
                return false
            }
            return dir == other.dir
        }

        /**
         * Comparison (most suitable first)
         */
        override fun compareTo(other: StorageDirectory): Int {
            if (status != other.status) {
                return if (status == 0) -1 else 1
            }
            if (free != other.free) {
                return -free.compareTo(other.free)
            }
            return if (dir.type != other.dir.type) {
                DirType.compare(dir.type, other.dir.type)
            } else 0
        }

        /**
         * Storage status
         *
         * @return status string
         */
        fun status(): CharSequence {
            if (status == 0) {
                return "Ok"
            }
            val status = StringBuilder()
            var first = true
            if (this.status and NULL_DIR != 0) {
                status.append("Is null dir")
                first = false
            }
            if (this.status and NOT_MOUNTED != 0) {
                if (!first) {
                    status.append(" | ")
                }
                status.append("Is not mounted")
                first = false
            }
            if (this.status and NOT_EXISTS != 0) {
                if (!first) {
                    status.append(" | ")
                }
                status.append("Does not exist")
                first = false
            }
            if (this.status and NOT_DIR != 0) {
                if (!first) {
                    status.append(" | ")
                }
                status.append("Is not a dir")
                first = false
            }
            if (this.status and NOT_WRITABLE != 0) {
                if (!first) {
                    status.append(" | ")
                }
                status.append("Is not writable")
            }
            return status
        }

        /**
         * Capacity test
         *
         * @return true if datapack fits in storage
         */
        fun fitsIn(context: Context): Boolean {
            if (java.lang.Float.isNaN(DATAPACK_SIZE_MB)) {
                val size = context.resources.getInteger(R.integer.size_datapack_working_total).toFloat()
                DATAPACK_SIZE_MB = (size + size / 10f) / (1024f * 1024f)
            }
            return !java.lang.Float.isNaN(free) && free >= DATAPACK_SIZE_MB
        }

        /**
         * Hash code
         */
        override fun hashCode(): Int {
            return dir.hashCode()
        }

        companion object {

            /**
             * Status flag: null dir
             */
            const val NULL_DIR = 0x0001

            /**
             * Status flag: storage is not mounted
             */
            const val NOT_MOUNTED = 0x0002

            /**
             * Status flag: directory does not exist
             */
            const val NOT_EXISTS = 0x0004

            /**
             * Status flag: not a directory
             */
            const val NOT_DIR = 0x0008

            /**
             * Status flag: directory is not writable
             */
            const val NOT_WRITABLE = 0x0010
        }
    }

    // T A R G E T

    /**
     * Get File for target
     *
     * @param context context
     * @return
     */
    @Suppress("unused")
    fun getDatapackFile(context: Context): File {
        return File(Settings.getDatapackDir(context), context.getString(R.string.default_target_file))
    }

    // U T I L S

    // Name-value pairs

    /**
     * Get storage dirs names and values
     *
     * @param nameValues1 values 1
     * @param nameValues2 values 2
     * @return pair of names and values
     */
    fun mergeNamesValues(nameValues1: Pair<Array<out CharSequence>, Array<String>>, nameValues2: Pair<Array<out CharSequence>, Array<String>>): Pair<Array<out CharSequence>, Array<String>> {
        val names: MutableList<CharSequence> = ArrayList()
        val values: MutableList<String> = ArrayList()
        names.addAll(nameValues1.first)
        names.addAll(nameValues2.first)
        values.addAll(nameValues1.second)
        values.addAll(nameValues2.second)
        return names.toTypedArray<CharSequence>() to values.toTypedArray<String>()
    }
}
