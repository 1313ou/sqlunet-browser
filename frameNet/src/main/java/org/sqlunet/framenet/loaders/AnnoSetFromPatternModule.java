/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.loaders;

import android.os.Parcelable;

import org.sqlunet.browser.TreeFragment;
import org.sqlunet.framenet.FnPatternPointer;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * AnnoSet from pattern module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class AnnoSetFromPatternModule extends BaseModule
{
	/**
	 * Pattern id
	 */
	@Nullable
	private Long patternId;

	/**
	 * Constructor
	 *
	 * @param fragment containing fragment
	 */
	public AnnoSetFromPatternModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.patternId = null;
		if (pointer instanceof FnPatternPointer)
		{
			final FnPatternPointer patternPointer = (FnPatternPointer) pointer;
			this.patternId = patternPointer.getId();
		}
	}

	@Override
	public void process(@NonNull final TreeNode node)
	{
		if (this.patternId != null)
		{
			// data
			annoSetsForPattern(this.patternId, node);
		}
	}
}
