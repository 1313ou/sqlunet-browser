package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.framenet.FnAnnoSetPointer;
import org.sqlunet.treeview.model.TreeNode;

public class AnnoSetModule extends BasicModule
{
	// query

	/**
	 * Pointer
	 */
	private Long annoSetId;

	// resources

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public AnnoSetModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	public void init(final Parcelable arguments)
	{
		super.init(arguments);

		// get query
		if (arguments instanceof FnAnnoSetPointer)
		{
			final FnAnnoSetPointer query = (FnAnnoSetPointer) arguments;
			this.annoSetId = query.getAnnoSetId();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.annoSetId != null)
		{
			// data
			annoset(this.annoSetId, node, true);
		}
	}
}
