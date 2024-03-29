/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.sqlunet.browser.MenuHandler.menuDispatch
import org.sqlunet.browser.Sender.send
import org.sqlunet.browser.common.R
import org.sqlunet.provider.BaseProvider
import org.sqlunet.sql.SqlFormatter.styledFormat
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

/**
 * Sql fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SqlFragment : Fragment() {

    private var exportLauncher: ActivityResultLauncher<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // launchers
        exportLauncher = registerExportLauncher()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sql, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // swipe refresh
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener {
            if (!isAdded) {
                return@setOnRefreshListener
            }
            val fragment = getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG)
            if (fragment is SqlStatementsFragment) {
                fragment.update()
            }
            // stop the refreshing indicator
            swipeRefreshLayout.isRefreshing = false
        }

        // sub fragment
        if (savedInstanceState == null) {
            // splash fragment
            val fragment: Fragment = SqlStatementsFragment()
            getChildFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container_sql_statements, fragment, FRAGMENT_TAG)
                // .addToBackStack(FRAGMENT_TAG) 
                .commit()
        }

        // menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // inflate
                menu.clear()
                menuInflater.inflate(R.menu.main, menu)
                menuInflater.inflate(R.menu.sql, menu)
                // MenuCompat.setGroupDividerEnabled(menu, true)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val itemId = menuItem.itemId
                return when (itemId) {
                    R.id.action_copy -> {
                        val sqls = stylizedSqls()
                        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("SQL", sqls)
                        clipboard.setPrimaryClip(clip)
                        true
                    }

                    R.id.action_sql_export -> {
                        export()
                        true
                    }

                    R.id.action_sql_send -> {
                        send(requireContext())
                        true
                    }

                    R.id.action_sql_clear -> {
                        BaseProvider.sqlBuffer.clear()
                        true
                    }

                    else -> {
                        menuDispatch((requireActivity() as AppCompatActivity), menuItem)
                    }
                }
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED)
    }

    override fun onDestroy() {
        super.onDestroy()
        exportLauncher!!.unregister()
    }

    /**
     * Export history
     */
    private fun registerExportLauncher(): ActivityResultLauncher<String> {

        val createContract = object : ActivityResultContracts.CreateDocument(MIME_TYPE) {

            override fun createIntent(context: Context, input: String): Intent {
                val intent: Intent = super.createIntent(context, input)
                intent.putExtra(Intent.EXTRA_TITLE, SQL_FILE)
                //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
                return intent
            }
        }
        return registerForActivityResult(createContract) { uri: Uri? ->
            // The result data contains a URI for the document or directory that the user selected.
            if (uri != null) {
                doExportSql(requireContext(), uri)
            }
        }
    }

    /**
     * Export history
     */
    fun export() {
        exportLauncher!!.launch(MIME_TYPE)
    }

    companion object {

        const val TAG = "SqlF"
        const val FRAGMENT_TAG = "sql"

        // S E N D

        fun send(context: Context) {
            val sqls = stylizedSqls()
            send(context, "Semantikos SQL", sqls)
        }

        // I M P O R T / E X P O R T

        /**
         * Export/import text file
         */
        private const val SQL_FILE = "semantikos.sql"

        /**
         * Mimetype
         */
        private const val MIME_TYPE = "text/plain"

        /**
         * Export Sql
         */
        private fun doExportSql(context: Context, uri: Uri) {
            Log.d(TAG, "Exporting to $uri")
            try {
                context.contentResolver.openFileDescriptor(uri, "w").use { pfd ->
                    FileOutputStream(pfd!!.fileDescriptor).use { fileOutputStream ->
                        OutputStreamWriter(fileOutputStream).use { writer ->
                            BufferedWriter(writer).use { bw ->
                                val sqls = textSqls()
                                bw.write(sqls)
                                Log.i(TAG, "Exported to $uri")
                                Toast.makeText(context, context.resources.getText(R.string.title_history_export).toString() + " " + uri, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "While writing", e)
                Toast.makeText(context, context.resources.getText(R.string.error_export).toString() + " " + uri, Toast.LENGTH_SHORT).show()
            }
        }

        // F A C T O R Y

        private fun textSqls(): String {
            val sb = StringBuilder()
            val sqls = BaseProvider.sqlBuffer.reverseItems()
            for (sql in sqls) {
                sb.append(sql)
                sb.append(";\n\n")
            }
            return sb.toString()
        }

        private fun stylizedSqls(): CharSequence {
            val sb = SpannableStringBuilder()
            val sqls = BaseProvider.sqlBuffer.reverseItems()
            for (sql in sqls) {
                sb.append(styledFormat(sql!!))
                sb.append(";\n\n")
            }
            return sb
        }
    }
}
