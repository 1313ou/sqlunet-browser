package org.sqlunet.framenet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasWordId;
import org.sqlunet.treeview.model.TreeNode;

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
	public void init(final Parcelable query)
	{
		super.init(query);

		// get query
		this.wordId = null;
		this.pos = null;
		if (query instanceof HasWordId)
		{
			final HasWordId wordQuery = (HasWordId) query;
			this.wordId = wordQuery.getWordId();
		}
		if (query instanceof HasPos)
		{
			final HasPos posQuery = (HasPos) query;
			this.pos = posQuery.getPos();
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
			node.disable();
		}
	}
}
