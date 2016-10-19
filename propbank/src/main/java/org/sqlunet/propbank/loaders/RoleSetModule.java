package org.sqlunet.propbank.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Module for rolesets
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class RoleSetModule extends BasicModule
{
	// query

	private Long rolesetid;

	/**
	 * Constructor
	 */
	public RoleSetModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	@Override
	void unmarshall(final Parcelable query)
	{
		// get query
		if (query instanceof PbRoleSetPointer)
		{
			final PbRoleSetPointer pointer = (PbRoleSetPointer) query;
			this.rolesetid = pointer.rolesetid;
		}
		if (query instanceof HasXId)
		{
			final HasXId pointer = (HasXId) query;
			if (pointer.getXsources().contains("pb")) //$NON-NLS-1$
			{
				this.rolesetid = pointer.getXinstanceid();
				// Long classid = pointer.getXclassid();
				// String sources = pointer.getXsources();
			}
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.rolesetid != null)
		{
			// data
			roleset(RoleSetModule.this.rolesetid, node);
		}
		else
		{
			node.disable();
		}
	}
}
