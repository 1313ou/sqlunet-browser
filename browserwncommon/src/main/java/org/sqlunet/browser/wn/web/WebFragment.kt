/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn.web

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.Pointer
import org.sqlunet.bnc.sql.BncImplementation
import org.sqlunet.browser.getParcelable
import org.sqlunet.browser.web.DocumentStringLoader
import org.sqlunet.browser.web.WebModel
import org.sqlunet.browser.wn.DocumentTransformer
import org.sqlunet.browser.wn.lib.BuildConfig
import org.sqlunet.browser.wn.lib.R
import org.sqlunet.dom.DomFactory.makeDocument
import org.sqlunet.dom.DomTransformer.docToXml
import org.sqlunet.dom.DomValidator.validateDocs
import org.sqlunet.dom.DomValidator.validateStrings
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.LogUtils
import org.sqlunet.settings.LogUtils.writeLog
import org.sqlunet.settings.Settings
import org.sqlunet.settings.StorageSettings
import org.sqlunet.sql.DataSource
import org.sqlunet.sql.NodeFactory.makeRootNode
import org.sqlunet.wordnet.SensePointer
import org.sqlunet.wordnet.SynsetPointer
import org.sqlunet.wordnet.WordPointer
import org.sqlunet.wordnet.sql.WordNetImplementation
import org.sqlunet.wordnet.sql.WordNetImplementation.Companion.init
import org.w3c.dom.Document
import java.net.URLDecoder

/**
 * A fragment representing a SqlUNet web view.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class WebFragment : Fragment() {

    internal inner class WebDocumentStringLoader(val context: Context, private val type: Int, private val data: String?, private val pointer: Parcelable?, private val pos: Char?, private val sources: Int, private val xml: Boolean) : DocumentStringLoader {

        override fun getDoc(): String? {
            try {
                DataSource(StorageSettings.getDatabasePath(context)).use {

                    // data source
                    val db = it.connection
                    init(db)

                    // dom documents
                    var wnDomDoc: Document? = null
                    var bncDomDoc: Document? = null

                    // selector mode
                    val isSelector = data != null

                    // make documents
                    if (isSelector) {
                        // this is a selector query
                        if (org.sqlunet.browser.wn.WnSettings.Source.WORDNET.test(sources)) {
                            wnDomDoc = WordNetImplementation().querySelectorDoc(db, data)
                        }
                    } else {
                        // this is a detail query
                        when (type) {
                            ProviderArgs.ARG_QUERYTYPE_ALL -> if (pointer != null) {
                                val sense2Pointer = pointer as SensePointer?
                                val wordId = sense2Pointer!!.getWordId()
                                val synsetId = sense2Pointer.getSynsetId()
                                if (org.sqlunet.browser.wn.WnSettings.Source.WORDNET.test(sources)) {
                                    wnDomDoc = WordNetImplementation().queryDoc(db, wordId, synsetId, withRelations = true, recurse = false)
                                }
                                if (org.sqlunet.browser.wn.WnSettings.Source.BNC.test(sources)) {
                                    bncDomDoc = BncImplementation().queryDoc(db, wordId, pos)
                                }
                            }

                            ProviderArgs.ARG_QUERYTYPE_WORD -> {
                                val wordPointer = pointer as WordPointer?
                                Log.d(TAG, "word=$wordPointer")
                                if (wordPointer != null && org.sqlunet.browser.wn.WnSettings.Source.WORDNET.test(sources)) {
                                    wnDomDoc = WordNetImplementation().queryWordDoc(db, wordPointer.getWordId())
                                }
                            }

                            ProviderArgs.ARG_QUERYTYPE_SYNSET -> {
                                val synsetPointer = pointer as SynsetPointer?
                                Log.d(TAG, "synset=$synsetPointer")
                                if (synsetPointer != null && org.sqlunet.browser.wn.WnSettings.Source.WORDNET.test(sources)) {
                                    wnDomDoc = WordNetImplementation().querySynsetDoc(db, synsetPointer.getSynsetId())
                                }
                            }
                        }
                    }

                    // stringify
                    return docsToString(data, xml, isSelector, wnDomDoc, bncDomDoc)
                }
            } catch (e: Exception) {
                Log.e(TAG, "getDoc", e)
            }
            return null
        }
    }

    /**
     * WebView
     */
    private var webview: WebView? = null

    /**
     * View model
     */
    private var model: WebModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return try {
            inflater.inflate(R.layout.fragment_web, container, false)
        } catch (_: InflateException) {
            Toast.makeText(requireContext(), "No WebView support", Toast.LENGTH_LONG).show()
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // webview
        webview = view.findViewById(R.id.webView)

        // models
        makeModels()

        // load data
        load()
    }

    /**
     * Make view models
     */
    private fun makeModels() {
        val xml: Boolean = Settings.getXmlPref(requireContext())
        model = ViewModelProvider(this)["wn:web(doc)", WebModel::class.java]
        model!!.getData().observe(getViewLifecycleOwner()) { doc: String? ->
            Log.d(TAG, "onLoadFinished")
            val mimeType = if (xml) "text/xml" else "text/html"
            val baseUrl = "file:///android_asset/"
            if (webview != null && doc != null)
                webview!!.loadDataWithBaseURL(baseUrl, doc, mimeType, "utf-8", null)
        }
    }

    /**
     * Load web view with data
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun load() {
        // settings
        val webSettings = webview!!.settings

        // enable javascript
        webSettings.javaScriptEnabled = true

        // zoom support
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true

        // client
        val webClient: WebViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, urlString: String): Boolean {
                val uri = Uri.parse(urlString)
                return handleUrl(uri)
            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                return handleUrl(uri)
            }

            private fun handleUrl(uri: Uri): Boolean {
                Log.d(TAG, "Uri $uri")
                try {
                    val query = URLDecoder.decode(uri.query, "UTF-8")
                    val target = query.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val name = target[0]
                    val value = target[1]
                    Log.d(TAG, "Query: $query name=$name value=$value")
                    val targetIntent = Intent(requireContext(), WebActivity::class.java)
                    if ("word" == name) {
                        targetIntent.putExtra(ProviderArgs.ARG_QUERYSTRING, value)
                    } else {
                        val id = value.toLong()

                        // warn with id
                        val activity: Activity? = activity
                        if (activity != null && !isDetached && !activity.isFinishing && !activity.isDestroyed) {
                            activity.runOnUiThread { Toast.makeText(activity, "id=$id", Toast.LENGTH_SHORT).show() }
                        }

                        // prepare data
                        val type: Int
                        val pointer: Pointer
                        when (name) {
                            "wordid" -> {
                                type = ProviderArgs.ARG_QUERYTYPE_WORD
                                pointer = WordPointer(id)
                            }

                            "synsetid" -> {
                                type = ProviderArgs.ARG_QUERYTYPE_SYNSET
                                pointer = SynsetPointer(id)
                            }

                            else -> {
                                Log.e(TAG, "Ill-formed Uri: $uri")
                                return false
                            }
                        }

                        // parameters
                        val recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(requireContext())
                        val parameters = org.sqlunet.wordnet.settings.Settings.makeParametersPref(requireContext())
                        targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, type)
                        targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
                        targetIntent.putExtra(ProviderArgs.ARG_QUERYRECURSE, recurse)
                        targetIntent.putExtra(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
                    }
                    targetIntent.action = ProviderArgs.ACTION_QUERY
                    startActivity(targetIntent)
                    return true
                } catch (e: Exception) {
                    Log.e(TAG, "Error while loading Uri: $uri", e)
                }
                return false
            }
        }
        webview!!.webViewClient = webClient

        // settings sources
        val context = requireContext()
        var mask = 0
        if (org.sqlunet.browser.wn.WnSettings.getWordNetPref(context)) {
            mask = org.sqlunet.browser.wn.WnSettings.Source.WORDNET.set(mask)
        }
        if (org.sqlunet.browser.wn.WnSettings.getBncPref(context)) {
            mask = mask or org.sqlunet.browser.wn.WnSettings.Source.BNC.set(mask)
        }
        val sources = mask

        // settings output
        val xml: Boolean = Settings.getXmlPref(context)

        // unmarshal arguments
        val args = requireArguments()

        // type
        val type = args.getInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_ALL)

        // pointer
        val pointer = getParcelable(args, ProviderArgs.ARG_QUERYPOINTER)
        Log.d(TAG, "query=$pointer")

        // hint
        val posString = args.getString(ProviderArgs.ARG_HINTPOS)
        val pos = if (posString != null) posString[0] else null

        // text
        val data = args.getString(ProviderArgs.ARG_QUERYSTRING)
        Log.d(TAG, "data=$data")

        // load the contents
        model!!.loadData(WebDocumentStringLoader(requireContext(), type, data, pointer, pos, sources, xml))
    }

    /**
     * Assemble result
     *
     * @param word       query word
     * @param xml        assemble as xml (or document to be xslt-transformed if false)
     * @param isSelector is selector source
     * @param wnDomDoc   wordnet document
     * @param bncDomDoc  bnc document
     * @return string
     */
    private fun docsToString(
        @Suppress("UNUSED_PARAMETER") word: String?, // might appear in the document
        xml: Boolean,
        isSelector: Boolean,
        wnDomDoc: Document?,
        bncDomDoc: Document?,
    ): String {
        // LogUtils.writeLog(DomTransformer.docToXml(wnDomDoc), false, "wn_sqlunet.log")
        // LogUtils.writeLog(DomTransformer.docToXml(bncDomDoc), false, "bnc_sqlunet.log")
        val data: String
        if (xml) {
            // merge all into one
            val rootDomDoc = makeDocument()
            makeRootNode(rootDomDoc, rootDomDoc, "sqlunet", null, SQLUNET_NS)
            if (wnDomDoc != null) {
                rootDomDoc.documentElement.appendChild(rootDomDoc.importNode(wnDomDoc.firstChild, true))
            }
            if (bncDomDoc != null) {
                rootDomDoc.documentElement.appendChild(rootDomDoc.importNode(bncDomDoc.firstChild, true))
            }
            data = docToXml(rootDomDoc)
            if (BuildConfig.DEBUG) {
                writeLog(data, false, requireContext(), LogUtils.DOC_LOG)
                val xsd = DocumentTransformer::class.java.getResource("/org/sqlunet/SqlUNet.xsd")!!
                validateStrings(xsd, data)
                // Log.d(TAG, "output=\n$data")
            }
        } else {
            val sb = StringBuilder()

            // header
            sb.append(BODY1)

            // css style sheet
            sb.append(STYLESHEET1).append("css/style.css").append(STYLESHEET2)
            sb.append(STYLESHEET1).append("css/tree.css").append(STYLESHEET2)
            sb.append(STYLESHEET1).append("css/wordnet.css").append(STYLESHEET2)
            if (bncDomDoc != null) {
                sb.append(STYLESHEET1).append("css/bnc.css").append(STYLESHEET2)
            }

            // javascripts
            sb.append(SCRIPT1).append("js/tree.js").append(SCRIPT2)
            sb.append(SCRIPT1).append("js/sarissa.js").append(SCRIPT2)
            sb.append(SCRIPT1).append("js/ajax.js").append(SCRIPT2)
            sb.append(SCRIPT1).append("js/wordnet.js").append(SCRIPT2)
            if (bncDomDoc != null) {
                sb.append(SCRIPT1).append("js/bnc.js").append(SCRIPT2)
            }

            // body
            sb.append(BODY2)

            // top
            sb.append(TOP)

            // xslt-transformed data
            sb.append(LIST1)
            if (wnDomDoc != null) {
                sb.append(ITEM1)
                sb.append(DocumentTransformer().docToHtml(wnDomDoc, org.sqlunet.browser.wn.WnSettings.Source.WORDNET.toString(), isSelector))
                sb.append(ITEM2)
            }
            if (bncDomDoc != null) {
                sb.append(ITEM1)
                sb.append(DocumentTransformer().docToHtml(bncDomDoc, org.sqlunet.browser.wn.WnSettings.Source.BNC.toString(), isSelector))
                sb.append(ITEM2)
            }
            sb.append(LIST2)

            // tail
            sb.append(BODY3)
            data = sb.toString()
            if (BuildConfig.DEBUG) {
                val xsd = DocumentTransformer::class.java.getResource("/org/sqlunet/SqlUNet.xsd")!!
                validateDocs(xsd, wnDomDoc, bncDomDoc)
                val context = getContext()
                if (context != null) {
                    writeLog(false, context, null, wnDomDoc, bncDomDoc)
                    writeLog(data, false, context, LogUtils.DOC_LOG)
                }
                // Log.d(TAG, "output=\n$data")
            }
        }
        return data
    }

    companion object {

        private const val TAG = "WebF"
        const val FRAGMENT_TAG = "web"
        private const val SQLUNET_NS = "http://org.sqlunet"

        /**
         * HTML stuff
         */
        private const val BODY1 = "<HTML><HEAD>"
        private const val BODY2 = "</HEAD><BODY>"
        private const val BODY3 = "</BODY></HTML>"
        private const val TOP = "<DIV class='titlesection'><IMG class='titleimg' src='images/logo.png'/></DIV>"
        private const val STYLESHEET1 = "<LINK rel='stylesheet' type='text/css' href='"
        private const val STYLESHEET2 = "' />"
        private const val SCRIPT1 = "<SCRIPT type='text/javascript' src='"
        private const val SCRIPT2 = "'></SCRIPT>"
        private const val LIST1 = "<UL style='display: block;'>"
        private const val LIST2 = "</UL>"
        private const val ITEM1 = "<LI class='treeitem treepanel'>"
        private const val ITEM2 = "</LI>"
    }
}
