package org.sqlunet.predicatematrix.loaders;

import android.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.Word;
import org.sqlunet.predicatematrix.settings.Settings.PMMode;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Module for predicate roles obtained from word
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateRoleFromWordModule extends BasicModule
{
	/**
	 * Query
	 */
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
	public PredicateRoleFromWordModule(final Fragment fragment, final PMMode mode)
	{
		super(fragment);
		this.mode = mode;
	}

	@Override
	void unmarshal(final Parcelable query)
	{
		if (query instanceof Word)
		{
			Word wordQuery = (Word) query;
			this.word = wordQuery.getWord();
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
