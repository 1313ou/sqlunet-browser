package org.sqlunet.wordnet.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasPos;
import org.sqlunet.HasSynsetId;
import org.sqlunet.treeview.model.TreeNode;

public class SynsetModule extends BasicModule
{
	/**
	 * Query
	 */
	Long synsetId;

	Character pos;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public SynsetModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	public void init(final Parcelable parcelable)
	{
		super.init(parcelable);

		// get arguments
		if (parcelable instanceof HasSynsetId)
		{
			final HasSynsetId query = (HasSynsetId) parcelable;
			this.synsetId = query.getSynsetId();
		}
		if (parcelable instanceof HasPos)
		{
			final HasPos query = (HasPos) parcelable;
			this.pos = query.getPos();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.synsetId != null && this.synsetId != 0)
		{
			// synset
			synset(this.synsetId, node, true);

			// members
			members(this.synsetId, node, true);

			// samples
			samples(this.synsetId, node);

			// semLinks
			semLinks(this.synsetId, node);

			// lexLinks
			this.lexLinks(this.synsetId, node);

			// special
			if (this.pos != null)
			{
				switch (this.pos)
				{
					case 'v':
						this.vFrames(this.synsetId, node);
						this.vFrameSentences(this.synsetId, node);
						break;

					case 'a':
						this.adjPosition(this.synsetId, node);
						break;
				}
			}
		}
	}
}
