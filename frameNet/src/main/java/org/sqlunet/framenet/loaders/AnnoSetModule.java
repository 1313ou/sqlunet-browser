package org.sqlunet.framenet.loaders;

import org.sqlunet.framenet.FnAnnoSetPointer;
import org.sqlunet.treeview.model.TreeNode;

import android.app.Fragment;
import android.os.Parcelable;

public class AnnoSetModule extends BasicModule
{
	// query

	/**
	 * Pointer
	 */
	private Long annosetid;

	// resources

	/**
	 * Constructor
	 *
	 * @param fragment0
	 *            fragment
	 */
	public AnnoSetModule(final Fragment fragment0)
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
		if (arguments instanceof FnAnnoSetPointer)
		{
			final FnAnnoSetPointer query = (FnAnnoSetPointer) arguments;
			this.annosetid = query.getAnnoSetId();
		}
	}

	@SuppressWarnings("boxing")
	@Override
	public void process(final TreeNode node)
	{
		if (this.annosetid != null)
		{
			// data
			annoset(this.annosetid, node, true);
		}
	}
}
