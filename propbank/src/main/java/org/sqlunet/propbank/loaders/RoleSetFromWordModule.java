package org.sqlunet.propbank.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasWordId;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Module for rolesets
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class RoleSetFromWordModule extends BasicModule
{
	/**
	 * Query
	 */
	private Long wordid;

	/**
	 * Constructor
	 */
	public RoleSetFromWordModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	@Override
	void unmarshall(final Parcelable query0)
	{
		final HasWordId wordQuery = (HasWordId) query0;
		this.wordid = wordQuery.getWordId();
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.wordid != 0)
		{
			// data
			rolesets(this.wordid, node);
		}
		else
		{
			node.disable();
		}
	}
}
