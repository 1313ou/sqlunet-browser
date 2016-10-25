package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.framenet.FnLexUnitPointer;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Lex unit module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LexUnitModule extends FrameModule
{
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
			this.luId = lexUnitQuery.getId();
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
