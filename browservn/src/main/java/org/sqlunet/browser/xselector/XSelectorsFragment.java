/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.xselector;

import android.content.Context;
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
import android.widget.SimpleCursorTreeAdapter;

import org.sqlunet.browser.BaseSelectorsExpandableListFragment;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.browser.vn.R;
import org.sqlunet.browser.vn.Settings;
import org.sqlunet.loaders.Queries;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.provider.XNetContract;
import org.sqlunet.provider.XNetContract.Words_PbWords_VnWords;
import org.sqlunet.provider.XNetContract.Words_XNet;
import org.sqlunet.provider.XSqlUNetProvider;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

/**
 * X selector fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XSelectorsFragment extends BaseSelectorsExpandableListFragment
{
	static private final String TAG = "XSelectorsF";

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	@FunctionalInterface
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(XSelectorPointer pointer, String word, String cased, String pronunciation, String pos);
	}

	/**
	 * The (saved instance state) key representing the groups state.
	 */
	static private final String STATE_GROUPS = "groups_state";

	/**
	 * Activate on click flag
	 */
	private boolean activateOnItemClick = true;

	/**
	 * id column
	 */
	static private final String GROUPID_COLUMN = "_id";

	/**
	 * Name column
	 */
	static private final String GROUPNAME_COLUMN = "xn";

	/**
	 * Icon column
	 */
	static private final String GROUPICON_COLUMN = "xicon";

	/**
	 * First expanded group
	 */
	static public final int GROUP_POSITION_INITIAL = 0;

	/**
	 * VerbNet position index
	 */
	static public final int GROUPINDEX_VERBNET = 0;

	/**
	 * Propbank position index
	 */
	static public final int GROUPINDEX_PROPBANK = 1;

	/**
	 * VerbNet id for loader
	 */
	static public final int GROUPID_VERBNET = 2;

	/**
	 * Propbank id for loader
	 */
	static public final int GROUPID_PROPBANK = 3;

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
	};

	/**
	 * Target resource
	 */
	static private final String[] childFrom = {Words_XNet.WORDID, //
			Words_XNet.SYNSETID, //
			Words_XNet.XID, //
			Words_XNet.XMEMBERID, //
			Words_XNet.XNAME, //
			Words_XNet.XHEADER, //
			Words_XNet.XINFO, //
			Words_XNet.XDEFINITION,};

	/**
	 * Group positions
	 */
	private int[] groupPositions;

	/**
	 * The current restored group state.
	 */
	@Nullable
	private Integer restoredGroupState;

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
	 * Id view model
	 */
	private SqlunetViewModel wordIdFromWordModel;

	/**
	 * VerbNet model
	 */
	private SqlunetViewModel vnFromWordIdModel;

	/**
	 * PropBank model
	 */
	private SqlunetViewModel pbFromWordIdModel;

	// L I F E C Y C L E

	// --activate--

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

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
		this.wordId = 0;
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		Log.d(TAG, "Lifecycle: onCreateView (3) " + this);
		return inflater.inflate(R.layout.fragment_xselectors, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view0, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view0, savedInstanceState);
		Log.d(TAG, "Lifecycle: onViewCreated (4) " + this);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		final ExpandableListView view = getListView();
		assert view != null;
		view.setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

		// data view models
		makeModels();

		// adapter
		final ExpandableListAdapter adapter = makeAdapter();
		setListAdapter(adapter);
	}

	//	@Override
	//	public void onActivityCreated(@Nullable final Bundle savedInstanceState)
	//	{
	//		super.onActivityCreated(savedInstanceState);
	//		Log.d(TAG, "Lifecycle: onActivityCreated (5) " + this);
	//	}

	@Override
	public void onStart()
	{
		super.onStart();
		Log.d(TAG, "Lifecycle: onStart (6) " + this);

		// load the contents
		// final MutableLiveData<Cursor> idLiveData = wordIdFromWordModel.getMutableData();
		// final Cursor idCursor = idLiveData.getValue();
		// if (idCursor != null && !idCursor.isClosed())
		// {
		//		idLiveData.setValue(idCursor);
		// }
		// else
		load();
	}

	// --deactivate--

	//	@Override
	//	public void onStop()
	//	{
	//		super.onStop();
	//		Log.d(TAG, "Lifecycle: onStop(-4) " + this);
	//	}

	// @Override
	// public void onDestroyView()
	// {
	// 	super.onDestroyView();
	// 	Log.d(TAG, "Lifecycle: onDestroyView (-3) " + this);
	// }

	//	@Override
	//	public void onDestroy()
	//	{
	//		super.onDestroy();
	//		Log.d(TAG, "Lifecycle: onDestroy (-2) " + this);
	//	}

	//	@Override
	//	public void onDetach()
	//	{
	//		super.onDetach();
	//		Log.d(TAG, "Lifecycle: onDetach (-1) " + this);
	//	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		Log.d(TAG, "Lifecycle: onSaveInstanceState (2) " + this);
		super.onSaveInstanceState(outState);

		// serialize and persist the activated group state
		final ExpandableListView expandableListView = getExpandableListView();
		if (expandableListView != null)
		{
			final ExpandableListAdapter adapter = getListAdapter();
			if (adapter != null)
			{
				int groupCount = adapter.getGroupCount();
				int groupState = 0;
				for (int i = 0; i < groupCount; i++)
				{
					if (expandableListView.isGroupExpanded(i))
					{
						groupState |= (1 << i);
					}
				}
				outState.putInt(STATE_GROUPS, groupState);
				Log.d(TAG, "Saved group states " + Integer.toHexString(groupState) + " " + this);
			}
		}
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState)
	{
		super.onViewStateRestored(savedInstanceState);
		Log.d(TAG, "Lifecycle: onViewStateRestored " + this);
		this.restoredGroupState = savedInstanceState != null ? savedInstanceState.getInt(STATE_GROUPS) : null;
	}

	// A D A P T E R

	/**
	 * Make adapter
	 */
	@NonNull
	private ExpandableListAdapter makeAdapter()
	{
		// group cursor populated
		Log.d(TAG, "make group cursor");
		final MatrixCursor groupCursor = new MatrixCursor(new String[]{GROUPID_COLUMN, GROUPNAME_COLUMN, GROUPICON_COLUMN});
		this.groupPositions = populateGroupCursor(requireContext(), groupCursor);
		groupCursor.moveToFirst();

		// adapter
		Log.d(TAG, "Make adapter");
		return new SimpleCursorTreeAdapter(requireContext(), groupCursor, R.layout.item_group_xselector, groupFrom, groupTo, R.layout.item_xselector, childFrom, childTo)
		{
			@Nullable
			@Override
			protected Cursor getChildrenCursor(@NonNull Cursor groupCursor)
			{
				// given the group, return a cursor for all the children within that group
				String groupName = groupCursor.getString(groupCursor.getColumnIndexOrThrow(GROUPNAME_COLUMN));
				Log.d(TAG, "getChildrenCursor(cursor) groupName= " + groupName);

				// get cursor
				Cursor cursor;
				switch (groupName)
				{
					case "verbnet":
						cursor = vnFromWordIdModel.getData().getValue();
						break;
					case "propbank":
						cursor = pbFromWordIdModel.getData().getValue();
						break;
					default:
						throw new IllegalArgumentException("Illegal group name " + groupName);
				}
				if (cursor != null && !cursor.isClosed())
				{
					return cursor;
				}

				// load
				int groupId = groupCursor.getInt(groupCursor.getColumnIndexOrThrow(GROUPID_COLUMN));
				Log.d(TAG, "getChildrenCursor(cursor) groupId=" + groupId);
				startChildLoader(groupId);
				return null; // set later when loader completes
			}

			@Override
			public void onGroupCollapsed(final int groupPosition)
			{
				// super.onGroupCollapsed(groupPosition);
				// prevent from deactivating cursor
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition)
			{
				return true;
			}

			@Nullable
			@Override
			public View getGroupView(final int groupPosition, final boolean isExpanded, final View convertView, final ViewGroup parent)
			{
				Cursor cursor = this.getCursor();
				if (cursor == null)
				{
					return null;
				}
				return super.getGroupView(groupPosition, isExpanded, convertView, parent);
			}
		};
	}

	/**
	 * Populate group cursor. Requires context to be available
	 */
	@NonNull
	private static int[] populateGroupCursor(@NonNull final Context context, @NonNull final MatrixCursor cursor)
	{
		// fill groups
		int position = 0;
		int enable = Settings.getAllPref(context);

		int[] groupPositions = new int[]{AdapterView.INVALID_POSITION, AdapterView.INVALID_POSITION};
		if (Settings.Source.VERBNET.test(enable))
		{
			groupPositions[GROUPINDEX_VERBNET] = position++;
			cursor.addRow(new Object[]{GROUPID_VERBNET, "verbnet", Integer.toString(R.drawable.verbnet)});
		}
		if (Settings.Source.PROPBANK.test(enable))
		{
			//noinspection UnusedAssignment
			groupPositions[GROUPINDEX_PROPBANK] = position++;
			cursor.addRow(new Object[]{GROUPID_PROPBANK, "propbank", Integer.toString(R.drawable.propbank)});
		}
		return groupPositions;
	}

	/**
	 * Initialize groups
	 */
	private void initializeGroups()
	{
		// expand (triggers data loading)
		Log.d(TAG, "expand group " + GROUP_POSITION_INITIAL + " " + this);
		expand(GROUP_POSITION_INITIAL);
	}

	/**
	 * Restore groups
	 */
	public void restoreGroups(@Nullable Integer groupState)
	{
		if (groupState == null || getListAdapter() == null)
		{
			return;
		}
		// final Handler handler = new Handler(Looper.getMainLooper());
		Log.d(TAG, "Restore saved position " + Integer.toHexString(groupState) + " " + this);
		int groupCount = getListAdapter().getGroupCount();
		for (int i = 0; i < groupCount; i++)
		{
			if ((groupState & (1 << i)) != 0)
			{
				expand(i);

				//int groupPosition = i;
				//requireActivity().runOnUiThread(() -> expand(finalI));
				//handler.postDelayed(() -> expand(groupPosition), 1500);
			}
		}
	}

	// V I E W M O D E L S

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		Log.d(TAG, "Make models");

		final LifecycleOwner owner = getViewLifecycleOwner();
		this.wordIdFromWordModel = new ViewModelProvider(this).get("vn:xselectors.wordid(word)", SqlunetViewModel.class);
		this.wordIdFromWordModel.getData().observe(owner, cursor -> {

			if (cursor != null)
			{
				cursor.close();
			}
			this.wordIdFromWordModel.getData().removeObservers(this);

			final ExpandableListAdapter adapter = makeAdapter();
			setListAdapter(adapter);

			if (this.restoredGroupState != null)
			{
				restoreGroups(this.restoredGroupState);
			}
			else
			{
				initializeGroups();
			}
		});

		this.vnFromWordIdModel = new ViewModelProvider(this).get("vn:xselectors.vn(wordid)", SqlunetViewModel.class);
		this.vnFromWordIdModel.getData().observe(owner, cursor -> {

			if (cursor != null && this.vnFromWordIdModel.getData().hasActiveObservers())
			{
				// CursorDump.dumpXCursor(cursor);

				// pass on to list adapter
				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				if (adapter != null && this.groupPositions[GROUPINDEX_VERBNET] != AdapterView.INVALID_POSITION)
				{
					adapter.setChildrenCursor(this.groupPositions[GROUPINDEX_VERBNET], cursor);
				}
			}
			else
			{
				Log.i(TAG, "VN none");
			}
		});

		this.pbFromWordIdModel = new ViewModelProvider(this).get("vn:xselectors.pb(wordid)", SqlunetViewModel.class);
		this.pbFromWordIdModel.getData().observe(owner, cursor -> {

			if (cursor != null && this.pbFromWordIdModel.getData().hasActiveObservers())
			{
				// CursorDump.dumpXCursor(cursor);

				// pass on to list adapter
				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				if (adapter != null && this.groupPositions[GROUPINDEX_PROPBANK] != AdapterView.INVALID_POSITION)
				{
					adapter.setChildrenCursor(this.groupPositions[GROUPINDEX_PROPBANK], cursor);
				}
			}
			else
			{
				Log.i(TAG, "PB none");
			}
		});
	}

	// L O A D

	/**
	 * Load id from word
	 */
	private void load()
	{
		// load the contents
		final Module.ContentProviderSql sql = Queries.prepareWordXSelectVn(XSelectorsFragment.this.word);
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(sql.providerUri));
		this.wordIdFromWordModel.loadData(uri, sql, this::wordIdFromWordPostProcess);
	}

	/**
	 * Post processing, extraction of wordid from cursor
	 * Closes cursor because it's no longer needed.
	 *
	 * @param cursor cursor
	 */
	private void wordIdFromWordPostProcess(@NonNull final Cursor cursor)
	{
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(Words_PbWords_VnWords.WORDID);
			this.wordId = cursor.getLong(idWordId);
		}
		// cursor.close();
	}

	/**
	 * Start child loader for
	 *
	 * @param groupId group id
	 */
	private void startChildLoader(int groupId)
	{
		Log.d(TAG, "Invoking startChildLoader() for groupId=" + groupId);
		switch (groupId)
		{
			case GROUPID_VERBNET:
			{
				//				final MutableLiveData<Cursor> vnLiveData = this.vnFromWordIdModel.getMutableData();
				//				final Cursor vnCursor = vnLiveData.getValue();
				//				if (vnCursor != null && !vnCursor.isClosed())
				//				{
				//					vnLiveData.setValue(vnCursor);
				//				}
				//				else
				loadVn(this.wordId);
			}
			break;

			case GROUPID_PROPBANK:
			{
				//				final MutableLiveData<Cursor> pbLiveData = this.pbFromWordIdModel.getMutableData();
				//				final Cursor pbCursor = pbLiveData.getValue();
				//				if (pbCursor != null && !pbCursor.isClosed())
				//				{
				//					pbLiveData.setValue(pbCursor);
				//				}
				//				else
				loadPb(this.wordId);
			}
			break;

			default:
				break;
		}
	}

	/**
	 * Load VerbNet data
	 *
	 * @param wordId word id
	 */
	private void loadVn(final long wordId)
	{
		final Module.ContentProviderSql sql = Queries.prepareVnXSelectVn(wordId);
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(sql.providerUri));
		this.vnFromWordIdModel.loadData(uri, sql, null);
	}

	/**
	 * Load PropBank data
	 *
	 * @param wordId word id
	 */
	private void loadPb(final long wordId)
	{
		final Module.ContentProviderSql sql = Queries.preparePbSelectVn(wordId);
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(sql.providerUri));
		this.pbFromWordIdModel.loadData(uri, sql, null);
	}

	// L I S T E N E R

	/**
	 * Set listener
	 *
	 * @param listener listener
	 */
	@SuppressWarnings("WeakerAccess")
	public void setListener(final Listener listener)
	{
		this.listener = listener;
	}

	// S E L E C T I O N   L I S T E N E R

	/*
	@Override
	public void onGroupExpand(final int groupPosition)
	{
		super.onGroupExpand(groupPosition);
		Log.d(TAG, "expand " + groupPosition);
	}
	*/

	/*
	@Override
	public void onGroupCollapse(final int groupPosition)
	{
		super.onGroupCollapse(groupPosition);
		Log.d(TAG, "collapse " + groupPosition);
	}
	*/

	// C L I C K

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
	 *
	 * @param activateOnItemClick true if activate
	 */
	@SuppressWarnings("WeakerAccess")
	public void setActivateOnItemClick(@SuppressWarnings("SameParameterValue") final boolean activateOnItemClick)
	{
		this.activateOnItemClick = activateOnItemClick;
	}

	@SuppressWarnings({"boxing", "SameReturnValue"})
	@Override
	public boolean onChildClick(@NonNull final ExpandableListView listView, final View view, final int groupPosition, final int childPosition, final long id)
	{
		super.onChildClick(listView, view, groupPosition, childPosition, id);

		if (this.listener != null)
		{
			//Log.d(TAG, "CLICK on group=" + groupPosition + " child=" + childPosition + " id=" + id);
			int index = listView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
			listView.setItemChecked(index, true);

			final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
			assert adapter != null;
			final Cursor cursor = adapter.getChild(groupPosition, childPosition);
			if (!cursor.isAfterLast())
			{
				// column indices
				final int idSynsetId = cursor.getColumnIndex(XNetContract.Words_XNet.SYNSETID);
				final int idXId = cursor.getColumnIndex(Words_XNet.XID);
				final int idXClassId = cursor.getColumnIndex(Words_XNet.XCLASSID);
				final int idXMemberId = cursor.getColumnIndex(Words_XNet.XMEMBERID);
				final int idXSources = cursor.getColumnIndex(Words_XNet.SOURCES);
				// final int idWordId = cursor.getColumnIndex(Words_XNet.WORDID);

				// data
				final long wordId = this.wordId;
				assert this.word != null;
				final String word = this.word;
				final String cased = this.word.equals(this.word.toLowerCase(Locale.ENGLISH)) ? null : this.word;
				final String pronunciation = null;
				final long synsetId = cursor.isNull(idSynsetId) ? 0 : cursor.getLong(idSynsetId);
				final String pos = synsetIdToPos(synsetId);
				final long xId = cursor.isNull(idXId) ? 0 : cursor.getLong(idXId);
				final long xClassId = cursor.isNull(idXClassId) ? 0 : cursor.getLong(idXClassId);
				final long xMemberId = cursor.isNull(idXMemberId) ? 0 : cursor.getLong(idXMemberId);
				final String xSources = cursor.getString(idXSources);
				final long xMask = XSelectorPointer.getMask(xSources);

				if (groupPosition == AdapterView.INVALID_POSITION)
				{
					return false;
				}

				int groupId = -1;
				if (groupPosition == this.groupPositions[GROUPINDEX_VERBNET])
				{
					groupId = GROUPID_VERBNET;
				}
				else if (groupPosition == this.groupPositions[GROUPINDEX_PROPBANK])
				{
					groupId = GROUPID_PROPBANK;
				}

				// pointer
				final XSelectorPointer pointer = new XSelectorPointer(synsetId, wordId, xId, xClassId, xMemberId, xSources, xMask, groupId);
				Log.d(TAG, "pointer=" + pointer);

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				//noinspection ConstantConditions
				this.listener.onItemSelected(pointer, word, cased, pronunciation, pos);
			}
			// cursor ownership is transferred  to adapter, so do not call
			// cursor.close();
		}
		return true;
	}

	// E X P A N D

	/**
	 * Expand all
	 */
	private void expandAll()
	{
		final ExpandableListView expandableListView = getExpandableListView();
		final ExpandableListAdapter adapter = getListAdapter();
		if (expandableListView == null || adapter == null)
		{
			return;
		}

		int count = adapter.getGroupCount();
		for (int position = 0; position < count; position++)
		{
			expandableListView.expandGroup(position);
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