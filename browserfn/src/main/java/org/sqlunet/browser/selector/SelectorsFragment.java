package org.sqlunet.browser.selector;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import org.sqlunet.Pointer;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.fn.R;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_or_Frames;
import org.sqlunet.framenet.provider.FrameNetProvider;
import org.sqlunet.provider.ProviderArgs;

import java.util.Locale;

/**
 * Selector Fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SelectorsFragment extends ListFragment
{
	// static protected final String TAG = "SelectorsFragment";

	private static final String ISLIKE = "islike";

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(Pointer pointer, String word, long wordId);
	}

	/**
	 * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
	 */
	static private final String STATE_ACTIVATED_SELECTOR = "activated_selector";

	/**
	 * Activate on click flag
	 */
	private boolean activateOnItemClick = false;
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int activatedPosition = AdapterView.INVALID_POSITION;

	/**
	 * The fragment's current callback object, which is notified of list item clicks.
	 */
	private Listener listener;

	/**
	 * Search query
	 */
	private String word;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SelectorsFragment()
	{
	}

	// C R E A T E

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// retain instance
		setRetainInstance(true);

		// arguments
		Bundle args = getArguments();
		assert args != null;

		// target word
		String query = args.getString(ProviderArgs.ARG_QUERYSTRING);
		if (query != null)
		{
			query = query.trim().toLowerCase(Locale.ENGLISH);
		}
		this.word = query;

		// list adapter, with no data
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.item_selector, null, //
				new String[]{ //
						LexUnits_or_Frames.NAME, LexUnits_or_Frames.FRAMENAME, LexUnits_or_Frames.WORD, LexUnits_or_Frames.FNID, LexUnits_or_Frames.FNWORDID, LexUnits_or_Frames.WORDID, LexUnits_or_Frames.FRAMEID, LexUnits_or_Frames.ISFRAME, //
				}, //
				new int[]{ //
						R.id.fnname, R.id.fnframename, R.id.fnword, R.id.fnid, R.id.fnwordid, R.id.wordid, R.id.fnframeid, R.id.icon, //
				}, 0);

		adapter.setViewBinder(new ViewBinder()
		{
			@Override
			public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex)
			{
				if (view instanceof TextView)
				{
					final int idIsLike = cursor.getColumnIndex(ISLIKE);
					final int idName = cursor.getColumnIndex(LexUnits_or_Frames.NAME);

					String text = cursor.getString(columnIndex);
					if (text == null || "0".equals(text))
					{
						text = "";
					}
					final TextView textView = (TextView) view;
					textView.setText(text);

					if (idName == columnIndex)
					{
						boolean isLike = cursor.getInt(idIsLike) != 0;
						textView.setTextColor(isLike ? Color.GRAY : Color.BLACK);
					}
					return true;
				}
				else if (view instanceof ImageView)
				{
					boolean isFrame = cursor.getInt(columnIndex) != 0;
					((ImageView) view).setImageResource(isFrame ? R.drawable.roles : R.drawable.member);
					return true;
				}
				else
				{
					throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter");
					//return false;
				}
			}
		});
		setListAdapter(adapter);
	}

	// V I E W

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_selectors, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

		// restore the previously serialized activated item position, if any
		if (savedInstanceState != null)
		{
			final int position = savedInstanceState.getInt(SelectorsFragment.STATE_ACTIVATED_SELECTOR, AdapterView.INVALID_POSITION);
			if (position == AdapterView.INVALID_POSITION)
			{
				getListView().setItemChecked(this.activatedPosition, false);
			}
			else
			{
				getListView().setItemChecked(position, true);
			}
			this.activatedPosition = position;
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// load the contents (once activity is available)
		load();
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		if (this.activatedPosition != AdapterView.INVALID_POSITION)
		{
			// serialize and persist the activated item position.
			outState.putInt(SelectorsFragment.STATE_ACTIVATED_SELECTOR, this.activatedPosition);
		}
	}

	// L I S T E N E R

	/**
	 * Set listener
	 *
	 * @param listener listener
	 */
	public void setListener(final Listener listener)
	{
		this.listener = listener;
	}

	// L O A D

	/**
	 * Load data from word
	 */
	private void load()
	{
		// load the contents
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(FrameNetProvider.makeUri(LexUnits_or_Frames.CONTENT_URI_TABLE));
				final String[] projection = { //
						LexUnits_or_Frames.ID, //
						LexUnits_or_Frames.FNID, //
						LexUnits_or_Frames.FNWORDID, //
						LexUnits_or_Frames.WORDID, //
						LexUnits_or_Frames.WORD, //
						LexUnits_or_Frames.NAME, //
						LexUnits_or_Frames.FRAMENAME, //
						LexUnits_or_Frames.FRAMEID, //
						LexUnits_or_Frames.ISFRAME, //
						LexUnits_or_Frames.WORD + "<>'" + SelectorsFragment.this.word + "' AS " + ISLIKE, //
				};
				final String selection = LexUnits_or_Frames.WORD + " LIKE ? || '%'";
				final String[] selectionArgs = {SelectorsFragment.this.word};
				final String sortOrder = LexUnits_or_Frames.ISFRAME + ',' + LexUnits_or_Frames.WORD + ',' + LexUnits_or_Frames.ID;
				return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				// pass on to list adapter
				((CursorAdapter) getListAdapter()).swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> arg)
			{
				((CursorAdapter) getListAdapter()).swapCursor(null);
			}
		});
	}

	// C L I C K

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
	 *
	 * @param activateOnItemClick true if activate
	 */
	public void setActivateOnItemClick(@SuppressWarnings("SameParameterValue") final boolean activateOnItemClick)
	{
		this.activateOnItemClick = activateOnItemClick;
	}

	@Override
	public void onListItemClick(final ListView listView, final View view, final int position, final long id)
	{
		super.onListItemClick(listView, view, position, id);
		activate(position);
	}

	/**
	 * Activate item at position
	 *
	 * @param position position
	 */
	private void activate(int position)
	{
		final ListView listView = getListView();
		listView.setItemChecked(position, true);
		this.activatedPosition = position;

		if (this.listener != null)
		{
			final SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
			final Cursor cursor = adapter.getCursor();
			if (cursor.moveToPosition(position))
			{
				// column indexes
				final int idIsFrame = cursor.getColumnIndex(LexUnits_or_Frames.ISFRAME);
				final int idFnId = cursor.getColumnIndex(LexUnits_or_Frames.FNID);
				final int idWord = cursor.getColumnIndex(LexUnits_or_Frames.WORD);
				final int idWordId = cursor.getColumnIndex(LexUnits_or_Frames.WORDID);

				// retrieve
				final long fnId = cursor.getLong(idFnId);
				final boolean isFrame = cursor.getInt(idIsFrame) != 0;
				final String word = cursor.getString(idWord);
				final long wordId = cursor.getLong(idWordId);

				// pointer
				final Pointer pointer = isFrame ? new FnFramePointer(fnId) : new FnLexUnitPointer(fnId);

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				this.listener.onItemSelected(pointer, word, wordId);
			}
		}
	}
}
