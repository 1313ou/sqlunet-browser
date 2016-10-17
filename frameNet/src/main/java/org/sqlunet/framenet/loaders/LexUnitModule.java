package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.treeview.model.TreeNode;

/**
 * A fragment representing a lexunit.
 *
 * @author Bernard Bou
 */

/**
 * @author bbou
 */
public class LexUnitModule extends FrameModule
{
	// Query

	/**
	 * LuId
	 */
	private Long luid;

	/**
	 * Constructor
	 */
	public LexUnitModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sqlunet.framenet.browser.FrameModule#init(android.os.Parcelable)
	 */
	@Override
	public void init(final Parcelable query0)
	{
		super.init(query0);

		// get query
		this.luid = null;
		if (query0 instanceof FnLexUnitPointer)
		{
			final FnLexUnitPointer lexUnitQuery = (FnLexUnitPointer) query0;
			this.luid = lexUnitQuery.getLuId();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sqlunet.framenet.browser.FrameModule#process()
	 */
	@Override
	public void process(final TreeNode node)
	{
		if (this.luid != null)
		{
			// data
			lexunit(this.luid, node, true, false);
		}
	}
}
