/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.wn

import org.sqlunet.browser.wn.common.CommonApplication

class Application : CommonApplication() {

    /**
     * Drop data
     *
     * @return true if flagged in build config
     */
    override fun dropData(): Boolean {
        @Suppress("KotlinConstantConditions")
        return BuildConfig.DROP_DATA
    }
}
