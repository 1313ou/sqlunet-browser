package org.sqlunet.framenet.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import org.sqlunet.browser.SqlunetViewTreeModel;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.framenet.FnSentencePointer;
import org.sqlunet.framenet.provider.FrameNetContract.Sentences;
import org.sqlunet.framenet.provider.FrameNetProvider;
import org.sqlunet.framenet.style.FrameNetFactories;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.style.Spanner;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

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
	 * @param fragment containing fragment
	 */
	public SentenceModule(@NonNull final TreeFragment fragment)
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
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(Sentences.CONTENT_URI_TABLE));
		final String[] projection = { //
				Sentences.SENTENCEID, //
				Sentences.TEXT, //
		};
		final String selection = Sentences.SENTENCEID + " = ?";
		final String[] selectionArgs = {Long.toString(sentenceId)};
		final String sortOrder = null;

		final String tag = "fn.sentence(sentenceid)";
		final SqlunetViewTreeModel model = ViewModelProviders.of(this.fragment).get(tag, SqlunetViewTreeModel.class);
		model.loadData(uri, projection, selection, selectionArgs, sortOrder, cursor -> sentenceCursorToTreeModel(cursor, parent));
		model.getData().observe(this.fragment, data -> new FireEvent(this.fragment).live(data));
	}

	private TreeNode[] sentenceCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}
		TreeNode[] changed;
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
			final TreeNode node = TreeFactory.addTextNode(parent, sb);

			// layers
			layersForSentence(id, SentenceModule.this.sentenceText, parent);

			changed = new TreeNode[]{parent, node};
		}
		else
		{
			TreeFactory.setNoResult(parent, true);
			changed = new TreeNode[]{parent};
		}

		cursor.close();
		return changed;
	}
}
