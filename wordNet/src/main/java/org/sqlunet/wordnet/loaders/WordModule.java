package org.sqlunet.wordnet.loaders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasWordId;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.view.FireEvent;
import org.sqlunet.view.TreeFactory;

/**
 * Module for WordNet word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class WordModule extends BaseModule
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
	public WordModule(@NonNull final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
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
		if (this.wordId != null && this.wordId != 0)
		{
			// sub nodes
			final TreeNode wordNode = TreeFactory.newTextNode("Word", this.context);
			parent.addChild(wordNode);

			// word
			word(this.wordId, wordNode, false);

			// senses sub node
			//final TreeNode sensesNode = TreeFactory.newTextNode("Senses", this.context);
			//parent.addChild(sensesNode);

			// senses
			// senses(this.wordId, sensesNode);
			senses(this.wordId, wordNode);

			FireEvent.onResults(parent);
		}
	}
}
