package org.sqlunet.propbank.loaders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.propbank.PbRoleSetPointer;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;

/**
 * Module for PropBank role sets from id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class RoleSetModule extends BaseModule
{
	/**
	 * Role set id
	 */
	@Nullable
	private Long roleSetId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public RoleSetModule(@NonNull final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.roleSetId = null;
		if (pointer instanceof PbRoleSetPointer)
		{
			final PbRoleSetPointer roleSetPointer = (PbRoleSetPointer) pointer;
			this.roleSetId = roleSetPointer.getId();
		}
		if (pointer instanceof HasXId)
		{
			final HasXId xIdPointer = (HasXId) pointer;
			if (xIdPointer.getXSources().contains("pb")) //
			{
				this.roleSetId = xIdPointer.getXClassId();
			}
		}
	}

	@Override
	public void process(@NonNull final TreeNode node)
	{
		if (this.roleSetId != null)
		{
			// data
			roleSet(RoleSetModule.this.roleSetId, node);
		}
		else
		{
			FireEvent.onNoResult(node, true);
		}
	}
}
