/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.fn.web

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
import org.sqlunet.browser.fn.BuildConfig
import org.sqlunet.browser.fn.DocumentTransformer
import org.sqlunet.browser.fn.R
import org.sqlunet.browser.getParcelable
import org.sqlunet.browser.web.DocumentStringLoader
import org.sqlunet.browser.web.WebModel
import org.sqlunet.dom.DomFactory.makeDocument
import org.sqlunet.dom.DomTransformer.docToXml
import org.sqlunet.dom.DomValidator.validateDocs
import org.sqlunet.dom.DomValidator.validateStrings
import org.sqlunet.framenet.FnAnnoSetPointer
import org.sqlunet.framenet.FnFramePointer
import org.sqlunet.framenet.FnLexUnitPointer
import org.sqlunet.framenet.FnSentencePointer
import org.sqlunet.framenet.sql.FrameNetImplementation
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.LogUtils
import org.sqlunet.settings.LogUtils.writeLog
import org.sqlunet.settings.Settings
import org.sqlunet.settings.StorageSettings
import org.sqlunet.sql.DataSource
import org.sqlunet.sql.NodeFactory.makeRootNode
import org.w3c.dom.Document
import java.net.URLDecoder

/**
 * A fragment representing a SqlUNet web view.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class WebFragment : Fragment() {

    private inner class WebDocumentStringLoader(val context: Context, val pointer: Parcelable?, val pos: Char?, val type: Int, val data: String?, val sources: Int, val xml: Boolean) : DocumentStringLoader {

        override fun getDoc(): String? {
            try {
                DataSource(StorageSettings.getDatabasePath(context)).use {

                    // data source
                    val db = it.connection

                    // dom documents
                    var fnDomDoc: Document? = null

                    // selector mode
                    val isSelector = data != null

                    // make documents
                    if (isSelector) {
                        // this is a selector query
                        if (org.sqlunet.browser.fn.FnSettings.Source.FRAMENET.test(sources)) {
                            fnDomDoc = FrameNetImplementation(true).querySelectorDoc(db, data!!, pos)
                        }
                    } else {
                        // this is a detail query
                        when (type) {
                            ProviderArgs.ARG_QUERYTYPE_ALL -> if (pointer != null) {
                                if (pointer is Pointer) {
                                    val xPointer = pointer as Pointer?
                                    val id = xPointer!!.id
                                    val isFrame = xPointer is FnFramePointer
                                    fnDomDoc = if (isFrame) FrameNetImplementation(true).queryFrameDoc(db, id, pos) else FrameNetImplementation(true).queryLexUnitDoc(db, id)
                                }
                            }

                            ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT -> {
                                val lexunitPointer = pointer as FnLexUnitPointer?
                                Log.d(TAG, "ArgPosition: fnlexunit=$lexunitPointer")
                                if (lexunitPointer != null && org.sqlunet.browser.fn.FnSettings.Source.FRAMENET.test(sources)) {
                                    fnDomDoc = FrameNetImplementation(true).queryLexUnitDoc(db, lexunitPointer.id)
                                }
                            }

                            ProviderArgs.ARG_QUERYTYPE_FNFRAME -> {
                                val framePointer = pointer as FnFramePointer?
                                Log.d(TAG, "ArgPosition: fnframe=$framePointer")
                                if (framePointer != null && org.sqlunet.browser.fn.FnSettings.Source.FRAMENET.test(sources)) {
                                    fnDomDoc = FrameNetImplementation(true).queryFrameDoc(db, framePointer.id, pos)
                                }
                            }

                            ProviderArgs.ARG_QUERYTYPE_FNSENTENCE -> {
                                val sentencePointer = pointer as FnSentencePointer?
                                Log.d(TAG, "ArgPosition: fnsentence=$sentencePointer")
                                if (sentencePointer != null && org.sqlunet.browser.fn.FnSettings.Source.FRAMENET.test(sources)) {
                                    fnDomDoc = FrameNetImplementation(true).querySentenceDoc(db, sentencePointer.id)
                                }
                            }

                            ProviderArgs.ARG_QUERYTYPE_FNANNOSET -> {
                                val annoSetPointer = pointer as FnAnnoSetPointer?
                                Log.d(TAG, "ArgPosition: fnannoset=$annoSetPointer")
                                if (annoSetPointer != null && org.sqlunet.browser.fn.FnSettings.Source.FRAMENET.test(sources)) {
                                    fnDomDoc = FrameNetImplementation(true).queryAnnoSetDoc(db, annoSetPointer.id)
                                }
                            }
                        }
                    }

                    // stringify
                    return docsToString(data, xml, isSelector, fnDomDoc)
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

    // View model
    private var model: WebModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return try {
            inflater.inflate(R.layout.fragment_web, container, false)
        } catch (e: InflateException) {
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
        model = ViewModelProvider(this)["fn:web(doc)", WebModel::class.java]
        model!!.getData().observe(getViewLifecycleOwner()) { doc: String? ->
            Log.d(TAG, "onLoadFinished")
            val mimeType = if (xml) "text/xml" else "text/html"
            val baseUrl = "file:///android_asset/"
            webview!!.loadDataWithBaseURL(baseUrl, doc!!, mimeType, "utf-8", null)
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
                            activity.runOnUiThread(Runnable { Toast.makeText(activity, "id=$id", Toast.LENGTH_SHORT).show() })
                        }

                        // prepare data
                        val type: Int
                        val pointer: Pointer
                        when (name) {
                            "fnframeid" -> {
                                type = ProviderArgs.ARG_QUERYTYPE_FNFRAME
                                pointer = FnFramePointer(id)
                            }

                            "fnluid" -> {
                                type = ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT
                                pointer = FnLexUnitPointer(id)
                            }

                            "fnsentenceid" -> {
                                type = ProviderArgs.ARG_QUERYTYPE_FNSENTENCE
                                pointer = FnSentencePointer(id)
                            }

                            "fnannosetid" -> {
                                type = ProviderArgs.ARG_QUERYTYPE_FNANNOSET
                                pointer = FnAnnoSetPointer(id)
                            }

                            else -> {
                                Log.e(TAG, "Ill-formed Uri: $uri")
                                return false
                            }
                        }
                        targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, type)
                        targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
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
        var mask = 0
        if (org.sqlunet.browser.fn.FnSettings.getFrameNetPref(requireContext())) {
            mask = mask or org.sqlunet.browser.fn.FnSettings.Source.FRAMENET.set(mask)
        }
        val sources = mask

        // settings output
        val xml: Boolean = Settings.getXmlPref(requireContext())

        // unmarshal arguments
        val args = requireArguments()

        // type
        val type = args.getInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_ALL)

        // pointer
        val pointer = getParcelable(args, ProviderArgs.ARG_QUERYPOINTER)
        Log.d(TAG, "ArgPosition: query=$pointer")

        // hint
        val posString = args.getString(ProviderArgs.ARG_HINTPOS)
        val pos = if (posString != null) posString[0] else null

        // text
        val data = args.getString(ProviderArgs.ARG_QUERYSTRING)
        Log.d(TAG, "ArgPosition: data=$data")

        // load the contents
        model!!.loadData(WebDocumentStringLoader(requireContext(), pointer, pos, type, data, sources, xml))
    }

    /**
     * Assemble result
     *
     * @param word       query word
     * @param xml        assemble as xml (or document to be xslt-transformed if false)
     * @param isSelector is selector source
     * @param fnDomDoc   framenet document
     * @return string
     */
    private fun docsToString(
        @Suppress("UNUSED_PARAMETER") word: String?, // might appear in the document
        xml: Boolean,
        isSelector: Boolean,
        fnDomDoc: Document?,
    ): String {
        // LogUtils.writeLog(DomTransformer.docToXml(wnDomDoc), false, "wn_sqlunet.log")
        // LogUtils.writeLog(DomTransformer.docToXml(vnDomDoc), false, "vn_sqlunet.log")
        // LogUtils.writeLog(DomTransformer.docToXml(pbDomDoc), false, "pb_sqlunet.log")
        // LogUtils.writeLog(DomTransformer.docToXml(fnDomDoc), false, "fn_sqlunet.log")
        // LogUtils.writeLog(DomTransformer.docToXml(bncDomDoc), false, "bnc_sqlunet.log")
        val data: String
        if (xml) {
            // merge all into one
            val rootDomDoc = makeDocument()
            makeRootNode(rootDomDoc, rootDomDoc, "sqlunet", null, SQLUNET_NS)
            if (fnDomDoc != null) {
                rootDomDoc.documentElement.appendChild(rootDomDoc.importNode(fnDomDoc.firstChild, true))
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
            if (fnDomDoc != null) {
                sb.append(STYLESHEET1).append("css/framenet.css").append(STYLESHEET2)
            }

            // javascripts
            sb.append(SCRIPT1).append("js/tree.js").append(SCRIPT2)
            sb.append(SCRIPT1).append("js/sarissa.js").append(SCRIPT2)
            sb.append(SCRIPT1).append("js/ajax.js").append(SCRIPT2)
            sb.append(SCRIPT1).append("js/wordnet.js").append(SCRIPT2)
            if (fnDomDoc != null) {
                sb.append(SCRIPT1).append("js/framenet.js").append(SCRIPT2)
            }

            // body
            sb.append(BODY2)

            // top
            sb.append(TOP)

            // xslt-transformed data
            sb.append(LIST1)
            if (fnDomDoc != null) {
                sb.append(ITEM1)
                sb.append(DocumentTransformer().docToHtml(fnDomDoc, org.sqlunet.browser.fn.FnSettings.Source.FRAMENET.toString(), isSelector))
                sb.append(ITEM2)
            }
            sb.append(LIST2)

            // tail
            sb.append(BODY3)
            data = sb.toString()
            if (BuildConfig.DEBUG) {
                val xsd = DocumentTransformer::class.java.getResource("/org/sqlunet/SqlUNet.xsd")!!
                validateDocs(xsd, fnDomDoc)
                writeLog(false, requireContext(), null, fnDomDoc)
                writeLog(data, false, requireContext(), LogUtils.DOC_LOG)
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
