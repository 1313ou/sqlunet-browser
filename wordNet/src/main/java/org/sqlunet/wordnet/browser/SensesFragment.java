package org.sqlunet.wordnet.browser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import org.sqlunet.browser.Module;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.wordnet.R;
import org.sqlunet.wordnet.SensePointer;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains;

import java.util.Locale;

/**
 * A fragment representing senses
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SensesFragment extends ListFragment
{
	/**
	 * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
	 */
	static private final String ACTIVATED_POSITION_NAME = "activated_position";
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int activatedPosition = AdapterView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(SensePointer sense);
	}

	/**
	 * The fragment's current callback object, which is notified of list item clicks.
	 */
	private Listener listener;

	/**
	 * Search query
	 */
	private String word;

	/**
	 * Word id
	 */
	private long wordId;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SensesFragment()
	{
		//
	}

	// L I F E C Y C L E

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// arguments
		final Bundle args = getArguments();

		// target word
		String query = args.getString(ProviderArgs.ARG_QUERYSTRING);
		if (query != null)
		{
			query = query.trim().toLowerCase(Locale.ENGLISH);
		}
		this.word = query;
		this.wordId = 0;

		// list adapter, with no data
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.item_sense, null, //
				new String[]{ //
						WordNetContract.PosTypes.POSNAME, //
						WordNetContract.Senses.SENSENUM, //
						WordNetContract.LexDomains.LEXDOMAIN, //
						WordNetContract.Synsets.DEFINITION, //
						WordNetContract.CasedWords.CASED, //
						WordNetContract.Senses.TAGCOUNT, //
						WordNetContract.Senses.LEXID, //
						WordNetContract.Senses.SENSEKEY, //
						WordNetContract.Words.WORDID, //
						WordNetContract.Synsets.SYNSETID, //
						WordNetContract.Senses.SENSEID, //
				}, //
				new int[]{ //
						R.id.pos, //
						R.id.sensenum, //
						R.id.lexdomain, //
						R.id.definition, //
						R.id.cased, //
						R.id.tagcount, //
						R.id.lexid, //
						R.id.sensekey, //
						R.id.wordid, //
						R.id.synsetid, //
						R.id.senseid, //
				}, 0);

		adapter.setViewBinder(new ViewBinder()
		{
			@Override
			public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex)
			{
				String text = cursor.getString(columnIndex);
				if (text == null)
				{
					text = "";
				}

				if (view instanceof TextView)
				{
					((TextView) view).setText(text);
				}
				else if (view instanceof ImageView)
				{
					try
					{
						((ImageView) view).setImageResource(Integer.parseInt(text));
					}
					catch (final NumberFormatException nfe)
					{
						((ImageView) view).setImageURI(Uri.parse(text));
					}
				}
				else
				{
					throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter");
				}
				return false;
			}
		});
		setListAdapter(adapter);

		// load the contents
		load();
	}

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
				final Uri uri = Uri.parse(Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI);
				final String[] projection = { //
						WordNetContract.Synsets.SYNSETID + " AS _id", //
						WordNetContract.Words.WORDID, //
						WordNetContract.Senses.SENSEID, //
						WordNetContract.Senses.SENSENUM, //
						WordNetContract.Senses.SENSEKEY, //
						WordNetContract.Senses.LEXID, //
						WordNetContract.Senses.TAGCOUNT, //
						WordNetContract.Synsets.SYNSETID, //
						WordNetContract.Synsets.DEFINITION, //
						WordNetContract.PosTypes.POSNAME, //
						WordNetContract.LexDomains.LEXDOMAIN, //
						WordNetContract.CasedWords.CASED};
				final String selection = Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEMMA + " = ?";
				final String[] selectionArgs = {SensesFragment.this.word};
				final String sortOrder = Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + ',' + Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSENUM;
				return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				// store source result
				if (cursor.moveToFirst())
				{
					final int wordId = cursor.getColumnIndex(WordNetContract.Words.WORDID);
					SensesFragment.this.wordId = cursor.getLong(wordId);
				}

				// pass on to list adapter
				((CursorAdapter) getListAdapter()).swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				((CursorAdapter) getListAdapter()).swapCursor(null);
			}
		});
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		if (this.activatedPosition != AdapterView.INVALID_POSITION)
		{
			// serialize and persist the activated item position.
			outState.putInt(SensesFragment.ACTIVATED_POSITION_NAME, this.activatedPosition);
		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public void onAttach(final Context context)
	{
		super.onAttach(context);
		onAttachToContext(context);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(final Activity activity)
	{
		super.onAttach(activity);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
		{
			onAttachToContext(activity);
		}
	}

	private void onAttachToContext(final Context context)
	{
		// activities containing this fragment must implement its listener
		if (context instanceof Listener)
		{
			//noinspection CastToIncompatibleInterface
			this.listener = (Listener) context;
			return;
		}
		throw new IllegalStateException("Activity must implement fragment's listener.");
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// reset the active listener interface to the dummy implementation.
		this.listener = null;
	}

	// V I E W

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_senses, container);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// restore the previously serialized activated item position, if any
		if (savedInstanceState != null && savedInstanceState.containsKey(SensesFragment.ACTIVATED_POSITION_NAME))
		{
			final int position = savedInstanceState.getInt(SensesFragment.ACTIVATED_POSITION_NAME);
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

	// C L I C K E V E N T L I S T E N

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
	 */
	@SuppressWarnings("unused")
	public void setActivateOnItemClick(final boolean activateOnItemClick)
	{
		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);
	}

	@Override
	public void onListItemClick(final ListView listView, final View view, final int position, final long id)
	{
		super.onListItemClick(listView, view, position, id);

		final SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
		final Cursor cursor = adapter.getCursor();
		if (cursor.moveToPosition(position))
		{
			final int idSynsetId = cursor.getColumnIndex(WordNetContract.Synsets.SYNSETID);
			final int idPos = cursor.getColumnIndex(WordNetContract.PosTypes.POSNAME);
			final int idCased = cursor.getColumnIndex(WordNetContract.CasedWords.CASED);

			final long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
			final String pos = cursor.getString(idPos);
			final String cased = cursor.getString(idCased);

			final SensePointer sense = new SensePointer(synsetId, pos, this.wordId, this.word, cased);

			// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
			if (this.listener != null)
			{
				this.listener.onItemSelected(sense);
			}
		}

		cursor.close();
	}
}
