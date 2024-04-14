package com.bbou.download

/**
 * Download data
 */
data class DownloadData(

    /**
     * Source url
     */
    val fromUrl: String?,

    /**
     * Local downloaded file
     */
    val toFile: String?,

    /**
     * Data stamp
     */
    val date: Long? = null,

    /**
     * Size
     */
    val size: Long? = null,

    /**
     * Etag
     */
    val etag: String? = null,

    /**
     * Version
     */
    val version: String? = null,

    /**
     * Static version
     */
    val staticVersion: String? = null,
)