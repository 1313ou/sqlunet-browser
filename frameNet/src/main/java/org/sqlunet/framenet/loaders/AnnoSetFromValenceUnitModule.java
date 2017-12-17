package org.sqlunet.framenet.loaders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.framenet.FnValenceUnitPointer;
import org.sqlunet.treeview.model.TreeNode;

/**
 * AnnoSet from valence unit module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class AnnoSetFromValenceUnitModule extends BaseModule
{
	/**
	 * Valence unit id
	 */
	@Nullable
	private Long vuId;

	/**
	 * Constructor
	 *
	 * @param fragment  containing fragment
	 */
	public AnnoSetFromValenceUnitModule(@NonNull final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.vuId = null;
		if (pointer instanceof FnValenceUnitPointer)
		{
			final FnValenceUnitPointer valenceUnitPointer = (FnValenceUnitPointer) pointer;
			this.vuId = valenceUnitPointer.getId();
		}
	}

	@Override
	public void process(@NonNull final TreeNode node)
	{
		if (this.vuId != null)
		{
			// data
			annoSetsForValenceUnit(this.vuId, node);
		}
	}
}
