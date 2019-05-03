package org.sqlunet.propbank.loaders;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.sqlunet.HasWordId;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Module for PropBank role sets from word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class RoleSetFromWordModule extends BaseModule
{
	/**
	 * Word id
	 */
	@Nullable
	private Long wordId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public RoleSetFromWordModule(@NonNull final TreeFragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.wordId = null;
		if (pointer instanceof HasWordId)
		{
			final HasWordId wordPointer = (HasWordId) pointer;
			this.wordId = wordPointer.getWordId();
		}
	}

	@Override
	public void process(@NonNull final TreeNode node)
	{
		if (this.wordId != null && this.wordId != 0)
		{
			// data
			roleSets(this.wordId, node);
		}
		else
		{
			TreeFactory.setNoResult(node, true, false);
		}
	}
}
