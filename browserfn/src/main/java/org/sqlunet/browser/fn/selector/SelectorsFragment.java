/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.fn.selector;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.sqlunet.Pointer;
import org.sqlunet.browser.BaseSelectorsListFragment;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.fn.R;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.framenet.loaders.Queries;
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_or_Frames;
import org.sqlunet.framenet.provider.FrameNetProvider;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.Colors;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Selector Fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class SelectorsFragment extends BaseSelectorsListFragment
{
	static private final String TAG = "SelectorsF";

	// L I F E C Y C L E

	// --constructor--

	public SelectorsFragment()
	{
		this.layoutId = R.layout.fragment_selectors;
		this.viewModelKey = "fn:selectors(word)";
	}

	// --activate--

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Lifecycle: onCreate (2) " + this + " " + savedInstanceState);

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
	}

	// A D A P T E R

	@Override
	@NonNull
	protected CursorAdapter makeAdapter()
	{
		Log.d(TAG, "Make adapter");
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
				final int idIsLike = cursor.getColumnIndex(Queries.ISLIKE);
				final int idName = cursor.getColumnIndex(LexUnits_or_Frames.NAME);

				String text = cursor.getString(columnIndex);
				if (text == null || "0".equals(text))
				{
					view.setVisibility(View.GONE);
					return true;
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

	// L O A D

	@Override
	protected void load()
	{
		// load the contents
		final Module.ContentProviderSql sql = Queries.prepareSelect(SelectorsFragment.this.word);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.dataModel.loadData(uri, sql, null);
	}

	// C L I C K   L I S T E N E R

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
	 * The fragment's current callback, which is notified of list item clicks.
	 */
	private Listener listener;

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

	@Override
	protected void activate(int position)
	{
		this.positionModel.setPosition(position);

		if (this.listener != null)
		{
			final SimpleCursorAdapter adapter = (SimpleCursorAdapter) this.adapter;
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
