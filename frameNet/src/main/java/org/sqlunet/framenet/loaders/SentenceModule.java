package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.Module;
import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences;
import org.sqlunet.framenet.style.FrameNetFactories;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

/**
 * A fragment representing a lexunit.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

/**
 * @author bbou
 */
public class SentenceModule extends BasicModule
{
	// query

	/**
	 * Sentence id
	 */
	private Long sentenceId;

	// text

	/**
	 * Sentence text
	 */
	private String sentenceText;

	/**
	 * Constructor
	 */
	public SentenceModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	public void init(final Parcelable query)
	{
		super.init(query);

		// get query
		if (query instanceof FnSentencePointer)
		{
			final FnSentencePointer sentenceQuery = (FnSentencePointer) query;
			this.sentenceId = sentenceQuery.getSentenceId();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.sentenceId != null)
		{
			// data
			sentence(this.sentenceId, node);
		}
	}

	// L O A D E R S

	private void sentence(final long sentenceId, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(Sentences.CONTENT_URI);
				final String[] projection = { //
						Sentences.SENTENCEID, //
						Sentences.TEXT, //
				};
				final String selection = Sentences.SENTENCEID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = {Long.toString(sentenceId)};
				final String sortOrder = null;
				return new CursorLoader(SentenceModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows"); //$NON-NLS-1$
				}
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					final int idSentenceId = cursor.getColumnIndex(Sentences.SENTENCEID);
					final int idText = cursor.getColumnIndex(Sentences.TEXT);

					// data
					SentenceModule.this.sentenceText = cursor.getString(idText);
					final long id = cursor.getLong(idSentenceId);
					Spanner.append(sb, SentenceModule.this.sentenceText, 0, FrameNetFactories.sentenceFactory);

					// attach result
					TreeFactory.addTextNode(parent, sb, SentenceModule.this.getContext());

					// layers
					layers_for_sentence(id, SentenceModule.this.sentenceText, parent);

					// expand
					TreeView.expand(parent, false);
				}
				else
				{
					parent.disable();
				}

				cursor.close();
			}

			@Override
			public void onLoaderReset(final Loader<Cursor> loader)
			{
				//
			}
		});
	}
}
