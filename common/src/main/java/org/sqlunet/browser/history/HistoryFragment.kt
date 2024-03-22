/*
 * Copyright (c) 2024. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.history

import android.content.Context
import android.content.Intent
import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.CursorAdapter
import android.widget.ListView
import android.widget.SimpleCursorAdapter
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
import kotlin.math.abs

/**
 * History fragment
 *
 * @author Bernard Bou
 * @noinspection WeakerAccess
 */
class HistoryFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {
    /**
     * List view
     */
    private var listView: ListView? = null

    /**
     * Cursor adapter
     */
    private var adapter: CursorAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // launchers
        registerLaunchers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // list adapter bound to the cursor
        adapter = SimpleCursorAdapter(
            requireContext(),  // context
            R.layout.item_history,  // row template to use android.R.layout.simple_list_item_1
            null, arrayOf(SearchRecentSuggestions.SuggestionColumns.DISPLAY1), intArrayOf(android.R.id.text1),  // objects to bind to those columns
            0
        )

        // list view
        listView = view.findViewById(android.R.id.list)

        // bind to adapter
        listView!!.setAdapter(adapter)

        // click listener
        listView!!.onItemClickListener = this

        // swipe
        val gestureListener = SwipeGestureListener()
        listView!!.setOnTouchListener(gestureListener)

        // menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.history, menu)
                // MenuCompat.setGroupDividerEnabled(menu, true);
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_history_export -> {
                        exportHistory()
                    }

                    R.id.action_history_import -> {
                        importHistory()
                    }

                    R.id.action_history_clear -> {
                        val suggestions = android.provider.SearchRecentSuggestions(requireContext(), getAuthority(requireContext()), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES)
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

        // initializes the cursor loader
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        exportLauncher!!.unregister()
        importLauncher!!.unregister()
    }

    override fun onCreateLoader(loaderID: Int, args: Bundle?): Loader<Cursor> {
        // assert loaderID == LOADER_ID;
        val suggestions = SearchRecentSuggestions(requireContext(), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES)
        return suggestions.cursorLoader()
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        cursor.moveToFirst()
        adapter!!.changeCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter!!.changeCursor(null)
    }

    // C L I C K
    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        Log.d(TAG, "Select $position")
        val cursor = (listView!!.adapter as SimpleCursorAdapter).cursor
        cursor.moveToPosition(position)
        if (!cursor.isAfterLast) {
            val dataIdx = cursor.getColumnIndex(SearchRecentSuggestions.SuggestionColumns.DISPLAY1)
            assert(dataIdx != -1)
            val query = cursor.getString(dataIdx)
            if (null != query) {
                val intent = makeSearchIntent(requireContext(), query)
                startActivity(intent)
            }
        }
    }

    // S W I P E

    private inner class SwipeGestureListener : SimpleOnGestureListener(), OnTouchListener {

        private val gestureDetector: GestureDetector = GestureDetector(requireContext(), this)

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null) {
                return false
            }
            val position = listView!!.pointToPosition(Math.round(e1.x), Math.round(e1.y))
            if (abs((e1.y - e2.y).toDouble()) <= SWIPE_MAX_OFF_PATH) {
                if (abs(velocityX.toDouble()) >= SWIPE_THRESHOLD_VELOCITY) {
                    if (e2.x - e1.x > SWIPE_MIN_DISTANCE) {
                        val cursor = adapter!!.cursor
                        if (!cursor.isAfterLast) {
                            if (cursor.moveToPosition(position)) {
                                val itemIdIdx = cursor.getColumnIndex("_id")
                                assert(itemIdIdx != -1)
                                val itemId = cursor.getString(itemIdIdx)
                                val dataIdx = cursor.getColumnIndex(SearchRecentSuggestions.SuggestionColumns.DISPLAY1)
                                assert(dataIdx != -1)
                                val data = cursor.getString(dataIdx)
                                val suggestions = SearchRecentSuggestions(requireContext(), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES)
                                suggestions.delete(itemId)
                                val cursor2 = suggestions.cursor()
                                adapter!!.changeCursor(cursor2)
                                Toast.makeText(requireContext(), resources.getString(R.string.title_history_deleted) + ' ' + data, Toast.LENGTH_SHORT).show()
                                return true
                            }
                        }
                    }
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_UP) {
                view.performClick()
            }
            return gestureDetector.onTouchEvent(event)
        }
    }

    /**
     * Export history
     */
    private fun exportHistory() {
        exportLauncher!!.launch(MIME_TYPE)
    }

    /**
     * Import history
     */
    private fun importHistory() {
        importLauncher!!.launch(arrayOf(MIME_TYPE))
    }

    // D O C U M E N T   I N T E R F A C E

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    private var exportLauncher: ActivityResultLauncher<String>? = null
    private var importLauncher: ActivityResultLauncher<Array<String>>? = null

    private fun registerLaunchers() {
        val createContract = object : CreateDocument(MIME_TYPE) {

            override fun createIntent(context: Context, input: String): Intent {
                val intent: Intent = super.createIntent(context, input)
                intent.putExtra(Intent.EXTRA_TITLE, HISTORY_FILE)
                //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
                return intent
            }
        }
        exportLauncher = registerForActivityResult(createContract) { uri: Uri? ->
            // The result data contains a URI for the document or directory that the user selected.
            uri?.let { doExportHistory(it) }
        }

        val openContract = object : ActivityResultContracts.OpenDocument() {

            override fun createIntent(context: Context, input: Array<String>): Intent {
                val intent: Intent = super.createIntent(context, input)
                intent.putExtra(Intent.EXTRA_TITLE, HISTORY_FILE)
                //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
                return intent
            }
        }
        importLauncher = registerForActivityResult(openContract) { uri: Uri? ->

            // The result data contains a URIs for the document or directory that the user selected.
            uri?.let { doImportHistory(it) }
        }
    }

    /**
     * Export history
     */
    private fun doExportHistory(uri: Uri) {
        Log.d(TAG, "Exporting to $uri")
        try {
            requireContext().contentResolver.openFileDescriptor(uri, "w").use { pfd ->
                assert(pfd != null)
                FileOutputStream(pfd!!.fileDescriptor).use { fileOutputStream ->
                    OutputStreamWriter(fileOutputStream).use { writer ->
                        BufferedWriter(writer).use { bw ->
                            val suggestions = SearchRecentSuggestions(requireContext(), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES)
                            suggestions.cursor().use { cursor ->
                                assert(cursor != null)
                                if (cursor!!.moveToFirst()) {
                                    do {
                                        val dataIdx = cursor.getColumnIndex(SearchRecentSuggestions.SuggestionColumns.DISPLAY1)
                                        assert(dataIdx != -1)
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

    /**
     * Import history
     */
    private fun doImportHistory(uri: Uri) {
        Log.d(TAG, "Importing from $uri")
        try {
            requireContext().contentResolver.openInputStream(uri).use { `is` ->
                InputStreamReader(`is`).use { reader ->
                    BufferedReader(reader).use { br ->
                        var line: String
                        while (br.readLine().also { line = it } != null) {
                            recordQuery(requireContext(), line.trim { it <= ' ' })
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

        const val SWIPE_MIN_DISTANCE = 120
        const val SWIPE_MAX_OFF_PATH = 250
        const val SWIPE_THRESHOLD_VELOCITY = 200

        // L O A D E R

        /**
         * Cursor loader id
         */
        private const val LOADER_ID = 2222

        // I M P O R T / E X P O R T

        /**
         * Export/import text file
         */
        private const val HISTORY_FILE = "semantikos_search_history.txt"

        /**
         * Mimetype
         */
        private const val MIME_TYPE = "text/plain"
    }
}
