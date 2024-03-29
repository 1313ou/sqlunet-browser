/*
 * Copyright (c) 2024. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

/**
 * Safe cast.
 * It's up to the author to ensure it's sage
 *
 * @param T source type o f what
 * @param U destination type
 * @param what object
 * @return what of type T as U
 */
@Suppress("UNCHECKED_CAST")
fun <T, U> safeCast(what: T): U {
    return what as U
}

// fun <U> examples(){
//     val x:Int = 0
//     val y = safeCast<Int,U>(x)
//     val z:U = safeCast(x)
//     val w:Number = safeCast(x)
// }

fun getParcelable(args: Bundle, key: String): Parcelable? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) args.getParcelable(key, Parcelable::class.java) else @Suppress("DEPRECATION") args.getParcelable(key)
}