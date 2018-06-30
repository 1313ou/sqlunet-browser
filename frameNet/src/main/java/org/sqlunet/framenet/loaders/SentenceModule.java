package org.sqlunet.framenet.loaders;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.Module;
import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences;
import org.sqlunet.framenet.provider.FrameNetProvider;
import org.sqlunet.framenet.style.FrameNetFactories;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;
import org.sqlunet.view.TreeFactory;

/**
 * Sentence module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SentenceModule extends BaseModule
{
	/**
	 * Sentence id
	 */
	@Nullable
	private Long sentenceId;

	/**
	 * Sentence text
	 */
	private String sentenceText;

	/**
	 * Constructor
	 *
	 * @param fragment  containing fragment
	 */
	public SentenceModule(@NonNull final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.sentenceId = null;
		if (pointer instanceof FnSentencePointer)
		{
			final FnSentencePointer sentencePointer = (FnSentencePointer) pointer;
			this.sentenceId = sentencePointer.getId();
		}
	}

	@Override
	public void process(@NonNull final TreeNode node)
	{
		if (this.sentenceId != null)
		{
			// data
			sentence(this.sentenceId, node);
		}
	}

	// L O A D E R S

	/**
	 * Sentence
	 *
	 * @param sentenceId sentence id
	 * @param parent     parent node
	 */
	private void sentence(final long sentenceId, @NonNull final TreeNode parent)
	{
		getLoaderManager().restartLoader(++Module.loaderId, null, new LoaderCallbacks<Cursor>()
		{
			@NonNull
			@Override
			public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle loaderArgs)
			{
				final Uri uri = Uri.parse(FrameNetProvider.makeUri(Sentences.CONTENT_URI_TABLE));
				final String[] projection = { //
						Sentences.SENTENCEID, //
						Sentences.TEXT, //
				};
				final String selection = Sentences.SENTENCEID + " = ?";
				final String[] selectionArgs = {Long.toString(sentenceId)};
				final String sortOrder = null;
				assert SentenceModule.this.context != null;
				return new CursorLoader(SentenceModule.this.context, uri, projection, selection, selectionArgs, sortOrder);
			}

			@Override
			public void onLoadFinished(@NonNull final Loader<Cursor> loader, @NonNull final Cursor cursor)
			{
				if (cursor.getCount() > 1)
				{
					throw new RuntimeException("Unexpected number of rows");
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
					TreeFactory.addTextNode(parent, sb, SentenceModule.this.context);

					// layers
					layersForSentence(id, SentenceModule.this.sentenceText, parent);

					// fire event
					FireEvent.onResults(parent);
				}
				else
				{
					FireEvent.onNoResult(parent, true);
				}

				//cursor.close();
			}

			@Override
			public void onLoaderReset(@NonNull final Loader<Cursor> loader)
			{
				//
			}
		});
	}
}
