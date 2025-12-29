/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.settings

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.os.UserManager
import android.util.Log
import org.sqlunet.xnet.R
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
     * Database size
     */
    private var DATABASE_SIZE_MB = Float.NaN

    fun isAuto(value: String): Boolean {
        return DirType.AUTO.toString() == value
    }

    val AUTO = DirType.AUTO.toString()

    val AUTO_LABEL = DirType.AUTO.toDisplay()

    // C O L L E C T

    /**
     * Get list of directories
     *
     * @param context context
     * @return list of storage directories
     */
    private fun getDirectories(context: Context): List<Directory> {
        val result: MutableList<Directory> = ArrayList()

        // A P P - S P E C I F I C - P O S S I B L Y   A D O P T E D
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result.add(Directory(context.filesDir, DirType.AUTO))
        }

        // A P P - S P E C I F I C

        // application-specific secondary external storage or primary external (KITKAT)
        var dir: File
        val dirs = context.getExternalFilesDirs(null)
        if (dirs.isNotEmpty()) {

            // preferably secondary storage
            for (i in 1 until dirs.size) {
                dir = dirs[i]
                result.add(Directory(dir, DirType.APP_EXTERNAL_SECONDARY))
            }

            // primary storage
            dir = dirs[0]
            result.add(Directory(dir, DirType.APP_EXTERNAL_PRIMARY))
        }

        // P U B L I C

        // top-level public external storage directory

        // top-level public external secondary storage directory: not accessible to apps

        // I N T E R N A L

        // internal private storage
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            result.add(Directory(context.filesDir, DirType.APP_INTERNAL))
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
    fun getSortedStorageDirectories(context: Context): List<StorageDirectory> {
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
    fun getExternalStorages(context: Context): Map<StorageType, Array<File>> {

        // result set of paths
        val dirs: MutableMap<StorageType, Array<File>> = EnumMap(StorageType::class.java)

        // P R I M A R Y

        // primary emulated
        val primaryEmulated = discoverPrimaryEmulatedExternalStorage(context)
        if (primaryEmulated != null) {
            dirs[StorageType.PRIMARY_EMULATED] = arrayOf(primaryEmulated)
        }

        // primary emulated
        val physicalEmulated = discoverPrimaryPhysicalExternalStorage()
        if (physicalEmulated != null) {
            dirs[StorageType.PRIMARY_PHYSICAL] = arrayOf(physicalEmulated)
        }

        // S E C O N D A R Y

        val secondaryStorages = discoverSecondaryExternalStorage()
        if (!secondaryStorages.isNullOrEmpty()) {
            dirs[StorageType.SECONDARY] = secondaryStorages
        }
        return dirs
    }

    /**
     * Select external storage
     *
     * @param context context
     * @return external storage directory
     */
    fun selectExternalStorage(context: Context): String? {

        // S E C O N D A R Y

        // all secondary sdcards split into array
        val secondaries = discoverSecondaryExternalStorage()
        if (!secondaries.isNullOrEmpty()) {
            return secondaries[0].absolutePath
        }

        // P R I M A R Y

        // primary emulated sdcard
        val primaryEmulated = discoverPrimaryEmulatedExternalStorage(context)
        if (primaryEmulated != null) {
            return primaryEmulated.absolutePath
        }
        val primaryPhysical = discoverPrimaryPhysicalExternalStorage()
        return primaryPhysical?.absolutePath
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
            val userId = getUserId(context)

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
            val dirs: MutableList<File> = ArrayList()
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

    // Q U A L I F I E S

    /**
     * Whether the dir qualifies as sqlunet storage
     *
     * @param dir directory
     * @return error code if it qualifies
     */
    private fun qualifies(dir: File?, type: DirType): Int {
        var status = 0

        // dir
        if (dir == null) {
            status = status or StorageDirectory.NULL_DIR
            return status
        }

        // state
        when (type) {
            DirType.APP_EXTERNAL_SECONDARY, DirType.APP_EXTERNAL_PRIMARY, DirType.PUBLIC_EXTERNAL_PRIMARY, DirType.PUBLIC_EXTERNAL_SECONDARY -> try {
                val state = Environment.getExternalStorageState(dir)
                if (Environment.MEDIA_MOUNTED != state) {
                    Log.d(TAG, "storage state of $dir: $state")
                    status = StorageDirectory.NOT_MOUNTED
                }
            } catch (_: Throwable) {
            }

            DirType.APP_INTERNAL, DirType.AUTO -> {}
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

    // C A P A C I T Y

    /**
     * Index of free storage value
     */
    const val STORAGE_FREE = 0

    /**
     * Index of capacity value
     */
    const val STORAGE_CAPACITY = 1

    /**
     * Index of occupancy value
     */
    const val STORAGE_OCCUPANCY = 2

    /**
     * Storage data at path
     *
     * @param path path
     * @return data
     */
    fun storageStats(path: String): FloatArray {
        val stats = FloatArray(3)
        stats[STORAGE_FREE] = storageFree(path)
        stats[STORAGE_CAPACITY] = storageCapacity(path)
        stats[STORAGE_OCCUPANCY] = if (stats[STORAGE_CAPACITY] == 0f) 0f else 100f * ((stats[STORAGE_CAPACITY] - stats[STORAGE_FREE]) / stats[STORAGE_CAPACITY])
        return stats
    }

    fun getFree(context: Context, target: String): String {
        val file = File(target)
        val dir = if (file.isDirectory) file.absolutePath else file.parent!!
        val dataStats = storageStats(dir)
        val df = dataStats[STORAGE_FREE]
        val dc = dataStats[STORAGE_CAPACITY]
        val dp = dataStats[STORAGE_OCCUPANCY]
        return context.getString(R.string.format_storage_data, dir, mbToString(df), mbToString(dc), dp)
    }

    /**
     * Free storage at path
     *
     * @param path path
     * @return free storage in megabytes
     */
    private fun storageFree(path: String): Float {
        return try {
            val stat = StatFs(path)
            val bytes = (stat.blockCountLong * stat.blockSizeLong).toFloat()
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
    fun storageCapacity(path: String?): Float {
        return try {
            val stat = StatFs(path)
            val bytes = (stat.blockCountLong * stat.blockSizeLong).toFloat()
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

    private val UNITS = arrayOf("B", "KB", "MB", "GB")

    /**
     * Byte count to string
     *
     * @param count byte count
     * @return string
     */
    fun countToStorageString(count: Long): String {
        if (count > 0) {
            var unit = 1024f * 1024f * 1024f
            for (i in 3 downTo 0) {
                if (count >= unit) {
                    return String.format(Locale.ENGLISH, "%.1f %s", count / unit, UNITS[i])
                }
                unit /= 1024f
            }
        } else if (count == 0L) {
            return "0 Byte"
        }
        return "[n/a]"
    }

    /**
     * Storage types
     */
    enum class StorageType {

        PRIMARY_EMULATED,
        PRIMARY_PHYSICAL,
        SECONDARY
    }

    /**
     * Directory type
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    enum class DirType {

        AUTO,
        APP_EXTERNAL_SECONDARY,
        APP_EXTERNAL_PRIMARY,
        PUBLIC_EXTERNAL_SECONDARY,
        PUBLIC_EXTERNAL_PRIMARY,
        APP_INTERNAL;

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

        fun toShortDisplay(): String {
            return if (this == AUTO) {
                "auto"
            } else toDisplay()
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
    class Directory(val file: File, val type: DirType) {

        val fSValue: CharSequence
            get() = file.absolutePath
        val value: CharSequence
            get() = if (DirType.AUTO == type) {
                DirType.AUTO.toString()
            } else file.absolutePath
        val taggedValue: CharSequence
            get() = if (DirType.AUTO == type) {
                DirType.AUTO.toString() + ':' + file.absolutePath
            } else file.absolutePath
    }

    /**
     * Storage directory
     *
     * @param dir       directory
     * @param free      free megabytes
     * @param occupancy occupancy percentage
     * @param status    status
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

        override fun toString(): String {
            return String.format(Locale.ENGLISH, "%s %s %s %.1f%% %s", dir.type.toDisplay(), dir.file.absolutePath, mbToString(free), occupancy, status())
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
         * Hash code
         *
         * @return hashcode
         */
        override fun hashCode(): Int {
            return dir.hashCode()
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
         * @param appContext application context
         * @return true if database fits in storage
         */
        fun fitsIn(appContext: Context): Boolean {
            if (java.lang.Float.isNaN(DATABASE_SIZE_MB)) {
                val size = appContext.resources.getInteger(R.integer.size_db_working_total).toFloat()
                DATABASE_SIZE_MB = (size + size / 10f) / (1024f * 1024f)
            }
            return !java.lang.Float.isNaN(free) && free >= DATABASE_SIZE_MB
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
}
