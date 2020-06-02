/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.loaders;

import android.os.Parcelable;

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
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public CollocationModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);
		this.collocationId = null;
	}

	@Override
	protected boolean isTargetSecond(final long word1Id, final long word2Id)
	{
		return false;
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
	}

	@Override
	public void process(@NonNull final TreeNode parent)
	{
		if (this.collocationId != null)
		{
			collocation(this.collocationId, parent);
		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}
}
