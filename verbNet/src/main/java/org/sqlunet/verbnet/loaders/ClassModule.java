/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.loaders;

import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.verbnet.VnClassPointer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * VerbNet class module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ClassModule extends BaseModule
{
	/**
	 * Query
	 */
	@Nullable
	private Long classId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public ClassModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.classId = null;
		if (pointer instanceof VnClassPointer)
		{
			final VnClassPointer classPointer = (VnClassPointer) pointer;
			this.classId = classPointer.getId();
		}
		if (pointer instanceof HasXId)
		{
			final HasXId xIdPointer = (HasXId) pointer;
			if (xIdPointer.getXSources().contains("vn")) //
			{
				this.classId = xIdPointer.getXClassId();
				// Long xMemberId = pointer.getXMemberId();
				// String xSources = pointer.getXSources();
			}
		}
	}

	@Override
	public void process(@NonNull final TreeNode parent)
	{
		if (this.classId != null)
		{
			// data
			vnClass(this.classId, parent);
		}
		else
		{
			TreeFactory.setNoResult(parent);
		}
	}
}
