package org.sqlunet.verbnet.loaders;

import android.support.v4.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.verbnet.VnClassPointer;
import org.sqlunet.view.FireEvent;

/**
 * VerbNet class module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ClassModule extends BaseModule
{
	/**
	 * Query
	 */
	private Long classId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public ClassModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.classId = null;
		if (pointer instanceof VnClassPointer)
		{
			final VnClassPointer classPointer = (VnClassPointer) pointer;
			this.classId = classPointer.getId();
		}
		if (pointer instanceof HasXId)
		{
			final HasXId xIdPointer = (HasXId) pointer;
			if (xIdPointer.getXSources().contains("vn")) //
			{
				this.classId = xIdPointer.getXClassId();
				// Long xMemberId = pointer.getXMemberId();
				// String xSources = pointer.getXSources();
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
			FireEvent.onNoResult(node, true);
		}
	}
}
