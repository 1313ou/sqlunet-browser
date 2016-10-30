package org.sqlunet.predicatematrix.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.settings.Settings.PMMode;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Module for predicate roles obtained from pm role id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateRoleModule extends BasicModule
{
	/**
	 * QueryData id
	 */
	private Long pmRoleId;

	/**
	 * View mode
	 */
	private final PMMode mode;

	/**
	 * Constructor
	 */
	public PredicateRoleModule(final Fragment fragment, final PMMode mode)
	{
		super(fragment);
		this.mode = mode;
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.pmRoleId = null;
		if (pointer instanceof PmRolePointer)
		{
			final PmRolePointer rolePointer = (PmRolePointer) pointer;
			this.pmRoleId = rolePointer.getId();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.pmRoleId != null)
		{
			Displayer displayer = null;
			switch (this.mode)
			{
				case ROWS:
					displayer = new DisplayerUngrouped();
					break;
				case ROWS_GROUPED_BY_SYNSET:
					displayer = new DisplayerBySynset();
					break;
				case ROLES:
				case ROWS_GROUPED_BY_ROLE:
					displayer = new DisplayerByPmRole();
					break;
			}
			fromRoleId(PredicateRoleModule.this.pmRoleId, node, displayer);
		}
	}
}
