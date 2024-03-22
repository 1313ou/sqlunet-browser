/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import org.sqlunet.settings.StorageSettings
import org.sqlunet.sql.Utils.replaceArgs
import org.sqlunet.sql.Utils.toArgs
import java.util.LinkedList
import java.util.Properties

/**
 * SqlUNet provider
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class BaseProvider : ContentProvider() {

    /**
     * Circular buffer
     */
    class CircularBuffer internal constructor(private val limit: Int) : LinkedList<CharSequence?>() {
        @Synchronized
        fun addItem(value: CharSequence?) {
            addLast(value)
            if (size > limit) {
                removeFirst()
            }
        }

        @Synchronized
        fun reverseItems(): Array<CharSequence?> {
            val n = size
            // Log.d(TAG, "sql size " + n);
            val array = arrayOfNulls<CharSequence>(n)
            val iter: MutableListIterator<CharSequence?> = listIterator()
            var i = n - 1
            while (iter.hasNext()) {
                val sql = iter.next()
                array[i] = sql
                i--
            }
            return array
        }

        companion object {
            const val PREF_SQL_BUFFER_CAPACITY = "pref_sql_buffer_capacity"
            const val PREF_SQL_LOG = "pref_sql_log"
        }
    }

    // D A T A B A S E

    // database
    @JvmField
    protected var db: SQLiteDatabase? = null

    // O P E N / S H U T D O W N

    @Throws(SQLiteCantOpenDatabaseException::class)
    protected fun openReadOnly() {
        val context = context!!
        val path = StorageSettings.getDatabasePath(context)
        try {
            db = openReadOnly(path, SQLiteDatabase.OPEN_READONLY)
            assert(db != null)
            Log.d(TAG, "Opened by " + this.javaClass + " content provider: " + db!!.path)
        } catch (e: SQLiteCantOpenDatabaseException) {
            Log.e(TAG, "Open failed by " + this.javaClass + " content provider: " + path, e)
            throw e
        }
    }

    @Throws(SQLiteCantOpenDatabaseException::class)
    protected fun openReadWrite() {
        val context = context!!
        val path = StorageSettings.getDatabasePath(context)
        try {
            db = openReadOnly(path, SQLiteDatabase.OPEN_READWRITE)
            assert(db != null)
            Log.d(TAG, "Opened by " + this.javaClass + " content provider: " + db!!.path)
        } catch (e: SQLiteCantOpenDatabaseException) {
            Log.e(TAG, "Open failed by " + this.javaClass + " content provider: " + path, e)
            throw e
        }
    }

    /**
     * Open database
     *
     * @param path  database path
     * @param flags database name
     * @return opened database
     */
    private fun openReadOnly(path: String, flags: Int): SQLiteDatabase? {
        db = SQLiteDatabase.openDatabase(path, null, flags)
        return db
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun shutdown() {
        Log.d(TAG, "Shutdown " + this.javaClass)
        // super.shutdown();
        close()
    }

    override fun refresh(uri: Uri, args: Bundle?, cancellationSignal: CancellationSignal?): Boolean {
        super.refresh(uri, args, cancellationSignal)
        Log.d(TAG, "Refresh " + this.javaClass)
        close()
        return true
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        Log.d(TAG, "Called '" + method + "' on " + this.javaClass)
        if (CALLED_REFRESH_METHOD == method) {
            close()
        }
        return null
    }

    /**
     * Close provider
     */
    private fun close() {
        if (db != null) {
            val path = db!!.path
            synchronized(this) {
                db!!.close()
                db = null
            }
            Log.d(TAG, "Close " + this.javaClass + " content provider: " + path)
        }
    }

    // W R I T E O P E R A T I O N S
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Read-only")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        throw UnsupportedOperationException("Read-only")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Read-only")
    }

    /**
     * Log query
     *
     * @param sql  sql
     * @param args parameters
     */
    protected fun logSql(sql: String, vararg args: String) {
        val sql2 = replaceArgs(sql, *toArgs(*args))
        sqlBuffer.addItem(sql2)
    }

    companion object {
        private const val TAG = "BaseProvider"

        const val VENDOR = "sqlunet"

        const val SCHEME = "content://"

        // C O N T E N T   P R O V I D E R   A U T H O R I T Y

        @JvmStatic
        protected fun makeAuthority(configKey: String): String {
            try {
                val `is` = BaseProvider::class.java.getResourceAsStream("/org/sqlunet/config.properties")
                val properties = Properties()
                properties.load(`is`)
                val authority = properties.getProperty(configKey)
                if (authority != null && authority.isNotEmpty()) {
                    return authority
                }
                throw RuntimeException("Null provider key=$configKey")
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        protected val authorityUris: Array<Uri?>
            get() = try {
                val `is` = BaseProvider::class.java.getResourceAsStream("/org/sqlunet/config.properties")
                val properties = Properties()
                properties.load(`is`)
                val authorityKeys = properties.stringPropertyNames()
                val authorities = arrayOfNulls<Uri>(authorityKeys.size)
                var i = 0
                for (authorityKey in authorityKeys) {
                    authorities[i++] = Uri.parse(SCHEME + properties.getProperty(authorityKey))
                }
                authorities
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        // S Q L   B U F F E R

        private const val DEFAULT_SQL_BUFFER_CAPACITY = 15

        /**
         * SQL statement buffer
         */
        @JvmField
        var sqlBuffer = CircularBuffer(DEFAULT_SQL_BUFFER_CAPACITY)

        /**
         * Record generated SQL
         */
        @JvmField
        var logSql = false

        /**
         * Refresh method name
         */
        const val CALLED_REFRESH_METHOD = "closeProvider"

        /**
         * Close provider
         *
         * @param context context
         * @param uri     provider uri
         */
        @JvmStatic
        fun closeProvider(context: Context, uri: Uri) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.contentResolver.refresh(uri, null, null)
            } else {
                context.contentResolver.call(uri, CALLED_REFRESH_METHOD, null, null)
            }
        }

        /**
         * Close all providers
         *
         * @param context context
         */
        @JvmStatic
        fun closeProviders(context: Context) {
            val uris = authorityUris
            for (uri in uris) {
                closeProvider(context, uri!!)
            }
        }

        // H E L P E R S

        /**
         * Append items to projection
         *
         * @param projection original projection
         * @param item       item to addItem to projection
         * @return augmented projection
         */
        @JvmStatic
        fun appendProjection(projection: Array<String>?, item: String): Array<String> {
            val projection2 = projection ?: arrayOf("*")
            val tail = arrayOf(item)
            return projection2 + tail
        }

        /**
         * Append items to projection
         *
         * @param projection original projection
         * @param items      item to addItem to projection
         * @return augmented projection
         */
        @JvmStatic
        fun appendProjection(projection: Array<String>?, items: Array<String>): Array<String> {
            val projection2 = projection ?: arrayOf("*")
            return projection2 + items
        }

        /**
         * Add items to projection
         *
         * @param projection original projection
         * @param item       item to append to projection
         * @return augmented projection
         */
        @JvmStatic
        fun prependProjection(projection: Array<String>?, item: String): Array<String> {

            val projection2 = projection ?: arrayOf("*")
            val head = arrayOf(item)
            return head + projection2
        }

        // /**
        //  * Convert args to string
        //  *
        //  * @param args args
        //  * @return string
        //  */
        // @JvmStatic
        // protected fun argsToString(vararg args: String): String {
        //     val sb = StringBuilder()
        //     if (args.isNotEmpty()) {
        //         for (s in args) {
        //             if (sb.isNotEmpty()) {
        //                 sb.append(", ")
        //             }
        //             sb.append(s)
        //         }
        //     }
        //     return sb.toString()
        // }

        /**
         * Convert args to string
         *
         * @param args args
         * @return string
         */
        @JvmStatic
        protected fun argsToString(args: Array<out String>?): String {
            val sb = StringBuilder()
            if (!args.isNullOrEmpty()) {
                for (s in args) {
                    if (sb.isNotEmpty()) {
                        sb.append(", ")
                    }
                    sb.append(s)
                }
            }
            return sb.toString()
        }

        /**
         * Resize sql buffer
         *
         * @param capacity capacity
         */
        @JvmStatic
        fun resizeSql(capacity: Int) {
            // Log.d(TAG, "Sql buffer capacity " + capacity);
            sqlBuffer = CircularBuffer(capacity)
        }
    }
}
