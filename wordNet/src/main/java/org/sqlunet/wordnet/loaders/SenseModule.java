package org.sqlunet.wordnet.loaders;

import android.os.Parcelable;

import org.sqlunet.HasWordId;
import org.sqlunet.model.TreeFactory;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;
import org.sqlunet.wordnet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Module for WordNet sense
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class SenseModule extends SynsetModule
{
	/**
	 * Word id
	 */
	@Nullable
	private Long wordId;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public SenseModule(@NonNull final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		super.unmarshal(pointer);

		this.wordId = null;
		if (pointer instanceof HasWordId)
		{
			final HasWordId wordPointer = (HasWordId) pointer;
			this.wordId = wordPointer.getWordId();
		}
	}

	@Override
	public void process(@NonNull final TreeNode parent)
	{
		if (this.wordId == null || this.synsetId == null)
		{
			return;
		}

		// sub nodes
		final TreeNode synsetNode = TreeFactory.newTextNode("Sense");
		final TreeNode membersNode = TreeFactory.newNode("Members", R.drawable.members);

		// attach result
		parent.addChildren(synsetNode, membersNode);

		// synset
		synset(this.synsetId, synsetNode, false);

		// members
		members(this.synsetId, membersNode);

		// morph
		morphs(this.wordId, parent);

		// special
		if (this.pos != null)
		{
			switch (this.pos)
			{
				case 'v':
					vFrames(this.synsetId, this.wordId, parent);
					vFrameSentences(this.synsetId, this.wordId, parent);
					break;

				case 'a':
					adjPosition(this.synsetId, this.wordId, parent);
					break;
			}
		}
		else
		{
			vFrames(this.synsetId, this.wordId, parent);
			vFrameSentences(this.synsetId, this.wordId, parent);
			adjPosition(this.synsetId, this.wordId, parent);
		}

		// links and samples
		final TreeNode linksNode = this.expand ? TreeFactory.newHotQueryNode("Links", R.drawable.ic_links, new LinksQuery(this.synsetId, this.wordId)).addTo(parent): TreeFactory.newQueryNode("Links", R.drawable.ic_links, new LinksQuery(this.synsetId, this.wordId)).addTo(parent);
		final TreeNode samplesNode = this.expand ? TreeFactory.newHotQueryNode("Samples", R.drawable.sample, new SamplesQuery(this.synsetId)).addTo(parent): TreeFactory.newQueryNode("Samples", R.drawable.sample, new SamplesQuery(this.synsetId)).addTo(parent);

		// fire event
		FireEvent.onQueryReady(linksNode);
		FireEvent.onQueryReady(samplesNode);
	}
}
