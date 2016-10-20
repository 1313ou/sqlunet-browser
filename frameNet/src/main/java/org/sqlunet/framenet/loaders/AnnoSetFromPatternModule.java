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
	private Long patternId;

	// resources

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public AnnoSetFromPatternModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	public void init(final Parcelable arguments)
	{
		super.init(arguments);

		// get query
		if (arguments instanceof FnPatternPointer)
		{
			final FnPatternPointer query = (FnPatternPointer) arguments;
			this.patternId = query.getId();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.patternId != null)
		{
			// data
			annosets_for_pattern(this.patternId, node);
		}
	}
}
