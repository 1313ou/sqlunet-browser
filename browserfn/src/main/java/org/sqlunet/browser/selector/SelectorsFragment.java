/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.selector;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.sqlunet.Pointer;
import org.sqlunet.browser.PositionViewModel;
import org.sqlunet.browser.SqlunetViewModel;
import org.sqlunet.browser.fn.R;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_or_Frames;
import org.sqlunet.framenet.provider.FrameNetProvider;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.Colors;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * Selector Fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class SelectorsFragment extends ListFragment
{
	static private final String TAG = "SelectorsF";

	private static final String ISLIKE = "islike";

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
	 */
	@FunctionalInterface
	public interface Listener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(Pointer pointer, String word, long wordId);
	}

	/**
	 * Activate on click flag
	 */
	private boolean activateOnItemClick = true;

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
	 * Data view model
	 */
	private SqlunetViewModel dataModel;

	/**
	 * Position view model
	 */
	private PositionViewModel positionModel;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public SelectorsFragment()
	{
		Log.d(TAG, "lifecycle: Constructor (0) " + this);
	}

	// L I F E C Y C L E

	// --activate--

	//	@Override
	//	public void onAttach(@NonNull final Context context)
	//	{
	//		super.onAttach(context);
	//		Log.d(TAG, "lifecycle: onAttach (1) " + this);
	//	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "lifecycle: onCreate (2) " + this);
		//noinspection deprecation
		this.setRetainInstance(false); // default

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
		ListAdapter adapter = makeAdapter();
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		Log.d(TAG, "lifecycle: onCreateView (3) " + this);
		return inflater.inflate(R.layout.fragment_selectors, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "lifecycle: onViewCreated (4) " + this);

		// when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
		getListView().setChoiceMode(this.activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);

		// data view models
		Log.d(TAG, "make models");
		makeModels();
	}

	//	@Override
	//	public void onActivityCreated(@Nullable final Bundle savedInstanceState)
	//	{
	//		super.onActivityCreated(savedInstanceState);
	//		Log.d(TAG, "lifecycle: onActivityCreated (5) " + this);
	//	}

	@Override
	public void onStart()
	{
		super.onStart();

		Log.d(TAG, "lifecycle: onStart (6) " + this);

		// load the contents
		// final MutableLiveData<Cursor> idLiveData = dataModel.getMutableData();
		//  final Cursor idCursor = idLiveData.getValue();
		//  if (idCursor != null && !idCursor.isClosed())
		//  {
		//   	idLiveData.setValue(idCursor);
		//  }
		//  else
		load();
	}

	// --deactivate--

	//	@Override
	//	public void onStop()
	//	{
	//		super.onStop();
	//		Log.d(TAG, "lifecycle: onStop(-4) " + this);
	//	}

	//	@Override
	//	public void onDestroyView()
	//	{
	//		super.onDestroyView();
	//		Log.d(TAG, "lifecycle: onDestroyView (-3) " + this);
	//	}

	//	@Override
	//	public void onDestroy()
	//	{
	//		super.onDestroy();
	//		Log.d(TAG, "lifecycle: onDestroy (-2) " + this);
	//	}

	//	@Override
	//	public void onDetach()
	//	{
	//		super.onDetach();
	//		Log.d(TAG, "lifecycle: onDetach (-1) " + this);
	//	}

	// H E L P E R S

	/**
	 * Make adapter
	 */
	@NonNull
	private ListAdapter makeAdapter()
	{
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(requireContext(), R.layout.item_selector, null, //
				new String[]{ //
						LexUnits_or_Frames.NAME, LexUnits_or_Frames.FRAMENAME, LexUnits_or_Frames.WORD, LexUnits_or_Frames.FNID, LexUnits_or_Frames.FNWORDID, LexUnits_or_Frames.WORDID, LexUnits_or_Frames.FRAMEID, LexUnits_or_Frames.ISFRAME, //
				}, //
				new int[]{ //
						R.id.fnname, R.id.fnframename, R.id.fnword, R.id.fnid, R.id.fnwordid, R.id.wordid, R.id.fnframeid, R.id.icon, //
				}, 0);

		adapter.setViewBinder((view, cursor, columnIndex) -> {

			if (view instanceof TextView)
			{
				final int idIsLike = cursor.getColumnIndex(ISLIKE);
				final int idName = cursor.getColumnIndex(LexUnits_or_Frames.NAME);

				String text = cursor.getString(columnIndex);
				if (text == null || "0".equals(text))
				{
					view.setVisibility(View.GONE);
					return false;
				}
				else
				{
					view.setVisibility(View.VISIBLE);
				}
				final TextView textView = (TextView) view;
				textView.setText(text);

				if (idName == columnIndex)
				{
					boolean isLike = cursor.getInt(idIsLike) != 0;
					textView.setTextColor(isLike ? Colors.textDimmedForeColor : Colors.textNormalForeColor);
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
			}
		});
		return adapter;
	}

	// V I E W M O D E L S

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		// data model
		this.dataModel = new ViewModelProvider(this).get("fn:selectors(word)", SqlunetViewModel.class);
		this.dataModel.getData().observe(getViewLifecycleOwner(), cursor -> {

			// pass on to list adapter
			final CursorAdapter adapter = (CursorAdapter) getListAdapter();
			assert adapter != null;
			adapter.swapCursor(cursor);
		});

		// position model
		this.positionModel = new ViewModelProvider(this).get(PositionViewModel.class);
		this.positionModel.getPositionLiveData().observe(getViewLifecycleOwner(), (position) -> {

			Log.d(TAG, "Observed position change " + position);
			getListView().setItemChecked(position, position != AdapterView.INVALID_POSITION);
		});
		this.positionModel.setPosition(AdapterView.INVALID_POSITION);
	}

	// L O A D

	/**
	 * Load data from word
	 */
	private void load()
	{
		// load the contents
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(LexUnits_or_Frames.CONTENT_URI_TABLE_FN));
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
		this.dataModel.loadData(uri, projection, selection, selectionArgs, sortOrder, null);
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

	@Override
	public void onListItemClick(@NonNull final ListView listView, @NonNull final View view, final int position, final long id)
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
		this.positionModel.setPosition(position);

		if (this.listener != null)
		{
			final SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
			assert adapter != null;
			final Cursor cursor = adapter.getCursor();
			assert cursor != null;
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
