package org.sqlunet.predicatematrix.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.settings.Settings.PMMode;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Module for predicate roles obtained from id
 *
 * @author Bernard Bou
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

	/*
	 * (non-Javadoc)
	 * @see org.sqlunet.propbank.browser.BasicModule#unmarshall(android.os.Parcelable)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see org.sqlunet.Module#process()
	 */
	@Override
	public void process(final TreeNode node)
	{
		if (this.pmroleid != null)
		{
			Displayer displayer;
			switch (this.mode)
			{
				default:
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
