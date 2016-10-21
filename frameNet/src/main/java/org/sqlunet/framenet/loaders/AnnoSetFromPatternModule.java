package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.framenet.FnPatternPointer;
import org.sqlunet.treeview.model.TreeNode;

/**
 * AnnoSet from pattern module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class AnnoSetFromPatternModule extends BasicModule
{
	/**
	 * Pattern id
	 */
	private Long patternId;

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
	public void init(final Parcelable query)
	{
		super.init(query);

		// get query
		if (query instanceof FnPatternPointer)
		{
			final FnPatternPointer pointer = (FnPatternPointer) query;
			this.patternId = pointer.getId();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.patternId != null)
		{
			// data
			annoSetsForPattern(this.patternId, node);
		}
	}
}
