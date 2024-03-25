/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.style

import org.sqlunet.style.Preprocessor

/**
 * FrameNet frame preprocessor
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object FrameNetFrameProcessor : Preprocessor() {

    /**
     * Replacers for preprocessor
     */
    private val replacers = arrayOf( 
        "<fex name=[\"']([^\"']+)[\"']>([^<]*)</fex>", "<fex>$2</fex> <xfen>[$1]</xfen>"
    )
}
