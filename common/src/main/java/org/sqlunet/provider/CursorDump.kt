/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import android.database.Cursor
import android.util.Log

object CursorDump {

    /**
     * Dump any cursor (discover its schema)
     */
    fun dump(cursor: Cursor?) {
        if (cursor == null) {
            Log.i("dump", "null cursor")
            return
        }
        if (cursor.isClosed) {
            Log.i("dump", "closed cursor=$cursor")
            return
        }
        if (cursor.moveToFirst()) {
            do {
                val count = cursor.columnCount
                for (c in 0 until count) {
                    val name = cursor.getColumnName(c)
                    val value = cursor.getString(c)
                    Log.i("dump", "$name=$value")
                }
            } while (cursor.moveToNext())
            cursor.moveToFirst()
        }
    }

    /**
     * Dump X cursor (x selector query)
     */
    fun dumpXCursor(cursor: Cursor?) {
        if (cursor == null) {
            Log.i("dump", "null cursor")
            return
        }
        if (cursor.moveToFirst()) {
            val idWordId = cursor.getColumnIndex(XNetContract.Words_XNet_U.WORDID)
            val idSynsetId = cursor.getColumnIndex(XNetContract.Words_XNet_U.SYNSETID)
            val idXId = cursor.getColumnIndex(XNetContract.Words_XNet_U.XID)
            val idXName = cursor.getColumnIndex(XNetContract.Words_XNet_U.XNAME)
            val idXHeader = cursor.getColumnIndex(XNetContract.Words_XNet_U.XHEADER)
            val idXInfo = cursor.getColumnIndex(XNetContract.Words_XNet_U.XINFO)
            val idDefinition = cursor.getColumnIndex(XNetContract.Words_XNet_U.XDEFINITION)
            val idSources = cursor.getColumnIndex(XNetContract.Words_XNet_U.SOURCES)
            do {
                val wordId = cursor.getLong(idWordId)
                val synsetId = if (cursor.isNull(idSynsetId)) 0 else cursor.getLong(idSynsetId)
                val xId = if (cursor.isNull(idXId)) 0 else cursor.getLong(idXId)
                val xName = if (cursor.isNull(idXName)) null else cursor.getString(idXName)
                val xHeader = if (cursor.isNull(idXHeader)) null else cursor.getString(idXHeader)
                val xInfo = if (cursor.isNull(idXInfo)) null else cursor.getString(idXInfo)
                val definition = if (cursor.isNull(idXInfo)) null else cursor.getString(idDefinition)
                val sources = if (cursor.isNull(idSources)) "" else
                    cursor.getString(idSources)
                Log.i(
                    "xloader", "sources=" + sources +
                            " wordid=" + wordId +
                            " synsetid=" + synsetId +
                            " xid=" + xId +
                            " name=" + xName +
                            " header=" + xHeader +
                            " info=" + xInfo +
                            " definition=" + definition
                )
            } while (cursor.moveToNext())
            cursor.moveToFirst()
        }
    }
}
