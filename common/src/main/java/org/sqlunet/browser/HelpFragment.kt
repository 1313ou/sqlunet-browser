/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.sqlunet.browser.common.R

/**
 * Help fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class HelpFragment : Fragment() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return try {
            inflater.inflate(R.layout.fragment_help, container, false)
        } catch (e: InflateException) {
            Toast.makeText(requireContext(), "No WebView support", Toast.LENGTH_LONG).show()
            null
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (getView() != null) {
            // web view
            val webView = view.findViewById<WebView>(R.id.webView)
            webView.clearCache(true)
            webView.clearHistory()
            webView.getSettings().javaScriptEnabled = true
            webView.getSettings().javaScriptCanOpenWindowsAutomatically = true
            webView.setWebViewClient(object : WebViewClient() {

                @Deprecated("Deprecated in Java")
                override fun onReceivedError(webView: WebView, errorCode: Int, description: String, failingUrl: String) {
                    super.onReceivedError(webView, errorCode, description, failingUrl)
                    Log.e(TAG, "$failingUrl:$description,$errorCode")
                }

                @TargetApi(Build.VERSION_CODES.N)
                override fun onReceivedError(webView: WebView, request: WebResourceRequest, error: WebResourceError) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        super.onReceivedError(webView, request, error)
                    }
                    Log.e(TAG, error.toString())
                }

                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                    if (url.endsWith("pdf") || url.endsWith("PDF")) {
                        val uri = Uri.parse(url)
                        if (handleUri(uri, "application/pdf")) {
                            return true
                        }
                    }
                    webView.loadUrl(url)
                    return false
                }

                @TargetApi(Build.VERSION_CODES.N)
                override fun shouldOverrideUrlLoading(webView: WebView, request: WebResourceRequest): Boolean {
                    val uri = request.url
                    val fileName = uri.lastPathSegment
                    if (fileName != null && (fileName.endsWith("pdf") || fileName.endsWith("PDF"))) {
                        if (handleUri(uri, "application/pdf")) {
                            return true
                        }
                    }
                    webView.loadUrl(uri.toString())
                    return false
                }
            })
            val lang = getString(R.string.lang_tag)
            var url = "file:///android_asset/help/"
            if (lang.isNotEmpty()) {
                url += "$lang-"
            }
            url += "index.html"
            webView.loadUrl(url)
        }
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as AppCompatActivity
        val actionBar = activity.supportActionBar!!
        actionBar.customView = null
        actionBar.setBackgroundDrawable(null)
    }

    private fun handleUri(uri: Uri, mime: String): Boolean {
        try {
            val intentUrl = Intent(Intent.ACTION_VIEW)
            intentUrl.setDataAndType(uri, mime)
            intentUrl.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentUrl)
            return true
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.status_viewer_failed, Toast.LENGTH_LONG).show()
        }
        return false
    }

    companion object {

        /**
         * Log tag
         */
        private const val TAG = "HelpF"
    }
}