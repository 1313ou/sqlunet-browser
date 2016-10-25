package org.sqlunet.browser;

import android.content.ComponentName;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.framenet.browser.FnSentenceActivity;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.style.RegExprSpanner;
import org.sqlunet.style.Spanner.SpanFactory;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.browser.SynsetActivity;

/**
 * TextSearch fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TextSearchFragment extends AbstractTableFragment
{
	private static final String TAG = "TextSearchFragment"; //

	/**
	 * Bold style factory
	 */
	private static final SpanFactory boldFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{/*new BackgroundColorSpan(Colors.dkred), new ForegroundColorSpan(Color.WHITE), */new StyleSpan(Typeface.BOLD)};
		}
	};

	/**
	 * Factories
	 */
	static private final SpanFactory[][] factories = {new SpanFactory[]{boldFactory,}};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public TextSearchFragment()
	{
		super();
	}

	/**
	 * Make view binder
	 *
	 * @return ViewBinder
	 */
	@Override
	protected ViewBinder makeViewBinder()
	{
		// args
		Bundle args = getArguments();
		if (args == null)
		{
			args = getActivity().getIntent().getExtras();
		}

		// search target
		String queryArg = args.getString(SqlUNetContract.ARG_QUERYARG);
		queryArg = queryArg != null ? queryArg.trim() : "";

		// pattern
		final String[] patterns = {'(' + queryArg + ')',};

		// spanner
		final RegExprSpanner spanner = new RegExprSpanner(patterns, factories);

		// view binder
		return new ViewBinder()
		{
			@Override
			public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex)
			{
				String value = cursor.getString(columnIndex);
				if (value == null)
				{
					value = ""; //
				}

				if (view instanceof TextView)
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder(value);
					spanner.setSpan(sb, 0, 0);
					((TextView) view).setText(sb);
				}
				else if (view instanceof ImageView)
				{
					try
					{
						((ImageView) view).setImageResource(Integer.parseInt(value));
					}
					catch (final NumberFormatException nfe)
					{
						((ImageView) view).setImageURI(Uri.parse(value));
					}
				}
				else
				{
					throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter"); //
				}
				return true;
			}
		};
	}

	// C L I C K

	@SuppressWarnings("boxing")
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		Log.d(TAG, "CLICK id=" + id + " pos=" + position);

		// cursor
		final Object item = getListAdapter().getItem(position);
		final Cursor cursor = (Cursor) item;
		dump(cursor);

		if (this.targetIntent != null)
		{
			// intent's classname
			final ComponentName componentName = this.targetIntent.getComponent();
			final String className = componentName.getClassName();
			if (SynsetActivity.class.getName().equals(className)) //
			{
				// target
				final int colIdx = cursor.getColumnIndex("synsetid");
				final long targetId = cursor.getLong(colIdx);
				Log.d(TAG, "CLICK targetid=" + targetId); //

				// build pointer
				final SynsetPointer synsetPointer = new SynsetPointer();
				synsetPointer.setSynset(targetId, null);

				// pass pointer
				this.targetIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_FNSENTENCE);
				this.targetIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, synsetPointer);

				// start
				startActivity(this.targetIntent);
			}
			else if (FnSentenceActivity.class.getName().equals(className)) //
			{
				// target
				final int colIdx = cursor.getColumnIndex("sentenceid");
				final long targetId = cursor.getLong(colIdx);
				Log.d(TAG, "CLICK targetid=" + targetId); //

				// build pointer
				@SuppressWarnings("TypeMayBeWeakened") final FnSentencePointer sentencePointer = new FnSentencePointer(targetId);

				// pass pointer
				this.targetIntent.putExtra(SqlUNetContract.ARG_QUERYACTION, SqlUNetContract.ARG_QUERYACTION_FNSENTENCE);
				this.targetIntent.putExtra(SqlUNetContract.ARG_QUERYPOINTER, sentencePointer);

				// start
				startActivity(this.targetIntent);
			}
		}
	}
}
