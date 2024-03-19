package com.bbou.download.workers

import com.bbou.download.preference.Settings

/**
 * Fragment factory from downloader
 *
 * @param downloader downloader
 * @return fragment
 */
fun toFragment(downloader: Settings.Downloader): DownloadBaseFragment {
    return when (downloader) {
        Settings.Downloader.DOWNLOAD -> DownloadFragment()
        Settings.Downloader.DOWNLOAD_ZIP -> DownloadZipFragment()
    }
}
