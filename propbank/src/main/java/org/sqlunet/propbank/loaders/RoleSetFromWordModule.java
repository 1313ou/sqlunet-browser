package org.sqlunet.propbank.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasWordId;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;

/**
 * Module for PropBank role sets from word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class RoleSetFromWordModule extends BasicModule
{
	/**
	 * Word id
	 */
	private Long wordId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public RoleSetFromWordModule(final Fragment fragment)
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
	public void process(final TreeNode node)
	{
		if (this.wordId != 0)
		{
			// data
			roleSets(this.wordId, node);
		}
		else
		{
			FireEvent.onNoResult(node, true);
		}
	}
}
