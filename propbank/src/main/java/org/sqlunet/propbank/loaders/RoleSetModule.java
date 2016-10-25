package org.sqlunet.propbank.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Module for PropBank role sets from id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class RoleSetModule extends BasicModule
{
	/**
	 * Role set id
	 */
	private Long roleSetId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public RoleSetModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	void unmarshal(final Parcelable query)
	{
		// get query
		if (query instanceof PbRoleSetPointer)
		{
			final PbRoleSetPointer pointer = (PbRoleSetPointer) query;
			this.roleSetId = pointer.getId();
		}
		if (query instanceof HasXId)
		{
			final HasXId pointer = (HasXId) query;
			if (pointer.getXSources().contains("pb")) //
			{
				this.roleSetId = pointer.getXInstanceId();
				// Long classId = pointer.getXClassId();
				// String sources = pointer.getXSources();
			}
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.roleSetId != null)
		{
			// data
			roleSet(RoleSetModule.this.roleSetId, node);
		}
		else
		{
			node.disable();
		}
	}
}
