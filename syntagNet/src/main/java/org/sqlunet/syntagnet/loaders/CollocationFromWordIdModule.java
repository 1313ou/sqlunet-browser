/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.loaders;

import android.os.Parcelable;

import org.sqlunet.HasWordId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Module for SyntagNet collocation from word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class CollocationFromWordIdModule extends BaseModule
{
	/**
	 * Word id
	 */
	@Nullable
	private Long wordId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public CollocationFromWordIdModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);
	}

	@Override
	protected boolean isTargetSecond(final long word1Id, final long word2Id)
	{
		return false;
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.wordId = null;
		if (pointer instanceof HasWordId)
		{
			final HasWordId wordPointer = (HasWordId) pointer;
			this.wordId = wordPointer.getWordId();
		}
	}

	@Override
	public void process(@NonNull final TreeNode parent)
	{
		if (this.wordId != null && this.wordId != 0)
		{
			// data
			collocations(this.wordId, parent);
		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}
}
