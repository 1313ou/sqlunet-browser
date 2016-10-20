package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.framenet.FnValenceUnitPointer;
import org.sqlunet.treeview.model.TreeNode;

/**
 * AnnoSet from valence unit module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class AnnoSetFromValenceUnitModule extends BasicModule
{
	/**
	 * Valence unit id
	 */
	private Long vuId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public AnnoSetFromValenceUnitModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	public void init(final Parcelable arguments)
	{
		super.init(arguments);

		// get query
		if (arguments instanceof FnValenceUnitPointer)
		{
			final FnValenceUnitPointer query = (FnValenceUnitPointer) arguments;
			this.vuId = query.getId();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.vuId != null)
		{
			// data
			annoSetsForValenceUnit(this.vuId, node);
		}
	}
}
