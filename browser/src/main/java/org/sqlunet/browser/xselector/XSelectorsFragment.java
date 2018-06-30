package org.sqlunet.browser.xselector;

import android.android.support.local.app.ExpandableListFragment;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.R;
import org.sqlunet.browser.xn.Settings;
import org.sqlunet.browser.xselector.XLoader.FnLoaderCallbacks;
import org.sqlunet.browser.xselector.XLoader.PbLoaderCallbacks;
import org.sqlunet.browser.xselector.XLoader.VnLoaderCallbacks;
import org.sqlunet.browser.xselector.XLoader.WnLoaderCallbacks;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.provider.XSqlUNetContract;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XSqlUNetContract.Words_XNet_U;
import org.sqlunet.provider.XSqlUNetProvider;

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
	static private final String GROUPID_COLUMN = "_id";

	/**
	 * Database column
	 */
	static private final String GROUPNAME_COLUMN = "xn";

	/**
	 * Loader column
	 */
	static private final String GROUPLOADER_COLUMN = "xloader";

	/**
	 * Database column
	 */
	static private final String GROUPICON_COLUMN = "xicon";

	/**
	 * Id
	 */
	static public final int GROUPID_WORDNET = 1;

	/**
	 * Id
	 */
	static public final int GROUPID_VERBNET = 2;

	/**
	 * Id
	 */
	static public final int GROUPID_PROPBANK = 3;

	/**
	 * Id
	 */
	static public final int GROUPID_FRAMENET = 4;

	/**
	 * Source fields for groups
	 */
	static private final String[] groupFrom = {GROUPNAME_COLUMN, GROUPICON_COLUMN,};

	/**
	 * Target resource for groups
	 */
	static private final int[] groupTo = {R.id.xn, R.id.xicon,};

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
	@NonNull
	private final MatrixCursor xnCursor;

	/**
	 * WordNet group position
	 */
	private int groupWordNetPosition;

	/**
	 * VerbNet group position
	 */
	private int groupVerbNetPosition;

	/**
	 * PropBank group position
	 */
	private int groupPropBankPosition;

	/**
	 * FrameNet group position
	 */
	private int groupFrameNetPosition;

	/**
	 * The current activated item position.
	 */
	private int groupPosition;

	/**
	 * The fragment's current callback object, which is notified of list item clicks.
	 */
	private Listener listener;

	/**
	 * Search query
	 */
	@Nullable
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
		this.groupWordNetPosition = -1;
		this.groupVerbNetPosition = -1;
		this.groupPropBankPosition = -1;
		this.groupFrameNetPosition = -1;
		this.xnCursor = new MatrixCursor(new String[]{GROUPID_COLUMN, GROUPNAME_COLUMN, GROUPLOADER_COLUMN, GROUPICON_COLUMN});
	}

	// C R E A T E

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// retain instance
		setRetainInstance(true);

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

		// fill groups
		int position = 0;
		int enable = Settings.getAllPref(getContext());

		if (Settings.Source.WORDNET.test(enable))
		{
			this.groupWordNetPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_WORDNET, "wordnet", 1111, Integer.toString(R.drawable.wordnet)});
		}
		if (Settings.Source.VERBNET.test(enable))
		{
			this.groupVerbNetPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_VERBNET, "verbnet", 2222, Integer.toString(R.drawable.verbnet)});
		}
		if (Settings.Source.PROPBANK.test(enable))
		{
			this.groupPropBankPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_PROPBANK, "propbank", 3333, Integer.toString(R.drawable.propbank)});
		}
		if (Settings.Source.FRAMENET.test(enable))
		{
			this.groupFrameNetPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_FRAMENET, "framenet", 4444, Integer.toString(R.drawable.framenet)});
		}
		this.groupPosition = position >= 0 ? 0 : -1;
		Log.d(XSelectorsFragment.TAG, "init position " + this.groupPosition + " " + this);
	}

	// V I E W

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_xselectors, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view0, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view0, savedInstanceState);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		final ExpandableListView view = getListView();
		assert view != null;
		view.setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

		// restore the previously serialized activated item position, if any
		if (savedInstanceState != null && savedInstanceState.containsKey(XSelectorsFragment.STATE_ACTIVATED_SELECTOR))
		{
			this.groupPosition = savedInstanceState.getInt(XSelectorsFragment.STATE_ACTIVATED_SELECTOR);
			Log.d(XSelectorsFragment.TAG, "restored position " + this.groupPosition + " " + this);
		}
	}

	@Override
	public void onStart()
	{
		// load the contents (once activity is available)
		load();

		super.onStart();
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// serialize and persist the activated item position.
		outState.putInt(XSelectorsFragment.STATE_ACTIVATED_SELECTOR, this.groupPosition);
		Log.d(XSelectorsFragment.TAG, "saved position " + this.groupPosition + " " + this);
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
	 * Load id from word
	 */
	private void load()
	{
		// load the contents
		getLoaderManager().initLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@NonNull
			@Override
			public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
			{
				Log.d(XSelectorsFragment.TAG, "onCreateLoader() for wordid loader id " + id);
				final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(Words_FnWords_PbWords_VnWords.CONTENT_URI_TABLE));
				final String[] projection = { //
						Words_FnWords_PbWords_VnWords.SYNSETID + " AS " + GROUPID_COLUMN, //
						Words_FnWords_PbWords_VnWords.WORDID, //
						Words_FnWords_PbWords_VnWords.FNWORDID, //
						Words_FnWords_PbWords_VnWords.VNWORDID, //
						Words_FnWords_PbWords_VnWords.PBWORDID, //
				};
				final String selection = XSqlUNetContract.WORD + '.' + Words_FnWords_PbWords_VnWords.LEMMA + " = ?";
				final String[] selectionArgs = {XSelectorsFragment.this.word};
				final String sortOrder = XSqlUNetContract.POS + '.' + Words_FnWords_PbWords_VnWords.POS + ',' + Words_FnWords_PbWords_VnWords.SENSENUM;
				final Context context = getActivity();
				assert context != null;
				return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(@NonNull final Loader<Cursor> loader, @NonNull final Cursor cursor)
			{
				int id = loader.getId();
				Log.d(XSelectorsFragment.TAG, "onLoadFinished() for wordid loader id " + id);

				// store source progressMessage
				if (cursor.moveToFirst())
				{
					final int idWordId = cursor.getColumnIndex(Words_FnWords_PbWords_VnWords.WORDID);
					XSelectorsFragment.this.wordId = cursor.getLong(idWordId);
					initialize();
				}
			}

			@Override
			public void onLoaderReset(@NonNull final Loader<Cursor> loader)
			{
				int id = loader.getId();
				Log.d(XSelectorsFragment.TAG, "onLoaderReset() for wordid loader id " + id);
			}
		});
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		// adapter
		final ExpandableListAdapter adapter = new SimpleCursorTreeAdapter(getActivity(), this.xnCursor, R.layout.item_group_xselector, groupFrom, groupTo, R.layout.item_xselector, childFrom, childTo)
		{
			@Override
			protected Cursor getChildrenCursor(@NonNull Cursor groupCursor)
			{
				// given the group, return a cursor for all the children within that group
				int groupPosition = groupCursor.getPosition();
				int loaderId = groupCursor.getInt(groupCursor.getColumnIndex(GROUPLOADER_COLUMN));
				int groupId = groupCursor.getInt(groupCursor.getColumnIndex(GROUPID_COLUMN));
				// String groupName = groupCursor.getString(groupCursor.getColumnIndex(GROUPNAME_COLUMN));
				// Log.d(XSelectorsFragment.TAG, "group " + groupPosition + ' ' + groupName + " loader=" + loaderId);

				initLoader(groupPosition, groupId, loaderId);

				return null; // set later when loader completes
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition)
			{
				return true;
			}

			@Override
			protected void setViewImage(@NonNull ImageView v, @NonNull String value)
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

					default:
						super.setViewImage(v, value);
						break;
				}
			}
		};
		setListAdapter(adapter);

		// expand (triggers data loading)
		Log.d(XSelectorsFragment.TAG, "expand position " + this.groupPosition + " " + this);
		expand(this.groupPosition);
	}

	/**
	 * Init loaders for
	 *
	 * @param groupPosition group position
	 * @param groupId       group id
	 * @param loaderId      loader id
	 */
	private void initLoader(int groupPosition, int groupId, int loaderId)
	{
		Log.d(XSelectorsFragment.TAG, "initLoader() for  groupPosition=" + groupPosition + " groupId=" + groupId + " loaderId=" + loaderId);
		LoaderCallbacks<Cursor> callbacks;
		switch (groupId)
		{
			case GROUPID_WORDNET:
				callbacks = getWnCallbacks(this.wordId, groupPosition);
				break;
			case GROUPID_VERBNET:
				callbacks = getVnCallbacks(this.wordId, groupPosition);
				break;
			case GROUPID_PROPBANK:
				callbacks = getPbCallbacks(this.wordId, groupPosition);
				break;
			case GROUPID_FRAMENET:
				callbacks = getFnCallbacks(this.wordId, groupPosition);
				break;
			default:
				return;
		}

		final FragmentActivity activity = getActivity();
		if (activity == null)
		{
			return;
		}
		if (activity.isFinishing())
		{
			return;
		}

		final Loader<Cursor> loaderChild = activity.getSupportLoaderManager().getLoader(loaderId);
		Log.d(XSelectorsFragment.TAG, "Existing loader with same loaderId null=" + (loaderChild == null));
		if (loaderChild != null)
		{
			Log.d(XSelectorsFragment.TAG, "Existing loader with same loaderId isReset=" + loaderChild.isReset());
		}

		if (loaderChild != null && !loaderChild.isReset())
		{
			Log.d(XSelectorsFragment.TAG, "restartLoader()");
			activity.getSupportLoaderManager().restartLoader(loaderId, null, callbacks);
		}
		else
		{
			Log.d(XSelectorsFragment.TAG, "initLoader()");
			activity.getSupportLoaderManager().initLoader(loaderId, null, callbacks);
		}
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
			public void onLoadFinished(@NonNull final Loader<Cursor> loader, @Nullable final Cursor cursor)
			{
				if (cursor != null)
				{
					// XLoader.dump(cursor);

					// pass on to list adapter
					final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
					assert adapter != null;
					adapter.setChildrenCursor(groupPosition, cursor);
				}
				else
				{
					Log.i(XSelectorsFragment.TAG, "WN none");
				}
			}

			@Override
			public void onLoaderReset(@NonNull final Loader<Cursor> loader)
			{
				int id = loader.getId();
				Log.d(XSelectorsFragment.TAG, "onLoaderReset() for WN loader id " + id);

				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				assert adapter != null;
				adapter.setChildrenCursor(groupPosition, null);
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
			public void onLoadFinished(@NonNull final Loader<Cursor> loader, @Nullable final Cursor cursor)
			{
				if (cursor != null)
				{
					// XLoader.dump(cursor);

					// pass on to list adapter
					final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
					assert adapter != null;
					adapter.setChildrenCursor(groupPosition, cursor);
				}
				else
				{
					Log.i(XSelectorsFragment.TAG, "VN none");
				}
			}

			@Override
			public void onLoaderReset(@NonNull final Loader<Cursor> loader)
			{
				int id = loader.getId();
				Log.d(XSelectorsFragment.TAG, "onLoaderReset() for VN loader id " + id);

				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				assert adapter != null;
				adapter.setChildrenCursor(groupPosition, null);
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
			public void onLoadFinished(@NonNull final Loader<Cursor> loader, @Nullable final Cursor cursor)
			{
				if (cursor != null)
				{
					// XLoader.dump(cursor);

					// pass on to list adapter
					final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
					assert adapter != null;
					adapter.setChildrenCursor(groupPosition, cursor);
				}
				else
				{
					Log.i(XSelectorsFragment.TAG, "PB none");
				}
			}

			@Override
			public void onLoaderReset(@NonNull final Loader<Cursor> loader)
			{
				int id = loader.getId();
				Log.d(XSelectorsFragment.TAG, "onLoaderReset() for PB loader id " + id);

				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				assert adapter != null;
				adapter.setChildrenCursor(groupPosition, null);
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
			public void onLoadFinished(@NonNull final Loader<Cursor> loader, @Nullable final Cursor cursor)
			{
				if (cursor != null)
				{
					// XLoader.dump(cursor);

					// pass on to list adapter
					final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
					assert adapter != null;
					adapter.setChildrenCursor(groupPosition, cursor);
				}
				else
				{
					Log.i(XSelectorsFragment.TAG, "FN none");
				}
			}

			@Override
			public void onLoaderReset(@NonNull final Loader<Cursor> loader)
			{
				int id = loader.getId();
				Log.d(XSelectorsFragment.TAG, "onLoaderReset() for FN loader id " + id);

				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				assert adapter != null;
				adapter.setChildrenCursor(groupPosition, null);
			}
		};
	}

	// S E L E C T I O N   L I S T E N E R

	@Override
	public void onGroupExpand(int groupPosition)
	{
		super.onGroupExpand(groupPosition);
		this.groupPosition = groupPosition;
		Log.d(XSelectorsFragment.TAG, "select " + this.groupPosition);
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

	@SuppressWarnings("boxing")
	@Override
	public boolean onChildClick(@NonNull final ExpandableListView listView, final View view, final int groupPosition, final int childPosition, final long id)
	{
		super.onChildClick(listView, view, groupPosition, childPosition, id);

		if (this.listener != null)
		{
			//Log.d(XSelectorsFragment.TAG, "CLICK on group=" + groupPosition + " child=" + childPosition + " id=" + id);
			int index = listView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
			listView.setItemChecked(index, true);

			@SuppressWarnings("TypeMayBeWeakened") final SimpleCursorTreeAdapter adapter = (SimpleCursorTreeAdapter) getListAdapter();
			assert adapter != null;
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

				int groupId = -1;
				if (this.groupWordNetPosition != -1 && groupPosition == this.groupWordNetPosition)
				{
					groupId = GROUPID_WORDNET;
				}
				else if (this.groupVerbNetPosition != -1 && groupPosition == this.groupVerbNetPosition)
				{
					groupId = GROUPID_VERBNET;
				}
				else if (this.groupPropBankPosition != -1 && groupPosition == this.groupPropBankPosition)
				{
					groupId = GROUPID_PROPBANK;
				}
				else if (this.groupFrameNetPosition != -1 && groupPosition == this.groupFrameNetPosition)
				{
					groupId = GROUPID_FRAMENET;
				}

				// pointer
				final XSelectorPointer pointer = new XSelectorPointer(synsetId, wordId, xId, xClassId, xMemberId, xSources, xMask, groupId);
				Log.d(XSelectorsFragment.TAG, "pointer=" + pointer);

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				this.listener.onItemSelected(pointer, lemma, cased, pos);
			}
		}

		//cursor.close();
		return true;
	}

	// E X P A N D

	/**
	 * Expand section
	 */
	private void expand(int groupPosition)
	{
		final ExpandableListView view = getExpandableListView();
		assert view != null;
		view.expandGroup(groupPosition);
	}

	/**
	 * Expand all
	 */
	@SuppressWarnings("unused")
	private void expandAll()
	{
		final ExpandableListView view = getExpandableListView();
		assert view != null;
		int count = this.xnCursor.getCount();
		for (int position = 0; position < count; position++)
		{
			view.expandGroup(position);
		}
	}

	// H E L P E R

	/**
	 * Extract pos from synset id number
	 *
	 * @param synsetId synset id
	 * @return pos
	 */
	@Nullable
	private String synsetIdToPos(@Nullable final Long synsetId)
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
