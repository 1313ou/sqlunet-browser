package org.sqlunet.framenet.loaders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasWordId;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;

/**
 * Lex unit from word module
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LexUnitFromWordModule extends LexUnitModule
{
	/**
	 * Word id
	 */
	@Nullable
	private Long wordId;

	/**
	 * Pos
	 */
	@Nullable
	private Character pos;

	/**
	 * Constructor
	 *
	 * @param fragment  containing fragment
	 */
	public LexUnitFromWordModule(@NonNull final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		super.unmarshal(pointer);

		this.wordId = null;
		this.pos = null;
		if (pointer instanceof HasWordId)
		{
			final HasWordId wordPointer = (HasWordId) pointer;
			this.wordId = wordPointer.getWordId();
		}
		if (pointer instanceof HasPos)
		{
			final HasPos posPointer = (HasPos) pointer;
			this.pos = posPointer.getPos();
		}
	}

	@Override
	public void process(@NonNull final TreeNode node)
	{
		if (this.wordId != null && this.pos != null)
		{
			// data
			lexUnitsForWordAndPos(this.wordId, this.pos, node);
		}
		else
		{
			TreeFactory.setNoResult(node, true);
		}
	}
}
