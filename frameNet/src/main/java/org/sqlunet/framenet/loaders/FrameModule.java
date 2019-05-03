package org.sqlunet.framenet.loaders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.model.TreeNode;

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
	@Nullable
	private Long frameId;

	/**
	 * Lex unit id
	 */
	@Nullable
	private Long luId;

	/**
	 * Constructor
	 *
	 * @param fragment  containing fragment
	 */
	public FrameModule(@NonNull final TreeFragment fragment)
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
	public void process(@NonNull final TreeNode node)
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
			TreeFactory.setNoResult(node, true, false);
		}
	}
}
