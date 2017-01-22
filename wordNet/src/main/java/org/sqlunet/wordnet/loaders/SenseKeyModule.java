package org.sqlunet.wordnet.loaders;

import android.support.v4.app.Fragment;
import android.os.Parcelable;

import org.sqlunet.HasSenseKey;
import org.sqlunet.treeview.model.TreeNode;

/**
 * Module for WordNet sense (from sensekey)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */

public class SenseKeyModule extends BaseModule
{
	/**
	 * Sense key
	 */
	private String senseKey;

	/**
	 * Constructor
	 *
	 * @param fragment fragment
	 */
	public SenseKeyModule(final Fragment fragment)
	{
		super(fragment);
	}

	@Override
	protected void unmarshal(final Parcelable pointer)
	{
		this.senseKey = null;
		if (pointer instanceof HasSenseKey)
		{
			final HasSenseKey sensePointer = (HasSenseKey) pointer;
			this.senseKey = sensePointer.getSenseKey();
		}
	}

	@Override
	public void process(final TreeNode parent)
	{
		if (this.senseKey == null || this.senseKey.isEmpty())
		{
			return;
		}

		// synset
		sense(this.senseKey, parent);
	}
}
