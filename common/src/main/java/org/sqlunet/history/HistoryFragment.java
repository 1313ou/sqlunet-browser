/*
 * Copyright (c) 2019-2023. Bernard Bou
 */

package org.sqlunet.history;

import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.sqlunet.browser.common.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;

/**
 * History activity
 *
 * @author Bernard Bou
 * @noinspection WeakerAccess
 */
public class HistoryFragment extends Fragment implements LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener
{
	private static final String TAG = "HistoryF";

	/**
	 * Export/import text file
	 */
	private static final String HISTORY_FILE = "semantikos_search_history.txt";

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
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// launchers
		registerLaunchers();

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.fragment_history, container, false);

		// list adapter bound to the cursor
		this.adapter = new SimpleCursorAdapter(this, // context
				R.layout.item_history, // row template to use android.R.layout.simple_list_item_1
				null, // empty cursor to bind to
				new String[]{SearchRecentSuggestions.SuggestionColumns.DISPLAY1}, // cursor columns to bind to
				new int[]{android.R.id.text1}, // objects to bind to those columns
				0);

		// list view
		this.listView = view.findViewById(android.R.id.list);

		// bind to adapter
		this.listView.setAdapter(this.adapter);

		// click listener
		this.listView.setOnItemClickListener(this);

		// swipe
		final SwipeGestureListener gestureListener = new SwipeGestureListener();
		this.listView.setOnTouchListener(gestureListener);

		// initializes the cursor loader
		LoaderManager.getInstance(this).initLoader(HistoryFragment.LOADER_ID, null, this);

		return view;
	}


	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater)
	{
		inflater.inflate(R.menu.history, menu);
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
			final android.provider.SearchRecentSuggestions suggestions = new android.provider.SearchRecentSuggestions(requireContext(), SearchRecentSuggestions.getAuthority(requireContext()), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
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
		final SearchRecentSuggestions suggestions = new SearchRecentSuggestions(requireContext(), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
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
		//noinspection EmptyTryBlock
		try (Cursor ignored = this.adapter.swapCursor(null))
		{
		}
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
				final Intent intent = History.makeSearchIntent(requireContext(), query);
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
			this.gestureDetector = new GestureDetector(requireContext(), this);
		}

		@Override
		public boolean onFling(@Nullable final MotionEvent e1, @NonNull final MotionEvent e2, final float velocityX, final float velocityY)
		{
			if (e1 == null)
			{
				return false;
			}

			final int position = HistoryFragment.this.listView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY()));

			if (Math.abs(e1.getY() - e2.getY()) <= SwipeGestureListener.SWIPE_MAX_OFF_PATH)
			{
				if (Math.abs(velocityX) >= SwipeGestureListener.SWIPE_THRESHOLD_VELOCITY)
				{
					if (e2.getX() - e1.getX() > SwipeGestureListener.SWIPE_MIN_DISTANCE)
					{
						// final SimpleCursorAdapter adapter = (SimpleCursorAdapter) listView.getAdapter();
						final Cursor cursor = HistoryFragment.this.adapter.getCursor();
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

								final SearchRecentSuggestions suggestions = new SearchRecentSuggestions(requireContext(), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
								suggestions.delete(itemId);
								final Cursor cursor2 = suggestions.cursor();
								HistoryFragment.this.adapter.swapCursor(cursor2);

								Toast.makeText(requireContext(), getResources().getString(R.string.title_history_deleted) + ' ' + data, Toast.LENGTH_SHORT).show();
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

	// I M P O R T / E X P O R T

	private static final String MIME_TYPE = "text/plain";

	/**
	 * Export history
	 */
	private void exportHistory()
	{
		exportLauncher.launch(MIME_TYPE);
	}

	/**
	 * Import history
	 */
	private void importHistory()
	{
		importLauncher.launch(new String[]{MIME_TYPE});
	}

	// D O C U M E N T   I N T E R F A C E

	// You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
	private ActivityResultLauncher<String> exportLauncher;

	private ActivityResultLauncher<String[]> importLauncher;

	private void registerLaunchers()
	{
		final ActivityResultContract<String, Uri> createContract = new ActivityResultContracts.CreateDocument(MIME_TYPE)
		{
			@NonNull
			@Override
			public Intent createIntent(@NonNull final Context context, @NonNull final String input)
			{
				final Intent intent = super.createIntent(context, input);
				intent.putExtra(Intent.EXTRA_TITLE, HISTORY_FILE);
				//intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
				return intent;
			}
		};
		this.exportLauncher = registerForActivityResult(createContract, uri -> {

			// The result data contains a URI for the document or directory that the user selected.
			if (uri != null)
			{
				doExportHistory(uri);
			}
		});

		final ActivityResultContract<String[], Uri> openContract = new ActivityResultContracts.OpenDocument()
		{
			@NonNull
			@Override
			public Intent createIntent(@NonNull final Context context, @NonNull final String[] input)
			{
				final Intent intent = super.createIntent(context, input);
				intent.putExtra(Intent.EXTRA_TITLE, HISTORY_FILE);
				//intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
				return intent;
			}
		};
		this.importLauncher = registerForActivityResult(openContract, uri -> {

			// The result data contains a URIs for the document or directory that the user selected.
			if (uri != null)
			{
				doImportHistory(uri);
			}
		});
	}

	/**
	 * Export history
	 */
	private void doExportHistory(@NonNull final Uri uri)
	{
		Log.d(HistoryFragment.TAG, "Exporting to " + uri);
		try ( //
		      final ParcelFileDescriptor pfd = requireContext().getContentResolver().openFileDescriptor(uri, "w") //
		)
		{
			assert pfd != null;
			try (final OutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());//
			     final Writer writer = new OutputStreamWriter(fileOutputStream);//
			     final BufferedWriter bw = new BufferedWriter(writer))
			{
				final SearchRecentSuggestions suggestions = new SearchRecentSuggestions(requireContext(), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
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
				cursor.close();
				Log.i(HistoryFragment.TAG, "Exported to " + uri);
				Toast.makeText(requireContext(), getResources().getText(R.string.title_history_export) + " " + uri, Toast.LENGTH_SHORT).show();
			}
		}
		catch (@NonNull final IOException e)
		{
			Log.e(HistoryFragment.TAG, "While writing", e);
			Toast.makeText(requireContext(), getResources().getText(R.string.error_export) + " " + uri, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Import history
	 */
	private void doImportHistory(@NonNull final Uri uri)
	{
		Log.d(HistoryFragment.TAG, "Importing from " + uri);
		try ( //
		      final InputStream is = requireContext().getContentResolver().openInputStream(uri); //
		      final Reader reader = new InputStreamReader(is); //
		      final BufferedReader br = new BufferedReader(reader) //
		)
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				History.recordQuery(requireContext(), line.trim());
			}
			Log.i(HistoryFragment.TAG, "Imported from " + uri);
			Toast.makeText(requireContext(), getResources().getText(R.string.title_history_import) + " " + uri, Toast.LENGTH_SHORT).show();
		}
		catch (@NonNull final IOException e)
		{
			Log.e(HistoryFragment.TAG, "While reading", e);
			Toast.makeText(requireContext(), getResources().getText(R.string.error_import) + " " + uri, Toast.LENGTH_SHORT).show();
		}
	}
}
