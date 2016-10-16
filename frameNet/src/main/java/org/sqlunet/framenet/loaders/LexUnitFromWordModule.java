package org.sqlunet.framenet.loaders;

import org.sqlunet.HasPos;
import org.sqlunet.HasWordId;
import org.sqlunet.treeview.model.TreeNode;

import android.app.Fragment;
import android.os.Parcelable;

/**
 * A fragment representing a lexunit.
 *
 * @author Bernard Bou
 */
public class LexUnitFromWordModule extends LexUnitModule
{
	/**
	 * Query
	 */
	private Long wordid;

	private Character pos;

	/**
	 * Constructor
	 */
	public LexUnitFromWordModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sqlunet.framenet.browser.LexUnitModule#init(android.os.Parcelable)
	 */
	@Override
	public void init(final Parcelable query0)
	{
		super.init(query0);

		// get query
		this.wordid = null;
		this.pos = null;
		if (query0 instanceof HasWordId)
		{
			final HasWordId wordQuery = (HasWordId) query0;
			this.wordid = wordQuery.getWordId();
		}
		if (query0 instanceof HasPos)
		{
			final HasPos posQuery = (HasPos) query0;
			this.pos = posQuery.getPos();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.sqlunet.framenet.browser.LexUnitModule#process()
	 */
	@SuppressWarnings("boxing")
	@Override
	public void process(final TreeNode node)
	{
		if (this.wordid != null && this.pos != null)
		{
			// data
			lexunits_for_word_pos(this.wordid, this.pos, node);
		}
		else
		{
			node.disable();
		}
	}
}
