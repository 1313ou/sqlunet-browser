/*
 * Copyright (c) 2019-2023. Bernard Bou
 */

package org.sqlunet.history;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.sqlunet.browser.common.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;

/**
 * History activity
 *
 * @author Bernard Bou
 * @noinspection WeakerAccess
 */
public class HistoryActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener
{
	private static final String TAG = "History";

	/**
	 * Export/import text file
	 */
	private static final String LISTFILE = "semantikos_list.txt";

	/**
	 * Write permission request code
	 */
	private static final int EXTERNALSTORAGE_WRITEPERMSREQUESTCODE = 1111;

	/**
	 * Read permission request code
	 */
	private static final int EXTERNALSTORAGE_READPERMSREQUESTCODE = 1112;

	/**
	 * Cursor loader id
	 */
	private static final int LOADER_ID = 2222;

	/**
	 * List view
	 */
	private ListView listView;

	/**
	 * Cursor adapter
	 */
	private CursorAdapter adapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_history);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// show the Up button in the action bar.
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		}

		// list adapter bound to the cursor
		this.adapter = new SimpleCursorAdapter(this, // context
				R.layout.item_history, // row template to use android.R.layout.simple_list_item_1
				null, // empty cursor to bind to
				new String[]{SearchRecentSuggestions.SuggestionColumns.DISPLAY1}, // cursor columns to bind to
				new int[]{android.R.id.text1}, // objects to bind to those columns
				0);

		// list view
		this.listView = findViewById(android.R.id.list);

		// bind to adapter
		this.listView.setAdapter(this.adapter);

		// click listener
		this.listView.setOnItemClickListener(this);

		// swipe
		final SwipeGestureListener gestureListener = new SwipeGestureListener();
		this.listView.setOnTouchListener(gestureListener);

		// initializes the cursor loader
		LoaderManager.getInstance(this).initLoader(HistoryActivity.LOADER_ID, null, this);
	}

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		int itemId = item.getItemId();
		if (itemId == R.id.action_history_export)
		{
			exportHistory();
		}
		else if (itemId == R.id.action_history_import)
		{
			importHistory();
		}
		else if (itemId == R.id.action_history_clear)
		{
			final android.provider.SearchRecentSuggestions suggestions = new android.provider.SearchRecentSuggestions(this, SearchRecentSuggestions.getAuthority(this), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
			suggestions.clearHistory();
			return true;
		}
		return false;
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(final int loaderID, final Bundle args)
	{
		// assert loaderID == LOADER_ID;
		final SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
		return suggestions.cursorLoader();
	}

	@Override
	public void onLoadFinished(@NonNull final Loader<Cursor> loader, @NonNull final Cursor cursor)
	{
		cursor.moveToFirst();
		this.adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(@NonNull final Loader<Cursor> loader)
	{
		this.adapter.swapCursor(null);
	}

	// C L I C K

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
	{
		Log.d(TAG, "Select " + position);
		final Cursor cursor = ((SimpleCursorAdapter) this.listView.getAdapter()).getCursor();
		cursor.moveToPosition(position);
		if (!cursor.isAfterLast())
		{
			final int dataIdx = cursor.getColumnIndex(SearchRecentSuggestions.SuggestionColumns.DISPLAY1);
			assert dataIdx != -1;
			final String query = cursor.getString(dataIdx);
			if (null != query)
			{
				final Intent intent = History.makeSearchIntent(this, query);
				startActivity(intent);
			}
		}
	}

	// S W I P E

	private class SwipeGestureListener extends SimpleOnGestureListener implements OnTouchListener
	{
		static final int SWIPE_MIN_DISTANCE = 120;
		static final int SWIPE_MAX_OFF_PATH = 250;
		static final int SWIPE_THRESHOLD_VELOCITY = 200;

		@NonNull
		private final GestureDetector gestureDetector;

		@SuppressWarnings("WeakerAccess")
		public SwipeGestureListener()
		{
			this.gestureDetector = new GestureDetector(HistoryActivity.this, this);
		}

		@Override
		public boolean onFling(@Nullable final MotionEvent e1, @NonNull final MotionEvent e2, final float velocityX, final float velocityY)
		{
			if (e1 == null)
			{
				return false;
			}

			final int position = HistoryActivity.this.listView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY()));

			if (Math.abs(e1.getY() - e2.getY()) <= SwipeGestureListener.SWIPE_MAX_OFF_PATH)
			{
				if (Math.abs(velocityX) >= SwipeGestureListener.SWIPE_THRESHOLD_VELOCITY)
				{
					if (e2.getX() - e1.getX() > SwipeGestureListener.SWIPE_MIN_DISTANCE)
					{
						// final SimpleCursorAdapter adapter = (SimpleCursorAdapter) listView.getAdapter();
						final Cursor cursor = HistoryActivity.this.adapter.getCursor();
						if (!cursor.isAfterLast())
						{
							if (cursor.moveToPosition(position))
							{
								final int itemIdIdx = cursor.getColumnIndex("_id");
								assert itemIdIdx != -1;
								final String itemId = cursor.getString(itemIdIdx);

								final int dataIdx = cursor.getColumnIndex(SearchRecentSuggestions.SuggestionColumns.DISPLAY1);
								assert dataIdx != -1;
								final String data = cursor.getString(dataIdx);

								final SearchRecentSuggestions suggestions = new SearchRecentSuggestions(HistoryActivity.this, SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
								suggestions.delete(itemId);
								final Cursor cursor2 = suggestions.cursor();
								HistoryActivity.this.adapter.swapCursor(cursor2);

								Toast.makeText(HistoryActivity.this, getResources().getString(R.string.title_history_deleted) + ' ' + data, Toast.LENGTH_SHORT).show();
								return true;
							}
						}
						// do not close: cursor.close();
					}
				}
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onTouch(@NonNull final View view, @NonNull final MotionEvent event)
		{
			if (event.getAction() == MotionEvent.ACTION_UP)
			{
				view.performClick();
			}
			return this.gestureDetector.onTouchEvent(event);
		}
	}

	// P E R M I S S I O N S

	static private final String WRITEPERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

	static private final String READPERMISSION = "android.permission.READ_EXTERNAL_STORAGE";

	@TargetApi(Build.VERSION_CODES.M)
	private void requestPermissions(int permsRequestCode, @NonNull final String... permissions)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			requestPermissions(permissions, permsRequestCode);
		}
	}

	@Override
	public void onRequestPermissionsResult(final int permsRequestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults)
	{
		super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
		switch (permsRequestCode)
		{
			case EXTERNALSTORAGE_WRITEPERMSREQUESTCODE:
				boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
				Log.i(TAG, "External storage write: " + writeAccepted);
				if (writeAccepted)
				{
					doExportHistory();
				}
				break;
			case EXTERNALSTORAGE_READPERMSREQUESTCODE:
				boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
				Log.i(TAG, "External storage read: " + readAccepted);
				if (readAccepted)
				{
					doImportHistory();
				}
				break;
			default:
				break;
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	@TargetApi(Build.VERSION_CODES.M)
	private boolean hasPermission(@NonNull String permission)
	{
		//noinspection SimplifiableIfStatement
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
		}
		return true;
	}

	private boolean shouldAskPermission(String permission)
	{
		final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		return (sharedPrefs.getBoolean(permission, true));
	}

	private void markPermissionAsAsked(@NonNull final String... permissions)
	{
		final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor editor = sharedPrefs.edit();
		for (String permission : permissions)
		{
			editor.putBoolean(permission, false);
		}
		editor.apply();
	}

	// I M P O R T / E X P O R T

	/**
	 * Export history
	 */
	private void exportHistory()
	{
		if (!HistoryActivity.isExternalStorageWritable())
		{
			return;
		}

		if (!hasPermission(WRITEPERMISSION))
		{
			if (shouldAskPermission(WRITEPERMISSION))
			{
				requestPermissions(EXTERNALSTORAGE_WRITEPERMSREQUESTCODE, READPERMISSION, WRITEPERMISSION);
				markPermissionAsAsked(READPERMISSION, WRITEPERMISSION);
			}
			return;
		}

		doExportHistory();
	}

	/**
	 * Export history
	 */
	private void doExportHistory()
	{
		final File exportFile = new File(Environment.getExternalStorageDirectory(), HistoryActivity.LISTFILE);
		Log.d(HistoryActivity.TAG, "Exporting to " + exportFile.getPath());
		try
		{
			final FileWriter writer = new FileWriter(exportFile);
			final BufferedWriter bw = new BufferedWriter(writer);
			final SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
			final Cursor cursor = suggestions.cursor();
			assert cursor != null;
			if (cursor.moveToFirst())
			{
				do
				{
					final int dataIdx = cursor.getColumnIndex(SearchRecentSuggestions.SuggestionColumns.DISPLAY1);
					assert dataIdx != -1;
					final String data = cursor.getString(dataIdx);
					bw.write(data + '\n');
				}
				while (cursor.moveToNext());
			}
			bw.close();
			cursor.close();
			Log.i(HistoryActivity.TAG, "Exported to " + exportFile.getPath());
			Toast.makeText(this, getResources().getText(R.string.title_history_export) + " " + exportFile.getPath(), Toast.LENGTH_SHORT).show();
		}
		catch (@NonNull final IOException e)
		{
			Log.e(HistoryActivity.TAG, "While writing", e);
			Toast.makeText(this, getResources().getText(R.string.error_export) + " " + exportFile.getPath(), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Import history
	 */
	private void importHistory()
	{
		if (!HistoryActivity.isExternalStorageReadable())
		{
			return;
		}

		if (!hasPermission(READPERMISSION))
		{
			if (shouldAskPermission(READPERMISSION))
			{
				requestPermissions(EXTERNALSTORAGE_READPERMSREQUESTCODE, READPERMISSION, WRITEPERMISSION);
				markPermissionAsAsked(READPERMISSION, WRITEPERMISSION);
			}
			return;
		}

		doImportHistory();
	}

	/**
	 * Import history
	 */
	private void doImportHistory()
	{
		final File importFile = new File(Environment.getExternalStorageDirectory(), HistoryActivity.LISTFILE);
		Log.d(HistoryActivity.TAG, "Importing from " + importFile.getPath());
		try
		{
			final FileReader reader = new FileReader(importFile);
			final BufferedReader br = new BufferedReader(reader);
			String line;
			while ((line = br.readLine()) != null)
			{
				History.recordQuery(this, line.trim());
			}
			br.close();
			Log.i(HistoryActivity.TAG, "Imported from " + importFile.getPath());
			Toast.makeText(this, getResources().getText(R.string.title_history_import) + " " + importFile.getPath(), Toast.LENGTH_SHORT).show();
		}
		catch (@NonNull final IOException e)
		{
			Log.e(HistoryActivity.TAG, "While reading", e);
			Toast.makeText(this, getResources().getText(R.string.error_import) + " " + importFile.getPath(), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Checks if external storage is available for read and write
	 */
	private static boolean isExternalStorageWritable()
	{
		final String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	/**
	 * Checks if external storage is available to at least read
	 */
	private static boolean isExternalStorageReadable()
	{
		final String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}
}
