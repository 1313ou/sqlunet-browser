/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.loaders;

import android.os.Parcelable;

import org.sqlunet.Has2SynsetId;
import org.sqlunet.Has2WordId;
import org.sqlunet.HasSynsetId;
import org.sqlunet.HasWordId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.syntagnet.SnCollocationPointer;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Module for SyntagNet collocation from id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class CollocationModule extends BaseModule
{
	/**
	 * Collocation id
	 */
	@Nullable
	private Long collocationId;

	/**
	 * Collocation id
	 */
	@Nullable
	private Long synset1Id;

	/**
	 * Collocation id
	 */
	@Nullable
	private Long synset2Id;

	/**
	 * Collocation id
	 */
	@Nullable
	private Long word1Id;

	/**
	 * Collocation id
	 */
	@Nullable
	private Long word2Id;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public CollocationModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);
		this.collocationId = null;
		this.synset1Id = null;
		this.synset2Id = null;
		this.word1Id = null;
		this.word2Id = null;
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.collocationId = null;
		if (pointer instanceof SnCollocationPointer)
		{
			final SnCollocationPointer collocationPointer = (SnCollocationPointer) pointer;
			this.collocationId = collocationPointer.getId();
		}
		if (pointer instanceof HasSynsetId)
		{
			final HasSynsetId pointer1 = (HasSynsetId) pointer;
			long id = pointer1.getSynsetId();
			this.synset1Id = id == -1 ? null : pointer1.getSynsetId();
		}
		if (pointer instanceof Has2SynsetId)
		{
			final Has2SynsetId pointer2 = (Has2SynsetId) pointer;
			long id = pointer2.getSynset2Id();
			this.synset2Id = id == -1 ? null : pointer2.getSynset2Id();
		}
		if (pointer instanceof HasWordId)
		{
			final HasWordId pointer1 = (HasWordId) pointer;
			long id = pointer1.getWordId();
			this.word1Id = id == -1 ? null : pointer1.getWordId();
		}
		if (pointer instanceof Has2WordId)
		{
			final Has2WordId pointer2 = (Has2WordId) pointer;
			long id = pointer2.getWord2Id();
			this.word2Id = id == -1 ? null : pointer2.getWord2Id();
		}
		/*
		if (pointer instanceof HasXId)
		{
			final HasXId xIdPointer = (HasXId) pointer;
			final String xSources = xIdPointer.getXSources();
			if (xSources == null || xSources.contains("sn")) //
			{
				this.collocationId = xIdPointer.getXClassId();
			}
		}
		*/
	}

	@Override
	public void process(@NonNull final TreeNode parent)
	{
		if (this.collocationId != null)
		{
			collocation(this.collocationId, parent);
		}
		else if (this.word1Id != null || this.word2Id != null || this.synset1Id != null || this.synset2Id != null)
		{
			collocations(this.word1Id, this.word2Id,  this.synset1Id,  this.synset2Id, parent);
		}
//		else if (this.word1Id != null)
//		{
//			collocations(this.word1Id, parent);
//		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}
}
