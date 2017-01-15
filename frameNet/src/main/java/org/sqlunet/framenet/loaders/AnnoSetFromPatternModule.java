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
public class AnnoSetFromPatternModule extends BaseModule
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
	public void process(final TreeNode node)
	{
		if (this.patternId != null)
		{
			// data
			annoSetsForPattern(this.patternId, node);
		}
	}
}
