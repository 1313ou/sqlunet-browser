/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.framenet.loaders;

import android.os.Parcelable;

import org.sqlunet.browser.TreeFragment;
import org.sqlunet.framenet.FnAnnoSetPointer;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * AnnoSet module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class AnnoSetModule extends BaseModule
{
	/**
	 * AnnoSet id
	 */
	@Nullable
	private Long annoSetId;

	/**
	 * Constructor
	 *
	 * @param fragment containing fragment
	 */
	public AnnoSetModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.annoSetId = null;
		if (pointer instanceof FnAnnoSetPointer)
		{
			final FnAnnoSetPointer annoSetPointer = (FnAnnoSetPointer) pointer;
			this.annoSetId = annoSetPointer.getId();
		}
	}

	@Override
	public void process(@NonNull final TreeNode node)
	{
		if (this.annoSetId != null)
		{
			// data
			annoSet(this.annoSetId, node, true);
		}
	}
}
