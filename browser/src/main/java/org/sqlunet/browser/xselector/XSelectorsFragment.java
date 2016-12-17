package org.sqlunet.browser.xselector;

import android.android.support.local.app.ExpandableListFragment;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.R;
import org.sqlunet.browser.xselector.XLoader.FnLoaderCallbacks;
import org.sqlunet.browser.xselector.XLoader.PbLoaderCallbacks;
import org.sqlunet.browser.xselector.XLoader.VnLoaderCallbacks;
import org.sqlunet.browser.xselector.XLoader.WnLoaderCallbacks;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.provider.XSqlUNetContract;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XSqlUNetContract.Words_XNet_U;

import java.util.Locale;

/**
 * X selector fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XSelectorsFragment extends ExpandableListFragment
{
	static private final String TAG = "XSelectorsFragment";

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(XSelectorPointer pointer, String word, String cased, String pos);
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
	 * Database column
	 */
	static private final String DBCOLUMN = "xb";

	/**
	 * Source fields for groups
	 */
	static private final String[] groupFrom = {DBCOLUMN,};

	/**
	 * Target resource for groups
	 */
	static private final int[] groupTo = {R.id.xn,};

	/**
	 * Source fields
	 */
	static private final int[] childTo = { //
			R.id.wordid, //
			R.id.synsetid, //
			R.id.xid, //
			R.id.xmemberid, //
			R.id.xname, //
			R.id.xheader, //
			R.id.xinfo, //
			R.id.xdefinition, //
			R.id.xsourcestext, //
			R.id.xsources, //
			R.id.pm, //
	};

	/**
	 * Target resource
	 */
	static private final String[] childFrom = {Words_XNet_U.WORDID, //
			Words_XNet_U.SYNSETID, //
			Words_XNet_U.XID, //
			Words_XNet_U.XMEMBERID, //
			Words_XNet_U.XNAME, //
			Words_XNet_U.XHEADER, //
			Words_XNet_U.XINFO, //
			Words_XNet_U.XDEFINITION, //
			Words_XNet_U.SOURCES, //
			Words_XNet_U.SOURCES, //
			Words_XNet_U.SOURCES,};

	/**
	 * Xn group cursor
	 */
	private final MatrixCursor xnCursor;

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
	 * Word id
	 */
	private long wordId;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	@SuppressWarnings("boxing")
	public XSelectorsFragment()
	{
		this.xnCursor = new MatrixCursor(new String[]{"_id", DBCOLUMN, "loader"});
		this.xnCursor.addRow(new Object[]{0, "wordnet", 1111});
		this.xnCursor.addRow(new Object[]{1, "verbnet", 2222});
		this.xnCursor.addRow(new Object[]{2, "propbank", 3333});
		this.xnCursor.addRow(new Object[]{3, "framenet", 4444});
	}

	// C R E A T E

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// args
		Bundle args = getArguments();
		assert args != null;

		// target word
		String query = args.getString(ProviderArgs.ARG_QUERYSTRING);
		if (query != null)
		{
			query = query.trim().toLowerCase(Locale.ENGLISH);
		}
		this.word = query;
		this.wordId = 0;
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

	// V I E W

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_xselectors, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

		// restore the previously serialized activated item position, if any
		if (savedInstanceState != null && savedInstanceState.containsKey(XSelectorsFragment.STATE_ACTIVATED_SELECTOR))
		{
			final int position = savedInstanceState.getInt(XSelectorsFragment.STATE_ACTIVATED_SELECTOR);
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

		// load the contents
		load();
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		if (this.activatedPosition != AdapterView.INVALID_POSITION)
		{
			// serialize and persist the activated item position.
			outState.putInt(XSelectorsFragment.STATE_ACTIVATED_SELECTOR, this.activatedPosition);
		}
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
			public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
			{
				final Uri uri = Uri.parse(Words_FnWords_PbWords_VnWords.CONTENT_URI);
				final String[] projection = { //
						Words_FnWords_PbWords_VnWords.SYNSETID + " AS _id", //
						Words_FnWords_PbWords_VnWords.WORDID, //
						Words_FnWords_PbWords_VnWords.FNWORDID, //
						Words_FnWords_PbWords_VnWords.VNWORDID, //
						Words_FnWords_PbWords_VnWords.PBWORDID, //
				};
				final String selection = XSqlUNetContract.WORD + '.' + Words_FnWords_PbWords_VnWords.LEMMA + " = ?";
				final String[] selectionArgs = {XSelectorsFragment.this.word};
				final String sortOrder = XSqlUNetContract.POS + '.' + Words_FnWords_PbWords_VnWords.POS + ',' + Words_FnWords_PbWords_VnWords.SENSENUM;
				return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				// store source result
				if (cursor.moveToFirst())
				{
					final int idWordId = cursor.getColumnIndex(Words_FnWords_PbWords_VnWords.WORDID);
					XSelectorsFragment.this.wordId = cursor.getLong(idWordId);

					load(XSelectorsFragment.this.wordId);
				}
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}

	/**
	 * Load data from word id
	 *
	 * @param wordId word id
	 */
	private void load(final long wordId)
	{
		// adapter
		final ExpandableListAdapter adapter = new SimpleCursorTreeAdapter(getActivity(), this.xnCursor, R.layout.item_group_xselector, groupFrom, groupTo, R.layout.item_xselector, childFrom, childTo)
		{
			@Override
			protected void setViewImage(ImageView v, String value)
			{
				switch (v.getId())
				{
					case R.id.xsources:
						final String[] fields = value.split(",");
						for (String field : fields)
						{
							switch (field)
							{
								case "wn": //
									v.setImageResource(R.drawable.wordnet);
									return;
								case "vn": //
									v.setImageResource(R.drawable.verbnet);
									return;
								case "pb": //
									v.setImageResource(R.drawable.propbank);
									return;
								case "fn": //
									v.setImageResource(R.drawable.framenet);
									return;
							}
						}
						v.setImageDrawable(null);
						// v.setVisibility(View.GONE);
						break;

					case R.id.pm:
						final String[] fields2 = value.split(",");
						for (String field2 : fields2)
						{
							if (field2.startsWith("pm")) //
							{
								v.setImageResource(R.drawable.predicatematrix);
								v.setVisibility(View.VISIBLE);
								return;
							}
						}
						v.setImageDrawable(null);
						v.setVisibility(View.GONE);
						break;
				}
			}

			@Override
			protected Cursor getChildrenCursor(Cursor groupCursor)
			{
				Activity activity = getActivity();
				if (activity == null)
				{
					return null;
				}

				// given the group, we return a cursor for all the children within that group
				int groupPos = groupCursor.getPosition();
				String groupName = groupCursor.getString(groupCursor.getColumnIndex(DBCOLUMN));
				int loaderId = groupCursor.getInt(groupCursor.getColumnIndex("loader"));
				Log.d(TAG, "group " + groupPos + ' ' + groupName + " loader=" + loaderId);
				LoaderCallbacks<Cursor> callbacks = null;
				switch (groupPos)
				{
					case 0:
						callbacks = getWnCallbacks(wordId, groupPos);
						break;
					case 1:
						callbacks = getVnCallbacks(wordId, groupPos);
						break;
					case 2:
						callbacks = getPbCallbacks(wordId, groupPos);
						break;
					case 3:
						callbacks = getFnCallbacks(wordId, groupPos);
						break;
				}

				Loader<Cursor> loader1 = activity.getLoaderManager().getLoader(loaderId);
				if (loader1 != null && !loader1.isReset())
				{
					activity.getLoaderManager().restartLoader(loaderId, null, callbacks);
				}
				else
				{
					activity.getLoaderManager().initLoader(loaderId, null, callbacks);
				}

				return null;
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition)
			{
				return true;
			}
		};
		setListAdapter(adapter);

		// expand (triggers data loading)
		expandWordNet();
	}

	// L O A D E R  C A L L B A C K S

	/**
	 * Get WordNet callbacks
	 *
	 * @param wordId        word id
	 * @param groupPosition position in group
	 * @return WordNet callbacks
	 */
	private LoaderCallbacks<Cursor> getWnCallbacks(final long wordId, final int groupPosition)
	{
		return new WnLoaderCallbacks(getActivity(), wordId)
		{
			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor != null)
				{
					// XLoader.dump(cursor);

					// pass on to list adapter
					((CursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, cursor);
				}
				else
				{
					Log.i(TAG, "WN none");
				}
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				((CursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, null);
			}
		};
	}

	/**
	 * Get VerbNet callbacks
	 *
	 * @param wordId        word id
	 * @param groupPosition position in group
	 * @return VerbNet callbacks
	 */
	private LoaderCallbacks<Cursor> getVnCallbacks(final long wordId, final int groupPosition)
	{
		return new VnLoaderCallbacks(getActivity(), wordId)
		{
			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor != null)
				{
					// XLoader.dump(cursor);

					// pass on to list adapter
					((CursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, cursor);
				}
				else
				{
					Log.i(TAG, "VN none");
				}
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				((CursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, null);
			}
		};
	}

	/**
	 * Get PropBank callbacks
	 *
	 * @param wordId        word id
	 * @param groupPosition position in group
	 * @return PropBank callbacks
	 */
	private LoaderCallbacks<Cursor> getPbCallbacks(final long wordId, final int groupPosition)
	{
		return new PbLoaderCallbacks(getActivity(), wordId)
		{
			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor != null)
				{
					// XLoader.dump(cursor);

					// pass on to list adapter
					((CursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, cursor);
				}
				else
				{
					Log.i(TAG, "PB none");
				}
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				((CursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, null);
			}
		};
	}

	/**
	 * Get FrameNet callbacks
	 *
	 * @param wordId        word id
	 * @param groupPosition position in group
	 * @return FrameNet callbacks
	 */
	private LoaderCallbacks<Cursor> getFnCallbacks(final long wordId, final int groupPosition)
	{
		return new FnLoaderCallbacks(getActivity(), wordId)
		{
			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor != null)
				{
					// XLoader.dump(cursor);

					// pass on to list adapter
					((CursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, cursor);
				}
				else
				{
					Log.i(TAG, "FN none");
				}
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				((CursorTreeAdapter) getListAdapter()).setChildrenCursor(groupPosition, null);
			}
		};
	}

	// C L I C K

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
	 *
	 * @param activateOnItemClick true if activate
	 */
	public void setActivateOnItemClick(final boolean activateOnItemClick)
	{
		this.activateOnItemClick = activateOnItemClick;
	}

	@SuppressWarnings("boxing")
	@Override
	public boolean onChildClick(final ExpandableListView listView, final View view, final int groupPosition, final int childPosition, final long id)
	{
		super.onChildClick(listView, view, groupPosition, childPosition, id);

		if (this.listener != null)
		{
			//Log.d(TAG, "CLICK on group=" + groupPosition + " child=" + childPosition + " id=" + id);
			int index = listView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
			listView.setItemChecked(index, true);

			@SuppressWarnings("TypeMayBeWeakened") final SimpleCursorTreeAdapter adapter = (SimpleCursorTreeAdapter) getListAdapter();
			final Cursor cursor = adapter.getChild(groupPosition, childPosition);
			if (!cursor.isAfterLast())
			{
				// column indices
				final int idSynsetId = cursor.getColumnIndex(Words_XNet_U.SYNSETID);
				final int idXId = cursor.getColumnIndex(Words_XNet_U.XID);
				final int idXClassId = cursor.getColumnIndex(Words_XNet_U.XCLASSID);
				final int idXMemberId = cursor.getColumnIndex(Words_XNet_U.XMEMBERID);
				final int idXSources = cursor.getColumnIndex(Words_XNet_U.SOURCES);
				// final int idWordId = cursor.getColumnIndex(Words_XNet_U.WORDID);

				// data
				final long wordId = this.wordId;
				final String lemma = this.word;
				final String cased = this.word;
				final long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
				final String pos = synsetIdToPos(synsetId);
				final long xId = cursor.isNull(idXId) ? 0 : cursor.getLong(idXId);
				final long xClassId = cursor.isNull(idXClassId) ? 0 : cursor.getLong(idXClassId);
				final long xMemberId = cursor.isNull(idXMemberId) ? 0 : cursor.getLong(idXMemberId);
				final String xSources = cursor.getString(idXSources);
				final long xMask = XSelectorPointer.getMask(xSources);

				// pointer
				final XSelectorPointer pointer = new XSelectorPointer(synsetId, wordId, xId, xClassId, xMemberId, xSources, xMask);
				Log.d(TAG, "pointer=" + pointer);
				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				this.listener.onItemSelected(pointer, lemma, cased, pos);
			}
		}
		// cursor.close();
		return true;
	}

	// E X P A N D

	/**
	 * Expand WordNet section
	 */
	private void expandWordNet()
	{
		ExpandableListView listView = getExpandableListView();
		listView.expandGroup(0);
	}

	/**
	 * Expand all
	 */
	@SuppressWarnings("unused")
	private void expandAll()
	{
		ExpandableListView listView = getExpandableListView();
		int count = this.xnCursor.getCount();
		for (int position = 0; position < count; position++)
		{
			listView.expandGroup(position);
		}
	}

	// H E L P E R

	/**
	 * Extract pos from synset id number
	 *
	 * @param synsetId synset id
	 * @return pos
	 */
	private String synsetIdToPos(final Long synsetId)
	{
		if (synsetId == null)
		{
			return null;
		}
		int p = (int) Math.floor(synsetId / 100000000F);
		switch (p)
		{
			case 1:
				return "n";
			case 2:
				return "v";
			case 3:
				return "a";
			case 4:
				return "r";
			default:
				return null;
		}
	}
}
