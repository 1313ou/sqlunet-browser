package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasWordId;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeView;

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
	private Long wordId;

	/**
	 * Pos
	 */
	private Character pos;

	/**
	 * Constructor
	 */
	public LexUnitFromWordModule(final Fragment fragment)
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
	public void process(final TreeNode node)
	{
		if (this.wordId != null && this.pos != null)
		{
			// data
			lexUnitsForWordAndPos(this.wordId, this.pos, node);
		}
		else
		{
			TreeView.disable(node);
		}
	}
}
