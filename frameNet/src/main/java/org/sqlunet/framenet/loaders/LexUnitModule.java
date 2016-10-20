package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.treeview.model.TreeNode;

/**
 * A fragment representing a lexUnit.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
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
	private Long luId;

	/**
	 * Constructor
	 */
	public LexUnitModule(final Fragment fragment)
	{
		super(fragment);
	}


	@Override
	public void init(final Parcelable query)
	{
		super.init(query);

		// get query
		this.luId = null;
		if (query instanceof FnLexUnitPointer)
		{
			final FnLexUnitPointer lexUnitQuery = (FnLexUnitPointer) query;
			this.luId = lexUnitQuery.getLuId();
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
	}
}
