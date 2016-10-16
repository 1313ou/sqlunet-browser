package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.framenet.FnValenceUnitPointer;
import org.sqlunet.treeview.model.TreeNode;

public class AnnoSetFromValenceUnitModule extends BasicModule
{
	// query

	/**
	 * Pointer
	 */
	private Long vuid;

	/**
	 * Constructor
	 *
	 * @param fragment0 fragment
	 */
	public AnnoSetFromValenceUnitModule(final Fragment fragment0)
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
		if (arguments instanceof FnValenceUnitPointer)
		{
			final FnValenceUnitPointer query = (FnValenceUnitPointer) arguments;
			this.vuid = query.getId();
		}
	}

	@SuppressWarnings("boxing")
	@Override
	public void process(final TreeNode node)
	{
		if (this.vuid != null)
		{
			// data
			annosets_for_valenceunit(this.vuid, node);
		}
	}
}
