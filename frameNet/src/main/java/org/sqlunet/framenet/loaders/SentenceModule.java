/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.loaders;

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
import org.sqlunet.view.TreeOp;
import org.sqlunet.view.TreeOpExecute;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import static org.sqlunet.view.TreeOp.TreeOpCode.NEWUNIQUE;
import static org.sqlunet.view.TreeOp.TreeOpCode.REMOVE;

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

	// View models

	private SqlunetViewTreeModel sentenceFromSentenceIdModel;

	/**
	 * Constructor
	 *
	 * @param fragment containing fragment
	 */
	public SentenceModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);

		// models
		makeModels();
	}

	/**
	 * Make view models
	 */
	private void makeModels()
	{
		this.sentenceFromSentenceIdModel = new ViewModelProvider(this.fragment).get("fn.sentence(sentenceid)", SqlunetViewTreeModel.class);
		this.sentenceFromSentenceIdModel.getData().observe(this.fragment, data -> new TreeOpExecute(this.fragment).exec(data));
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
		final ContentProviderSql sql = Queries.prepareSentence(sentenceId);
		final Uri uri = Uri.parse(FrameNetProvider.makeUri(sql.providerUri));
		this.sentenceFromSentenceIdModel.loadData(uri, sql, cursor -> sentenceCursorToTreeModel(cursor, parent));
	}

	private TreeOp[] sentenceCursorToTreeModel(@NonNull final Cursor cursor, @NonNull final TreeNode parent)
	{
		if (cursor.getCount() > 1)
		{
			throw new RuntimeException("Unexpected number of rows");
		}
		TreeOp[] changed;
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
			final TreeNode node = TreeFactory.makeTextNode(sb, false).addTo(parent);

			// layers
			layersForSentence(id, SentenceModule.this.sentenceText, parent);

			changed = TreeOp.seq(NEWUNIQUE, node);
		}
		else
		{
			TreeFactory.setNoResult(parent);
			changed = TreeOp.seq(REMOVE, parent);
		}

		cursor.close();
		return changed;
	}
}
