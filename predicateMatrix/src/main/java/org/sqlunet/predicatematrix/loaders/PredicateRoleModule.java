package org.sqlunet.predicatematrix.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.settings.Settings.PMMode;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Module for predicate roles obtained from id
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateRoleModule extends BasicModule
{
	/**
	 * Query id
	 */
	private Long pmroleid;

	/**
	 * View mode
	 */
	private final PMMode mode;

	/**
	 * Constructor
	 */
	public PredicateRoleModule(final Fragment fragment0, final PMMode mode)
	{
		super(fragment0);
		this.mode = mode;
	}

	@Override
	void unmarshall(final Parcelable query)
	{
		// get query
		if (query instanceof PmRolePointer)
		{
			final PmRolePointer pointer = (PmRolePointer) query;
			this.pmroleid = pointer.getRoleId();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.pmroleid != null)
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
			fromRoleId(PredicateRoleModule.this.pmroleid, node, displayer);
		}
	}
}
