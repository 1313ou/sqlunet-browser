/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.database.Cursor

interface CursorRecyclerViewAdapter {
    fun changeCursor(cursor: Cursor?)
    fun getCursor(): Cursor?
}
