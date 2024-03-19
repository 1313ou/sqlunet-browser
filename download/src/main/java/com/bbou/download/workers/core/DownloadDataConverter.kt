package com.bbou.download.workers.core

import androidx.work.Data
import com.bbou.download.DownloadData

/**
 * Source URL
 */
const val FROM = "from_url"

/**
 * Destination file
 */
const val TO = "to_file"

/**
 * Size key
 */
const val SIZE = "download_size"

/**
 * Etag key
 */
const val ETAG = "download_etag"

/**
 * Date key
 */
const val DATE = "download_date"

/**
 * Version key
 */
const val VERSION = "download_version"

/**
 * Static version key
 */
const val STATIC_VERSION = "download_static_version"

/**
 * Convert DownloadData to Data
 *
 * @return Data
 */
fun DownloadData.toData(): Data.Builder {
    return Data.Builder()
        .putString(FROM, fromUrl)
        .putString(TO, toFile)
        .putLong(SIZE, size ?: -1)
        .putLong(DATE, date ?: -1)
        .putString(ETAG, etag)
        .putString(VERSION, version)
        .putString(STATIC_VERSION, staticVersion)
}

/**
 * Convert Data to DownloadData
 *
 * @return DownloadData
 */
fun Data.toDownloadData(): DownloadData {
    return DownloadData(
        getString(FROM),
        getString(TO),
        if (getLong(DATE, -1) == -1L) null else getLong(DATE, -1),
        if (getLong(SIZE, -1) == -1L) null else getLong(SIZE, -1),
        getString(ETAG),
        getString(VERSION),
        getString(STATIC_VERSION)
    )
}