package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Frame module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameModule extends BasicModule
{
	/**
	 * Frame id
	 */
	private Long frameId;

	/**
	 * Lex unit id
	 */
	private Long luId;

	/**
	 * Constructor
	 */
	public FrameModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	public void init(final Parcelable query)
	{
		super.init(query);

		unmarshal(query);
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
			if (pointer.getXSources().contains("fn")) //
			{
				this.frameId = pointer.getXClassId();
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
			lexUnit(this.luId, node, true, false);
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
