/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.predicatematrix.loaders;

import android.os.Parcelable;

import org.sqlunet.Word;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.predicatematrix.settings.Settings.PMMode;
import org.sqlunet.treeview.model.TreeNode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Module for predicate roles obtained from word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateRoleFromWordModule extends BaseModule
{
	/**
	 * Query
	 */
	@Nullable
	private String word;

	/**
	 * View mode
	 */
	private final PMMode mode;

	/**
	 * Constructor
	 *
	 * @param mode predicatematrix mode
	 */
	public PredicateRoleFromWordModule(@NonNull final TreeFragment fragment, final PMMode mode)
	{
		super(fragment);
		this.mode = mode;
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.word = null;
		if (pointer instanceof Word)
		{
			Word word = (Word) pointer;
			this.word = word.getWord();
		}
	}

	@Override
	public void process(final TreeNode node)
	{
		if (this.word != null)
		{
			switch (this.mode)
			{
				case ROLES:
					fromWordGrouped(this.word, node);
					break;

				case ROWS_GROUPED_BY_ROLE:
					fromWord(this.word, node, new DisplayerByPmRole());
					break;

				case ROWS_GROUPED_BY_SYNSET:
					fromWord(this.word, node, new DisplayerBySynset());
					break;

				case ROWS:
					fromWord(this.word, node, new DisplayerUngrouped());
					break;
			}
		}
	}
}
