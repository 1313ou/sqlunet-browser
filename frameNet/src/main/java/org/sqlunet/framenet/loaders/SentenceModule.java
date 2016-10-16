package org.sqlunet.framenet.loaders;

import org.sqlunet.browser.Module;
import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences;
import org.sqlunet.framenet.style.FrameNetFactories;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.view.TreeFactory;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

/**
 * A fragment representing a lexunit.
 *
 * @author Bernard Bou
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
	private Long sentenceid;

	// text

	/**
	 * Sentence text
	 */
	private String sentenceText;

	/**
	 * Constructor
	 */
	public SentenceModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sqlunet.framenet.browser.FrameModule#init(android.os.Parcelable)
	 */
	@Override
	public void init(final Parcelable query0)
	{
		super.init(query0);

		// get query
		if (query0 instanceof FnSentencePointer)
		{
			final FnSentencePointer sentenceQuery = (FnSentencePointer) query0;
			this.sentenceid = sentenceQuery.getSentenceId();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.sqlunet.framenet.browser.FrameModule#process()
	 */
	@SuppressWarnings("boxing")
	@Override
	public void process(final TreeNode node)
	{
		if (this.sentenceid != null)
		{
			// data
			sentence(this.sentenceid, node);
		}
	}

	// L O A D E R S

	private void sentence(final long sentenceid0, final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId0, final Bundle args)
			{
				final Uri uri = Uri.parse(Sentences.CONTENT_URI);
				final String[] projection = new String[] { //
						Sentences.SENTENCEID, //
						Sentences.TEXT, //
				};
				final String selection = Sentences.SENTENCEID + " = ?"; //$NON-NLS-1$
				final String[] selectionArgs = new String[] { Long.toString(sentenceid0) };
				final String sortOrder = null;
				return new CursorLoader(SentenceModule.this.getContext(), uri, projection, selection, selectionArgs, sortOrder);
			}

			@SuppressWarnings("synthetic-access")
			@Override
			public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor)
			{
				if (cursor.getCount() > 1)
					throw new RuntimeException("Unexpected number of rows"); //$NON-NLS-1$
				if (cursor.moveToFirst())
				{
					final SpannableStringBuilder sb = new SpannableStringBuilder();

					final int idSentenceId = cursor.getColumnIndex(Sentences.SENTENCEID);
					final int idText = cursor.getColumnIndex(Sentences.TEXT);

					// data
					SentenceModule.this.sentenceText = cursor.getString(idText);
					final long sentenceId = cursor.getLong(idSentenceId);

					Spanner.append(sb, SentenceModule.this.sentenceText, 0, FrameNetFactories.sentenceFactory);

					// attach result
					TreeFactory.addTextNode(parent, sb, SentenceModule.this.getContext());

					// layers
					layers_for_sentence(sentenceId, SentenceModule.this.sentenceText, parent);

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
			public void onLoaderReset(final Loader<Cursor> arg0)
			{
				//
			}
		});
	}
}
