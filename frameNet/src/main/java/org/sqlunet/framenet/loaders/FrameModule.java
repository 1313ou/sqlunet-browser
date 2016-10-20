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
	private Long frameId;

	private Long luId;

	// C R E A T I O N

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public FrameModule(final Fragment fragment)
	{
		super(fragment);
	}


	@Override
	public void init(final Parcelable arguments)
	{
		super.init(arguments);

		unmarshal(arguments);
	}

	/**
	 * Unmarshal parceled query
	 *
	 * @param query parceled query
	 */
	private void unmarshal(final Parcelable query)
	{
		// get query
		if (query instanceof FnFramePointer)
		{
			final FnFramePointer pointer = (FnFramePointer) query;
			this.frameId = pointer.frameId;
			this.luId = null;
		}
		if (query instanceof HasXId)
		{
			final HasXId pointer = (HasXId) query;
			if (pointer.getxSources().contains("fn")) //$NON-NLS-1$
			{
				this.frameId = pointer.getxClassId();
				this.luId = pointer.getXInstanceId();
			}
		}
	}


	@Override
	public void process(final TreeNode node)
	{
		if (this.luId != null)
		{
			// data
			lexunit(this.luId, node, true, false);
		}
		else if (this.frameId != null)
		{
			// data
			frame(this.frameId, node);
		}
		else
		{
			node.disable();
		}
	}
}
