package org.sqlunet.browser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.framenet.provider.FrameNetContract;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.propbank.provider.PropBankContract;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.style.RegExprSpanner;
import org.sqlunet.style.Spanner.SpanFactory;
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.verbnet.provider.VerbNetContract;
import org.sqlunet.wordnet.SynsetPointer;
import org.sqlunet.wordnet.WordPointer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * TextSearch progressMessage fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TextFragment extends AbstractTableFragment
{
	static private final String TAG = "TSResultFragment";

	/**
	 * Bold style factory
	 */
	static private final SpanFactory boldFactory = new SpanFactory()
	{
		@Override
		public Object makeSpans(final long flags)
		{
			return new Object[]{/*new BackgroundColorSpan(Colors.dk_red), new ForegroundColorSpan(Color.WHITE), */new StyleSpan(Typeface.BOLD)};
		}
	};

	/**
	 * Factories
	 */
	static private final SpanFactory[][] factories = {new SpanFactory[]{boldFactory,}};

	/**
	 * Query argument
	 */
	private String query;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public TextFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// args
		Bundle args = getArguments();
		assert args != null;

		// search target
		// final String database = args.getString(ProviderArgs.ARG_QUERYDATABASE);
		String queryArg = args.getString(ProviderArgs.ARG_QUERYARG);
		this.query = queryArg != null ? queryArg.trim() : "";

		super.onCreate(savedInstanceState);
	}

	/**
	 * Make view binder
	 *
	 * @return ViewBinder
	 */
	@Override
	protected ViewBinder makeViewBinder()
	{
		// pattern (case-insensitive)
		final String[] patterns = {'(' + "(?i)" + this.query + ')',};

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
					value = "";
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
					throw new IllegalStateException(view.getClass().getName() + " is not a view that can be bound by this SimpleCursorAdapter");
				}
				return true;
			}
		};
	}

	// C L I C K

	@Override
	public void onListItemClick(final ListView listView, final View view, final int position, final long id)
	{
		super.onListItemClick(listView, view, position, id);

		Log.d(TAG, "CLICK id=" + id + " pos=" + position);

		// cursor
		final Object item = getListAdapter().getItem(position);
		final Cursor cursor = (Cursor) item;

		// args
		Bundle args = getArguments();
		assert args != null;

		// search target
		final String database = args.getString(ProviderArgs.ARG_QUERYDATABASE);
		if (database != null)
		{
			// wordnet
			if ("wn".equals(database))
			{
				String subtarget = args.getString(ProviderArgs.ARG_QUERYIDTYPE);
				if ("synset".equals(subtarget))
				{
					// target
					final int colIdx = cursor.getColumnIndex("synsetid");
					final long targetId = cursor.getLong(colIdx);
					Log.d(TAG, "CLICK wn synset=" + targetId);

					// build pointer
					final Parcelable synsetPointer = new SynsetPointer(targetId);

					// intent
					final Intent targetIntent = new Intent(this.getActivity(), org.sqlunet.wordnet.browser.SynsetActivity.class);
					targetIntent.setAction(ProviderArgs.ACTION_QUERY);
					targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET);
					targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, synsetPointer);

					// start
					startActivity(targetIntent);
				}
				else if ("lemma".equals(subtarget))
				{
					// target
					final int colIdx = cursor.getColumnIndex("wordid");
					final long targetId = cursor.getLong(colIdx);
					Log.d(TAG, "CLICK wn word=" + targetId);

					// build pointer
					final Parcelable wordPointer = new WordPointer(targetId);

					// intent
					final Intent targetIntent = new Intent(this.getActivity(), org.sqlunet.wordnet.browser.WordActivity.class);
					targetIntent.setAction(ProviderArgs.ACTION_QUERY);
					targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD);
					targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, wordPointer);

					// start
					startActivity(targetIntent);
				}
			}
			else if ("vn".equals(database)) //
			{
				final int idClasses = cursor.getColumnIndex(VerbNetContract.Lookup_VnExamples_X.CLASSES);
				final String classes = cursor.getString(idClasses);
				Log.d(TAG, "CLICK vn classes=" + classes);

				final Pair<TypedPointer[], CharSequence[]> result = makeData(classes);
				if (result.first.length > 1)
				{
					final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(final DialogInterface dialog, int which)
						{
							// which argument contains the index position of the selected item
							final TypedPointer typedPointer = result.first[which];
							startVn(typedPointer);
						}
					};

					final AlertDialog dialog = makeDialog(listener, result.second);
					dialog.show();
				}
				else if (result.first.length == 1)
				{
					final TypedPointer typedPointer = result.first[0];
					startVn(typedPointer);
				}
			}
			else if ("pb".equals(database)) //
			{
				final int idRolesets = cursor.getColumnIndex(PropBankContract.Lookup_PbExamples_X.ROLESETS);
				final String roleSets = cursor.getString(idRolesets);
				Log.d(TAG, "CLICK pb rolesets=" + roleSets);

				final Pair<TypedPointer[], CharSequence[]> result = makeData(roleSets);
				if (result.first.length > 1)
				{
					final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(final DialogInterface dialog, int which)
						{
							// which argument contains the index position of the selected item
							final TypedPointer typedPointer = result.first[which];
							startPb(typedPointer);
						}
					};

					final AlertDialog dialog = makeDialog(listener, result.second);
					dialog.show();
				}
				else if (result.first.length == 1)
				{
					final TypedPointer typedPointer = result.first[0];
					startPb(typedPointer);
				}
			}
			else if ("fn".equals(database)) //
			{
				final int idFrames = cursor.getColumnIndex(FrameNetContract.Lookup_FnSentences_X.FRAMES);
				final int idLexUnits = cursor.getColumnIndex(FrameNetContract.Lookup_FnSentences_X.LEXUNITS);
				final int idSentenceId = cursor.getColumnIndex(FrameNetContract.Lookup_FnSentences_X.SENTENCEID);
				final String frames = cursor.getString(idFrames);
				final String lexUnits = cursor.getString(idLexUnits);
				final String sentence = "sentence@" + cursor.getString(idSentenceId);
				Log.d(TAG, "CLICK fn frames=" + frames);
				Log.d(TAG, "CLICK fn lexunits=" + lexUnits);
				Log.d(TAG, "CLICK fn sentence=" + sentence);

				final Pair<TypedPointer[], CharSequence[]> result = makeData(frames, lexUnits, sentence);
				if (result.first.length > 1)
				{
					final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(final DialogInterface dialog, int which)
						{
							// which argument contains the index position of the selected item
							final TypedPointer typedPointer = result.first[which];
							startFn(typedPointer);
						}
					};

					final AlertDialog dialog = makeDialog(listener, result.second);
					dialog.show();
				}
				else if (result.first.length == 1)
				{
					final TypedPointer typedPointer = result.first[0];
					startFn(typedPointer);
				}
			}
		}
	}

	/**
	 * Start VerbNet
	 *
	 * @param typedPointer typed pointer
	 */
	private void startVn(final TypedPointer typedPointer)
	{
		final long targetId = typedPointer.id;
		Intent targetIntent = null;
		Parcelable pointer = null;

		// intent, type, pointer
		switch (typedPointer.type)
		{
			case 0:
				pointer = new VnClassPointer(targetId);
				targetIntent = new Intent(TextFragment.this.getActivity(), org.sqlunet.verbnet.browser.VnClassActivity.class);
				targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_VNCLASS);
				break;
		}

		// pass pointer
		assert targetIntent != null;
		targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
		targetIntent.setAction(ProviderArgs.ACTION_QUERY);

		// start
		startActivity(targetIntent);
	}

	/**
	 * Start PropBank
	 *
	 * @param typedPointer typed pointer
	 */
	private void startPb(final TypedPointer typedPointer)
	{
		final long targetId = typedPointer.id;
		Intent targetIntent = null;
		Parcelable pointer = null;

		// intent, type, pointer
		switch (typedPointer.type)
		{
			case 0:
				pointer = new PbRoleSetPointer(targetId);
				targetIntent = new Intent(TextFragment.this.getActivity(), org.sqlunet.propbank.browser.PbRoleSetActivity.class);
				targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PBROLESET);
				break;
		}

		// pass pointer
		assert targetIntent != null;
		targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
		targetIntent.setAction(ProviderArgs.ACTION_QUERY);

		// start
		startActivity(targetIntent);
	}

	/**
	 * Start FrameNet
	 *
	 * @param typedPointer typed pointer
	 */
	private void startFn(final TypedPointer typedPointer)
	{
		final long targetId = typedPointer.id;
		Intent targetIntent = null;
		Parcelable pointer = null;

		// intent, type, pointer
		switch (typedPointer.type)
		{
			case 0:
				pointer = new FnFramePointer(targetId);
				targetIntent = new Intent(TextFragment.this.getActivity(), org.sqlunet.framenet.browser.FnFrameActivity.class);
				targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNFRAME);
				break;
			case 1:
				pointer = new FnLexUnitPointer(targetId);
				targetIntent = new Intent(TextFragment.this.getActivity(), org.sqlunet.framenet.browser.FnLexUnitActivity.class);
				targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT);
				break;
			case 2:
				pointer = new FnSentencePointer(targetId);
				targetIntent = new Intent(TextFragment.this.getActivity(), org.sqlunet.framenet.browser.FnSentenceActivity.class);
				targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNSENTENCE);
				break;
		}

		// pass pointer
		assert targetIntent != null;
		targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
		targetIntent.setAction(ProviderArgs.ACTION_QUERY);

		// start
		startActivity(targetIntent);
	}

	/**
	 * Typed pointer
	 */
	private class TypedPointer
	{
		public final int type;
		public final long id;

		TypedPointer(int type, long id)
		{
			this.type = type;
			this.id = id;
		}
	}

	/**
	 * Make choice data
	 *
	 * @param concatChoices concatenated choices
	 * @return array of typed pointers and array of labels
	 */
	private Pair<TypedPointer[], CharSequence[]> makeData(final String... concatChoices)
	{
		final List<TypedPointer> typedPointers = new ArrayList<>();
		final List<CharSequence> labels = new ArrayList<>();
		final Pattern pattern = Pattern.compile('(' + "(?i)" + this.query + ')');

		int type = 0;
		for (String choices : concatChoices)
		{
			if (choices == null)
			{
				type++;
				continue;
			}
			final List<String> choiceList = Arrays.asList(choices.split(","));
			Collections.sort(choiceList);
			for (String choice : choiceList)
			{
				final String[] fields = choice.split("@");
				final String label = fields[0];
				int resId = -1;
				switch (type)
				{
					case 0:
						resId = R.drawable.roles;
						break;
					case 1:
						resId = R.drawable.role;
						break;
					case 2:
						resId = R.drawable.sentence;
						break;
				}
				final SpannableStringBuilder sb = new SpannableStringBuilder();
				appendImage(getActivity(), sb, resId);
				sb.append(' ');
				if (pattern.matcher(label).find())
				{
					append(sb, label, new ForegroundColorSpan(Color.BLACK), new StyleSpan(Typeface.BOLD));
				}
				else
				{
					append(sb, label, new ForegroundColorSpan(Color.GRAY));
				}

				labels.add(sb);
				typedPointers.add(new TypedPointer(type, Long.parseLong(fields[1])));
			}
			type++;
		}
		final TypedPointer[] typedPointersArray = typedPointers.toArray(new TypedPointer[0]);
		final CharSequence[] labelsArray = labels.toArray(new CharSequence[0]);
		return new Pair<>(typedPointersArray, labelsArray);
	}

	/**
	 * Make selection dialog
	 *
	 * @param listener click listener
	 * @param choices  choices
	 * @return dialog
	 */
	private AlertDialog makeDialog(final DialogInterface.OnClickListener listener, final CharSequence... choices)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// set the dialog characteristics
		builder.setTitle(R.string.title_activity_text_search);

		// data and listener
		builder.setItems(choices, listener);

		// get the dialog
		return builder.create();
	}

	/**
	 * Append text
	 *
	 * @param sb    spannable string builder
	 * @param text  text
	 * @param spans spans to apply
	 */
	static private void append(final SpannableStringBuilder sb, final CharSequence text, final Object... spans)
	{
		if (text == null || text.length() == 0)
		{
			return;
		}

		final int from = sb.length();
		sb.append(text);
		final int to = sb.length();

		for (final Object span : spans)
		{
			sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	/**
	 * Append Image
	 *
	 * @param context context
	 * @param sb      spannable string builder
	 * @param resId   resource id
	 */
	static private void appendImage(final Context context, final SpannableStringBuilder sb, final int resId)
	{
		append(sb, "\u0000", makeImageSpan(context, resId));
	}

	/**
	 * Make image span
	 *
	 * @param context context
	 * @param resId   res id
	 * @return image span
	 */
	@SuppressWarnings("deprecation")
	static private Object makeImageSpan(final Context context, final int resId)
	{
		Drawable drawable;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
		{
			drawable = context.getResources().getDrawable(resId);
		}
		else
		{
			drawable = context.getResources().getDrawable(resId, null);
		}
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		return new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);
	}
}
