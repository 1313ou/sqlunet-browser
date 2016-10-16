package org.sqlunet.verbnet.loaders;

import org.sqlunet.HasXId;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.verbnet.VnClassPointer;

import android.app.Fragment;
import android.os.Parcelable;

public class ClassModule extends BasicModule
{
	/**
	 * Query
	 */
	private Long classid;

	/**
	 * Constructor
	 *
	 * @param fragment0
	 *            host fragment
	 */
	public ClassModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sqlunet.verbnet.browser.BasicModule#unmarshall(android.os.Parcelable)
	 */
	@Override
	void unmarshall(final Parcelable query)
	{
		if (query instanceof VnClassPointer)
		{
			final VnClassPointer pointer = (VnClassPointer) query;
			this.classid = pointer.classid;
		}
		if (query instanceof HasXId)
		{
			final HasXId pointer = (HasXId) query;
			if (pointer.getXsources().contains("vn")) //$NON-NLS-1$
			{
				this.classid = pointer.getXclassid();
				// Long instanceid = pointer.getXinstanceid();
				// String sources = pointer.getXsources();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sqlunet.Module#process()
	 */
	@SuppressWarnings("boxing")
	@Override
	public void process(final TreeNode node)
	{
		if (this.classid != null)
		{
			// data
			vnclasses(this.classid, node);
		}
		else
		{
			node.disable();
		}
	}
}
