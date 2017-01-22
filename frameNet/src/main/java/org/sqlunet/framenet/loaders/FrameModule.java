package org.sqlunet.framenet.loaders;

import android.support.v4.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;

/**
 * Frame module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameModule extends BaseModule
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
	protected void unmarshal(final Parcelable pointer)
	{
		this.frameId = null;
		this.luId = null;
		if (pointer instanceof FnFramePointer)
		{
			final FnFramePointer framePointer = (FnFramePointer) pointer;
			this.frameId = framePointer.getId();
		}
		if (pointer instanceof HasXId)
		{
			final HasXId xIdPointer = (HasXId) pointer;
			if (xIdPointer.getXSources().contains("fn")) //
			{
				this.frameId = xIdPointer.getXClassId();
				this.luId = xIdPointer.getXMemberId();
			}
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.luId != null)
		{
			lexUnit(this.luId, node, true, false);
		}
		else if (this.frameId != null)
		{
			frame(this.frameId, node);
		}
		else
		{
			FireEvent.onNoResult(node, true);
		}
	}
}
