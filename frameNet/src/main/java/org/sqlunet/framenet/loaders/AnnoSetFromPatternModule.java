package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.framenet.FnPatternPointer;
import org.sqlunet.treeview.model.TreeNode;

public class AnnoSetFromPatternModule extends BasicModule
{
	// query

	/**
	 * Pointer
	 */
	private Long patternid;

	// resources

	/**
	 * Constructor
	 *
	 * @param fragment0 fragment
	 */
	public AnnoSetFromPatternModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sqlunet.Module#init(android.os.Parcelable)
	 */
	@Override
	public void init(final Parcelable arguments)
	{
		super.init(arguments);

		// get query
		if (arguments instanceof FnPatternPointer)
		{
			final FnPatternPointer query = (FnPatternPointer) arguments;
			this.patternid = query.getId();
		}
	}

	@SuppressWarnings("boxing")
	@Override
	public void process(final TreeNode node)
	{
		if (this.patternid != null)
		{
			// data
			annosets_for_pattern(this.patternid, node);
		}
	}
}
