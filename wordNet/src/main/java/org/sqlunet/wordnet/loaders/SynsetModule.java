package org.sqlunet.wordnet.loaders;

import org.sqlunet.HasPos;
import org.sqlunet.HasSynsetId;
import org.sqlunet.treeview.model.TreeNode;

import android.app.Fragment;
import android.os.Parcelable;

public class SynsetModule extends BasicModule
{
	/**
	 * Query
	 */
	Long synsetid;

	Character pos;

	/**
	 * Constructor
	 *
	 * @param fragment0 fragment
	 */
	public SynsetModule(final Fragment fragment0)
	{
		super(fragment0);
	}

	@Override
	public void init(final Parcelable parcelable)
	{
		super.init(parcelable);

		// get arguments
		if (parcelable instanceof HasSynsetId)
		{
			final HasSynsetId query = (HasSynsetId) parcelable;
			this.synsetid = query.getSynsetId();
		}
		if (parcelable instanceof HasPos)
		{
			final HasPos query = (HasPos) parcelable;
			this.pos = query.getPos();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sqlunet.Module#data()
	 */
	@SuppressWarnings("boxing")
	@Override
	public void process(final TreeNode node)
	{
		if (this.synsetid != null && this.synsetid != 0)
		{
			// synset
			synset(this.synsetid, node, true);

			// members
			members(this.synsetid, node, true);

			// samples
			samples(this.synsetid, node);

			// semlinks
			semlinks(this.synsetid, node);

			// lexlinks
			this.lexlinks(this.synsetid, node);

			// special
			if (this.pos != null)
			{
				switch (this.pos)
				{
				case 'v':
					this.vframes(this.synsetid, node);
					this.vframesentences(this.synsetid, node);
					break;

				case 'a':
					this.adjposition(this.synsetid, node);
					break;
				}
			}
		}
	}
}
