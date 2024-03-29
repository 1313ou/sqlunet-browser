package com.bbou.download

/**
 * Download data
 */
data class DownloadData(

    /**
     * Source url
     */
    @JvmField val fromUrl: String?,

    /**
     * Local downloaded file
     */
    @JvmField val toFile: String?,

    /**
     * Data stamp
     */
    @JvmField val date: Long? = null,

    /**
     * Size
     */
    @JvmField val size: Long? = null,

    /**
     * Etag
     */
    @JvmField val etag: String? = null,

    /**
     * Version
     */
    @JvmField val version: String? = null,

    /**
     * Static version
     */
    @JvmField val staticVersion: String? = null,
)