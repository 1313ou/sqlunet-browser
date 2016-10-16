package org.sqlunet.predicatematrix.loaders;

import org.sqlunet.Word;
import org.sqlunet.predicatematrix.settings.Settings.PMMode;
import org.sqlunet.treeview.model.TreeNode;

import android.app.Fragment;
import android.os.Parcelable;

/**
 * Module for predicate roles obtained from word
 *
 * @author Bernard Bou
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
	public PredicateRoleFromWordModule(final Fragment fragment0, final PMMode mode)
	{
		super(fragment0);
		this.mode = mode;
	}

	/*
	 * (non-Javadoc)
	 * @see org.sqlunet.propbank.browser.BasicModule#unmarshall(android.os.Parcelable)
	 */
	@Override
	void unmarshall(final Parcelable query0)
	{
		if (query0 instanceof Word)
		{
			Word wordQuery = (Word) query0;
			this.word = wordQuery.getWord();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.sqlunet.Module#process()
	 */
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
