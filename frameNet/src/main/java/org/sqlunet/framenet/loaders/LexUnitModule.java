package org.sqlunet.framenet.loaders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
	@Nullable
	private Long luId;

	/**
	 * Constructor
	 *
	 * @param fragment  containing fragment
	 */
	public LexUnitModule(@NonNull final Fragment fragment)
	{
		super(fragment);
	}


	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		super.unmarshal(pointer);

		this.luId = null;
		if (pointer instanceof FnLexUnitPointer)
		{
			final FnLexUnitPointer lexUnitPointer = (FnLexUnitPointer) pointer;
			this.luId = lexUnitPointer.getId();
		}
	}


	@Override
	public void process(@NonNull final TreeNode node)
	{
		if (this.luId != null)
		{
			// data
			lexUnit(this.luId, node, true, false);
		}
	}
}
