/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.xselector;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import org.sqlunet.browser.R;
import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.browser.xn.Settings;
import org.sqlunet.provider.CursorDump;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.provider.XSqlUNetContract;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XSqlUNetContract.Words_XNet_U;
import org.sqlunet.provider.XSqlUNetProvider;
import org.sqlunet.wordnet.provider.WordNetContract;
import org.sqlunet.wordnet.provider.WordNetProvider;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.app.local.ExpandableListFragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * X selector fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XSelectorsFragment extends ExpandableListFragment
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
		void onItemSelected(XSelectorPointer pointer, String word, String cased, String pos);
	}

	/**
	 * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
	 */
	static private final String STATE_GROUP = "group_state";

	/**
	 * Activate on click flag
	 */
	private boolean activateOnItemClick = false;

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
	 * WordNet position
	 */
	static public final int GROUP_POSITION_WORDNET = 0;

	/**
	 * VerbNet position
	 */
	static public final int GROUP_POSITION_VERBNET = 1;

	/**
	 * Propbank position
	 */
	static public final int GROUP_POSITION_PROPBANK = 2;

	/**
	 * FrameNet position
	 */
	static public final int GROUP_POSITION_FRAMENET = 3;

	/**
	 * Group position
	 */
	static public final int GROUP_POSITION_COUNT = 4;

	/**
	 * WordNet id
	 */
	static public final int GROUPID_WORDNET = GROUP_POSITION_WORDNET + 1;

	/**
	 * VerbNet id
	 */
	static public final int GROUPID_VERBNET = GROUP_POSITION_VERBNET + 1;

	/**
	 * Propbank id
	 */
	static public final int GROUPID_PROPBANK = GROUP_POSITION_PROPBANK + 1;

	/**
	 * FrameNet id
	 */
	static public final int GROUPID_FRAMENET = GROUP_POSITION_FRAMENET + 1;

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
	private final MatrixCursor xnCursor = new MatrixCursor(new String[]{GROUPID_COLUMN, GROUPNAME_COLUMN, GROUPICON_COLUMN});

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
	 * The current group state.
	 */
	private int groupState;

	/**
	 * The current restored group state.
	 */
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
	 * Model
	 */
	private SqlunetViewModel wordIdFromWordModel;

	/**
	 * WordNet model
	 */
	private SqlunetViewModel wnFromWordIdModel;

	/**
	 * VerbNet model
	 */
	private SqlunetViewModel vnFromWordIdModel;

	/**
	 * PropBank model
	 */
	private SqlunetViewModel pbFromWordIdModel;

	/**
	 * FrameNet model
	 */
	private SqlunetViewModel fnFromWordIdModel;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public XSelectorsFragment()
	{
		Log.d(TAG, "lifecycle: Constructor " + this);
		this.groupWordNetPosition = AdapterView.INVALID_POSITION;
		this.groupVerbNetPosition = AdapterView.INVALID_POSITION;
		this.groupPropBankPosition = AdapterView.INVALID_POSITION;
		this.groupFrameNetPosition = AdapterView.INVALID_POSITION;
		this.groupState = 0;
	}

	// L I F E C Y C L E   E V E N T S

	@Override
	public void onAttach(@NonNull final Context context)
	{
		super.onAttach(context);
		Log.d(TAG, "lifecycle: onAttach (1) " + this);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "lifecycle: onCreate (2) " + this);
		this.setRetainInstance(false);

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

		// group cursor
		makeGroupCursor();
		this.groupPosition = 0;
		Log.d(TAG, "onCreate: groupCursor at groupPosition " + this.groupPosition);

		// data view models
		makeModels();
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		Log.d(TAG, "lifecycle: onCreateView (3) " + this);
		return inflater.inflate(R.layout.fragment_xselectors, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view0, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view0, savedInstanceState);
		Log.d(TAG, "lifecycle: onViewCreated (4) " + this);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		final ExpandableListView view = getListView();
		assert view != null;
		view.setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

		// load the contents (once activity is available)
		//		final MutableLiveData<Cursor> idLiveData = wordIdFromWordModel.getMutableData();
		//		final Cursor idCursor = idLiveData.getValue();
		//		if (idCursor != null && !idCursor.isClosed())
		//		{
		//			idLiveData.setValue(idCursor);
		//		}
		//		else
		{
			load();
		}
	}

	@Override
	public void onActivityCreated(@Nullable final Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "lifecycle: onActivityCreated (5) " + this);
	}

	@Override
	public void onStop()
	{
		super.onStop();
		Log.d(TAG, "lifecycle: onStop (1) " + this);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		Log.d(TAG, "lifecycle: onDestroyView (3) " + this);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "lifecycle: onDestroy (4) " + this);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		Log.d(TAG, "lifecycle: onDetach (5) " + this);
	}

	public void clear()
	{
		View view = getExpandableListView();
		ViewGroup container = (ViewGroup) view.getParent();
		container.removeView(view);

		CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
		adapter.setChildrenCursor(0, null);
		adapter.setChildrenCursor(1, null);
		adapter.setChildrenCursor(2, null);
		adapter.setChildrenCursor(3, null);
		adapter.setGroupCursor(null);
		setListAdapter(null);
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		Log.d(TAG, "lifecycle: onSaveInstanceState (2) " + this);
		//clear();
		super.onSaveInstanceState(outState);

		// serialize and persist the activated item position.
		outState.putInt(STATE_GROUP, this.groupState);
		Log.d(TAG, "save group states " + Integer.toHexString(this.groupState) + " " + this);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState)
	{
		super.onViewStateRestored(savedInstanceState);
		Log.d(TAG, "lifecycle: onViewStateRestored " + this);
		if (savedInstanceState != null)
		{
			restoredGroupState = savedInstanceState.getInt(STATE_GROUP);
		}
		else
		{
			restoredGroupState = null;
		}
	}

	/**
	 * Initialize groups
	 */
	private void initializeGroups()
	{
		// expand (triggers data loading)
		Log.d(TAG, "expand group " + this.groupPosition + " " + this);
		expand(this.groupPosition);
	}

	/**
	 * Restore groups
	 */
	public void restoreGroups(@Nullable Integer groupState)
	{
		if (groupState == null)
		{
			return;
		}
		final Handler handler = new Handler(Looper.getMainLooper());
		Log.d(TAG, "saved position " + Integer.toHexString(groupState) + " " + this);
		for (int i = 0; i < GROUP_POSITION_COUNT; i++)
		{
			if ((groupState & (1 << i)) != 0)
			{
				// expand(i);

				int finalI = i;
				//requireActivity().runOnUiThread(() -> expand(finalI));
				handler.postDelayed(() -> expand(finalI), 1500);
			}
		}
	}

	// H E L P E R S

	private void makeGroupCursor()
	{
		// fill groups
		int position = 0;
		int enable = Settings.getAllPref(requireContext());

		if (Settings.Source.WORDNET.test(enable))
		{
			this.groupWordNetPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_WORDNET, "wordnet", Integer.toString(R.drawable.wordnet)});
		}
		if (Settings.Source.VERBNET.test(enable))
		{
			this.groupVerbNetPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_VERBNET, "verbnet", Integer.toString(R.drawable.verbnet)});
		}
		if (Settings.Source.PROPBANK.test(enable))
		{
			this.groupPropBankPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_PROPBANK, "propbank", Integer.toString(R.drawable.propbank)});
		}
		if (Settings.Source.FRAMENET.test(enable))
		{
			//noinspection UnusedAssignment
			this.groupFrameNetPosition = position++;
			this.xnCursor.addRow(new Object[]{GROUPID_FRAMENET, "framenet", Integer.toString(R.drawable.framenet)});
		}
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.wordIdFromWordModel = new ViewModelProvider(this).get("xselectors.wordid(word)", SqlunetViewModel.class);
		this.wordIdFromWordModel.getData().observe(this, unusedCursor -> {

			unusedCursor.close();

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

		this.wnFromWordIdModel = new ViewModelProvider(this).get("xselectors.wn(wordid)", SqlunetViewModel.class);
		this.wnFromWordIdModel.getData().observe(this, cursor -> {

			if (cursor != null)
			{
				// dumpXCursor(cursor);

				// pass on to list adapter
				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				if (adapter != null && this.groupPosition >= 0)
				{
					adapter.setChildrenCursor(this.groupPosition, cursor);
				}
			}
			else
			{
				Log.i(TAG, "WN none");
			}
		});

		this.vnFromWordIdModel = new ViewModelProvider(this).get("xselectors.vn(wordid)", SqlunetViewModel.class);
		this.vnFromWordIdModel.getData().observe(this, cursor -> {

			if (cursor != null)
			{
				// dumpXCursor(cursor);

				// pass on to list adapter
				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				if (adapter != null && this.groupPosition >= 0)
				{
					adapter.setChildrenCursor(this.groupPosition, cursor);
				}
			}
			else
			{
				Log.i(TAG, "VN none");
			}
		});

		this.pbFromWordIdModel = new ViewModelProvider(this).get("xselectors.pb(wordid)", SqlunetViewModel.class);
		this.pbFromWordIdModel.getData().observe(this, cursor -> {

			if (cursor != null)
			{
				// dumpXCursor(cursor);

				// pass on to list adapter
				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				if (adapter != null && this.groupPosition >= 0)
				{
					adapter.setChildrenCursor(this.groupPosition, cursor);
				}
			}
			else
			{
				Log.i(TAG, "PB none");
			}
		});

		this.fnFromWordIdModel = new ViewModelProvider(this).get("xselectors.fn(wordid)", SqlunetViewModel.class);
		this.fnFromWordIdModel.getData().observe(this, cursor -> {

			if (cursor != null)
			{
				// dumpXCursor(cursor);

				// pass on to list adapter
				final CursorTreeAdapter adapter = (CursorTreeAdapter) getListAdapter();
				if (adapter != null && this.groupPosition >= 0)
				{
					adapter.setChildrenCursor(this.groupPosition, cursor);
				}
			}
			else
			{
				Log.i(TAG, "FN none");
			}
		});
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
		this.wordIdFromWordModel.loadData(uri, projection, selection, selectionArgs, sortOrder, this::xselectorsPostProcess);
	}

	/**
	 * Read wordId from cursor
	 *
	 * @param cursor cursor
	 */
	private void xselectorsPostProcess(@NonNull final Cursor cursor)
	{
		// store source
		if (cursor.moveToFirst())
		{
			final int idWordId = cursor.getColumnIndex(Words_FnWords_PbWords_VnWords.WORDID);
			XSelectorsFragment.this.wordId = cursor.getLong(idWordId);
		}
	}

	/**
	 * Make adapter
	 */
	private ExpandableListAdapter makeAdapter()
	{
		Log.d(TAG, "make adapter");

		// adapter
		this.xnCursor.moveToFirst();
		return new SimpleCursorTreeAdapter(requireContext(), this.xnCursor, R.layout.item_group_xselector, groupFrom, groupTo, R.layout.item_xselector, childFrom, childTo)
		{
			@Nullable
			@Override
			protected Cursor getChildrenCursor(@NonNull Cursor groupCursor)
			{
				Log.d(TAG, "getChildrenCursor(cursor) query groupId");

				// given the group, return a cursor for all the children within that group
				int groupPosition = groupCursor.getPosition();
				int groupId = groupCursor.getInt(groupCursor.getColumnIndex(GROUPID_COLUMN));
				String groupName = groupCursor.getString(groupCursor.getColumnIndex(GROUPNAME_COLUMN));
				Log.d(TAG, "getChildrenCursor(cursor) group " + groupPosition + ' ' + groupName);

				// load
				startChildLoader(groupId);
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
				int id = v.getId();
				if (R.id.xsources == id)
				{
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
				}
				else if (R.id.pm == id)
				{
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
				}
				else
				{
					super.setViewImage(v, value);
				}
			}

			@Override
			protected void bindChildView(final View view, final Context context, final Cursor cursor, final boolean isLastChild)
			{
				try
				{
					super.bindChildView(view, context, cursor, isLastChild);
				}
				catch (Exception e)
				{
					Log.e(TAG, "bindChildView() adapter=" + this + " cursor=" + cursor, e);
					CursorDump.dump(cursor);
					throw e;
				}
			}

			@Override
			protected void bindGroupView(final View view, final Context context, final Cursor cursor, final boolean isExpanded)
			{
				try
				{
					super.bindGroupView(view, context, cursor, isExpanded);
				}
				catch (Exception e)
				{
					Log.e(TAG, "bindGroupView() adapter=" + this + " cursor=" + cursor, e);
					CursorDump.dump(cursor);
					throw e;
				}
			}
		};
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
			case GROUPID_WORDNET:
			{
				//				final MutableLiveData<Cursor> wnLiveData = this.wnFromWordIdModel.getMutableData();
				//				final Cursor wnCursor = wnLiveData.getValue();
				//				if (wnCursor != null && !wnCursor.isClosed())
				//				{
				//					wnLiveData.setValue(wnCursor);
				//				}
				//				else
				{
					loadWn(this.wordId);
				}
			}
			break;

			case GROUPID_VERBNET:
			{
				//				final MutableLiveData<Cursor> vnLiveData = this.vnFromWordIdModel.getMutableData();
				//				final Cursor vnCursor = vnLiveData.getValue();
				//				if (vnCursor != null && !vnCursor.isClosed())
				//				{
				//					vnLiveData.setValue(vnCursor);
				//				}
				//				else
				{
					loadVn(this.wordId);
				}
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
				{
					loadPb(this.wordId);
				}
			}
			break;

			case GROUPID_FRAMENET:
			{
				//				final MutableLiveData<Cursor> fnLiveData = this.fnFromWordIdModel.getMutableData();
				//				final Cursor fnCursor = fnLiveData.getValue();
				//				if (fnCursor != null && !fnCursor.isClosed())
				//				{
				//					fnLiveData.setValue(fnCursor);
				//				}
				//				else
				{
					loadFn(this.wordId);
				}
			}
			break;

			default:
				break;
		}
	}

	// L O A D

	/**
	 * Load WordNet data
	 *
	 * @param wordId word id
	 */
	private void loadWn(final long wordId)
	{
		Log.d(TAG, "loadWn " + wordId);
		final Uri uri = Uri.parse(WordNetProvider.makeUri(WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.CONTENT_URI_TABLE));
		final String[] projection = { //
				"'wn' AS " + Words_XNet_U.SOURCES, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS " + Words_XNet_U.XID, //
				"NULL AS " + Words_XNet_U.XCLASSID, //
				"NULL AS " + Words_XNet_U.XMEMBERID, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEMMA + "|| '.' ||" + WordNetContract.POS + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.POS + " AS " + Words_XNet_U.XNAME, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.LEXDOMAIN + " AS " + Words_XNet_U.XHEADER, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SENSEKEY + " AS " + Words_XNet_U.XINFO, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
				WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.SYNSETID + " AS _id"};
		final String selection = WordNetContract.Words_Senses_CasedWords_Synsets_PosTypes_LexDomains.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		this.wnFromWordIdModel.loadData(uri, projection, selection, selectionArgs, null, null);
	}

	/**
	 * Load VerbNet data
	 *
	 * @param wordId word id
	 */
	private void loadVn(final long wordId)
	{
		Log.d(TAG, "loadVn " + wordId);
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(XSqlUNetContract.Words_VnWords_VnClasses_U.CONTENT_URI_TABLE));
		final String[] projection = { //
				XSqlUNetContract.Words_VnWords_VnClasses_U.WORDID, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.SYNSETID, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID + " AS " + Words_XNet_U.XID, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID + " AS " + Words_XNet_U.XCLASSID, //
				"NULL AS " + Words_XNet_U.XMEMBERID, //
				"TRIM(" + XSqlUNetContract.Words_VnWords_VnClasses_U.CLASS + ",'-.0123456789')" + " AS " + Words_XNet_U.XNAME, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASS + " AS " + Words_XNet_U.XHEADER, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSTAG + " AS " + Words_XNet_U.XINFO, //
				XSqlUNetContract.Words_VnWords_VnClasses_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
				"rowid AS _id",};
		final String selection = XSqlUNetContract.Words_VnWords_VnClasses_U.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		final String sortOrder = XSqlUNetContract.Words_VnWords_VnClasses_U.CLASSID;
		this.vnFromWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
	}

	/**
	 * Load PropBank data
	 *
	 * @param wordId word id
	 */
	private void loadPb(final long wordId)
	{
		Log.d(TAG, "loadPb " + wordId);
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(XSqlUNetContract.Words_PbWords_PbRoleSets_U.CONTENT_URI_TABLE));
		final String[] projection = { //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.WORDID, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.SYNSETID, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETID + " AS " + Words_XNet_U.XID, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETID + " AS " + Words_XNet_U.XCLASSID, //
				"NULL AS " + Words_XNet_U.XMEMBERID, //
				"TRIM(" + XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETNAME + ",'.0123456789')" + " AS " + Words_XNet_U.XNAME, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETNAME + " AS " + Words_XNet_U.XHEADER, //
				//Words_PbWords_PbRoleSets_U.ROLESETHEAD + " AS " + Words_XNet_U.XHEADER, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETDESCR + " AS " + Words_XNet_U.XINFO, //
				XSqlUNetContract.Words_PbWords_PbRoleSets_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
				"rowid AS _id",};
		final String selection = XSqlUNetContract.Words_PbWords_PbRoleSets_U.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		final String sortOrder = XSqlUNetContract.Words_PbWords_PbRoleSets_U.ROLESETID;
		this.pbFromWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
	}

	/**
	 * Load FrameNet data
	 *
	 * @param wordId word id
	 */
	private void loadFn(final long wordId)
	{
		Log.d(TAG, "loadFn " + wordId);
		final Uri uri = Uri.parse(XSqlUNetProvider.makeUri(XSqlUNetContract.Words_FnWords_FnFrames_U.CONTENT_URI_TABLE));
		final String[] projection = { //
				XSqlUNetContract.Words_FnWords_FnFrames_U.WORDID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.SYNSETID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.FRAMEID + " AS " + Words_XNet_U.XID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.FRAMEID + " AS " + Words_XNet_U.XCLASSID, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.LUID + " AS " + Words_XNet_U.XMEMBERID, //
				"GROUP_CONCAT(" + XSqlUNetContract.Words_FnWords_FnFrames_U.LEXUNIT + ",'\n')" + " AS " + Words_XNet_U.XNAME, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.FRAME + " AS " + Words_XNet_U.XHEADER, //
				"GROUP_CONCAT(" + XSqlUNetContract.Words_FnWords_FnFrames_U.LUDEFINITION + ",'\n') AS " + Words_XNet_U.XINFO, //
				XSqlUNetContract.Words_FnWords_FnFrames_U.DEFINITION + " AS " + Words_XNet_U.XDEFINITION, //
				"rowid AS _id",};
		final String selection = XSqlUNetContract.Words_FnWords_FnFrames_U.WORDID + " = ?";
		final String[] selectionArgs = {Long.toString(wordId)};
		final String sortOrder = XSqlUNetContract.Words_FnWords_FnFrames_U.LUID + ' ' + "IS NULL" + ',' + XSqlUNetContract.Words_FnWords_FnFrames_U.SOURCES + ',' + XSqlUNetContract.Words_FnWords_FnFrames_U.FRAMEID;
		this.fnFromWordIdModel.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
	}

	// S E L E C T I O N   L I S T E N E R

	@Override
	public void onGroupExpand(final int groupPosition)
	{
		super.onGroupExpand(groupPosition);
		this.groupPosition = groupPosition;
		this.groupState |= 1 << groupPosition;
		Log.d(TAG, "expand " + groupPosition);
	}

	@Override
	public void onGroupCollapse(final int groupPosition)
	{
		super.onGroupCollapse(groupPosition);
		this.groupPosition = -1;
		this.groupState &= ~(1 << groupPosition);
		Log.d(TAG, "collapse " + groupPosition);
	}

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

			final CursorTreeAdapter adapter = (SimpleCursorTreeAdapter) getListAdapter();
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
				if (this.groupWordNetPosition != AdapterView.INVALID_POSITION && groupPosition == this.groupWordNetPosition)
				{
					groupId = GROUPID_WORDNET;
				}
				else if (this.groupVerbNetPosition != AdapterView.INVALID_POSITION && groupPosition == this.groupVerbNetPosition)
				{
					groupId = GROUPID_VERBNET;
				}
				else if (this.groupPropBankPosition != AdapterView.INVALID_POSITION && groupPosition == this.groupPropBankPosition)
				{
					groupId = GROUPID_PROPBANK;
				}
				else if (this.groupFrameNetPosition != AdapterView.INVALID_POSITION && groupPosition == this.groupFrameNetPosition)
				{
					groupId = GROUPID_FRAMENET;
				}

				// pointer
				final XSelectorPointer pointer = new XSelectorPointer(synsetId, wordId, xId, xClassId, xMemberId, xSources, xMask, groupId);
				Log.d(TAG, "pointer=" + pointer);

				// notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
				this.listener.onItemSelected(pointer, lemma, cased, pos);
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
		if (getExpandableListAdapter() == null)
		{
			return;
		}

		final ExpandableListView view = getExpandableListView();
		assert view != null;
		int count = this.xnCursor.getCount();
		for (int position = 0; position < count; position++)
		{
			view.expandGroup(position);
		}
	}

	/**
	 * Expand or Collapse section
	 */
	private void expandOrCollapse(int groupPosition, boolean expand)
	{
		if (getExpandableListAdapter() == null)
		{
			return;
		}

		final ExpandableListView view = getExpandableListView();
		assert view != null;
		boolean expanded = view.isGroupExpanded(groupPosition);
		Log.d(TAG, "Expanded " + expanded);
		if (expand && !expanded)
		{
			Log.d(TAG, "Expand");
			expand(groupPosition);
		}
		else if (!expand && expanded)
		{
			Log.d(TAG, "Collapse");
			collapse(groupPosition);
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