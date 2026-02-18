/*
 * Copyright (c) 2025. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.history

import android.annotation.SuppressLint
import android.content.Context
import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.common.R
import org.sqlunet.browser.history.History.makeSearchIntent
import org.sqlunet.browser.history.History.recordQuery
import org.sqlunet.browser.history.SearchRecentSuggestions.Companion.getAuthority
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class HistoryFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLaunchers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        adapter = HistoryAdapter(requireContext(), null)
        recyclerView.adapter = adapter

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val cursor = adapter.getCursor()
                if (cursor != null && cursor.moveToPosition(position)) {
                    val itemIdIdx = cursor.getColumnIndex("_id")
                    val itemId = cursor.getString(itemIdIdx)
                    val dataIdx = cursor.getColumnIndex(SearchRecentSuggestions.SuggestionColumns.DISPLAY1)
                    val data = cursor.getString(dataIdx)
                    val suggestions = SearchRecentSuggestions(AppContext.context, SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES)
                    suggestions.delete(itemId)
                    // Restart the loader to get the updated cursor
                    LoaderManager.getInstance(this@HistoryFragment).restartLoader(LOADER_ID, null, this@HistoryFragment)
                    Toast.makeText(requireContext(), resources.getString(R.string.title_history_deleted) + ' ' + data, Toast.LENGTH_SHORT).show()
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeCallback)
        touchHelper.attachToRecyclerView(recyclerView)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.history, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_history_export -> exportHistory()
                    R.id.action_history_import -> importHistory()
                    R.id.action_history_clear -> {
                        val suggestions = android.provider.SearchRecentSuggestions(AppContext.context, getAuthority(AppContext.context), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES)
                        suggestions.clearHistory()
                        return true
                    }
                }
                return false
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED)
    }

    override fun onStart() {
        super.onStart()
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::exportLauncher.isInitialized) {
            exportLauncher.unregister()
        }
        if (::importLauncher.isInitialized) {
            importLauncher.unregister()
        }
    }

    override fun onCreateLoader(loaderID: Int, args: Bundle?): Loader<Cursor> {
        val suggestions = SearchRecentSuggestions(AppContext.context, SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES)
        return suggestions.cursorLoader()
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        adapter.changeCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.changeCursor(null)
    }

    class HistoryAdapter(private val context: Context, private var cursor: Cursor?) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (cursor?.moveToPosition(position) == true) {
                val dataIdx = cursor!!.getColumnIndex(SearchRecentSuggestions.SuggestionColumns.DISPLAY1)
                val query = cursor!!.getString(dataIdx)
                holder.textView.text = query
                holder.itemView.setOnClickListener {
                    val intent = makeSearchIntent(context, query)
                    context.startActivity(intent)
                }
            }
        }

        override fun getItemCount(): Int {
            return cursor?.count ?: 0
        }

        @SuppressLint("NotifyDataSetChanged")
        fun changeCursor(newCursor: Cursor?) {
            if (cursor === newCursor) {
                return
            }
            cursor = newCursor
            notifyDataSetChanged()
        }

        fun getCursor(): Cursor? {
            return cursor
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(android.R.id.text1)
        }
    }

    // E X P O R T

    private fun exportHistory() {
        exportLauncher.launch(MIME_TYPE)
    }

    private fun importHistory() {
        importLauncher.launch(arrayOf(MIME_TYPE))
    }

    private lateinit var exportLauncher: ActivityResultLauncher<String>

    private lateinit var importLauncher: ActivityResultLauncher<Array<String>>

    private fun registerLaunchers() {
        exportLauncher = registerForActivityResult(CreateDocument(MIME_TYPE)) { uri: Uri? ->
            uri?.let { doExportHistory(it) }
        }
        importLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { doImportHistory(it) }
        }
    }

    private fun doExportHistory(uri: Uri) {
        Log.d(TAG, "Exporting to $uri")
        try {
            AppContext.context.contentResolver.openFileDescriptor(uri, "w").use { pfd ->
                FileOutputStream(pfd!!.fileDescriptor).use { fileOutputStream ->
                    OutputStreamWriter(fileOutputStream).use { writer ->
                        BufferedWriter(writer).use { bw ->
                            val suggestions = SearchRecentSuggestions(AppContext.context, SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES)
                            suggestions.cursor().use { cursor ->
                                if (cursor?.moveToFirst() == true) {
                                    do {
                                        val dataIdx = cursor.getColumnIndex(SearchRecentSuggestions.SuggestionColumns.DISPLAY1)
                                        val data = cursor.getString(dataIdx)
                                        bw.write(data + '\n')
                                    } while (cursor.moveToNext())
                                }
                            }
                            Log.i(TAG, "Exported to $uri")
                            Toast.makeText(requireContext(), resources.getText(R.string.title_history_export).toString() + " " + uri, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "While writing", e)
            Toast.makeText(requireContext(), resources.getText(R.string.error_export).toString() + " " + uri, Toast.LENGTH_SHORT).show()
        }
    }

    private fun doImportHistory(uri: Uri) {
        Log.d(TAG, "Importing from $uri")
        try {
            AppContext.context.contentResolver.openInputStream(uri).use { `is` ->
                InputStreamReader(`is`).use { reader ->
                    BufferedReader(reader).use { br ->
                        var line: String?
                        while (br.readLine().also { line = it } != null) {
                            recordQuery(AppContext.context, line!!.trim { it <= ' ' })
                        }
                        Log.i(TAG, "Imported from $uri")
                        Toast.makeText(requireContext(), resources.getText(R.string.title_history_import).toString() + " " + uri, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "While reading", e)
            Toast.makeText(requireContext(), resources.getText(R.string.error_import).toString() + " " + uri, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "HistoryF"

        private const val LOADER_ID = 2222

        private const val MIME_TYPE = "text/plain"

        // private const val HISTORY_FILE = "semantikos_search_history.txt"
    }
}