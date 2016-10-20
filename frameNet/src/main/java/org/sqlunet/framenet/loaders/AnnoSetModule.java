package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.framenet.FnAnnoSetPointer;
import org.sqlunet.treeview.model.TreeNode;

/**
 * AnnoSet module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class AnnoSetModule extends BasicModule
{
	/**
	 * AnnoSet id
	 */
	private Long annoSetId;

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
			annoSet(this.annoSetId, node, true);
		}
	}
}
