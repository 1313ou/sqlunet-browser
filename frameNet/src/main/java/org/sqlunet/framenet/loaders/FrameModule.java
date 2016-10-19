package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.treeview.model.TreeNode;

/**
 * A module to retrieve frame
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameModule extends BasicModule
{
	/**
	 * Query
	 */
	private Long frameid;

	private Long luid;

	// C R E A T I O N

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public FrameModule(final Fragment fragment0)
	{
		super(fragment0);
	}


	@Override
	public void init(final Parcelable arguments)
	{
		super.init(arguments);

		unmarshall(arguments);
	}

	private void unmarshall(final Parcelable query)
	{
		// get query
		if (query instanceof FnFramePointer)
		{
			final FnFramePointer pointer = (FnFramePointer) query;
			this.frameid = pointer.frameid;
			this.luid = null;
		}
		if (query instanceof HasXId)
		{
			final HasXId pointer = (HasXId) query;
			if (pointer.getXsources().contains("fn")) //$NON-NLS-1$
			{
				this.frameid = pointer.getXclassid();
				this.luid = pointer.getXinstanceid();
			}
		}
	}


	@Override
	public void process(final TreeNode node)
	{
		if (this.luid != null)
		{
			// data
			lexunit(this.luid, node, true, false);
		}
		else if (this.frameid != null)
		{
			// data
			frame(this.frameid, node);
		}
		else
		{
			node.disable();
		}
	}
}
