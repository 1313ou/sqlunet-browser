/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet

inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T =
    if (condition) apply(block) else this

inline fun <T> T.applyIf(condition: T.() -> Boolean, block: T.() -> Unit): T =
    if (condition()) apply(block) else this
