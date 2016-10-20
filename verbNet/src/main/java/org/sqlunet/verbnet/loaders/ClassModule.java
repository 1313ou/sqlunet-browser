package org.sqlunet.verbnet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.verbnet.VnClassPointer;

public class ClassModule extends BasicModule
{
	/**
	 * Query
	 */
	private Long classId;

	/**
	 * Constructor
	 *
	 * @param fragment host fragment
	 */
	public ClassModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	void unmarshal(final Parcelable query)
	{
		if (query instanceof VnClassPointer)
		{
			final VnClassPointer pointer = (VnClassPointer) query;
			this.classId = pointer.classId;
		}
		if (query instanceof HasXId)
		{
			final HasXId pointer = (HasXId) query;
			if (pointer.getXSources().contains("vn")) //$NON-NLS-1$
			{
				this.classId = pointer.getXClassId();
				// Long instanceId = pointer.getXInstanceId();
				// String sources = pointer.getXSources();
			}
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.classId != null)
		{
			// data
			vnClass(this.classId, node);
		}
		else
		{
			node.disable();
		}
	}
}
