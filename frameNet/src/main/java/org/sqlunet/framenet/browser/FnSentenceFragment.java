package org.sqlunet.framenet.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.framenet.R;
import org.sqlunet.framenet.loaders.SentenceModule;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;

/**
 * A fragment representing a sentence.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnSentenceFragment extends TreeFragment
{
	// static private final String TAG = "FNSentenceF";

	/**
	 * Constructor
	 */
	public FnSentenceFragment()
	{
		this.layoutId = R.layout.fragment_fnsentence;
		this.treeContainerId = R.id.data_contents;
		this.header = "Sentence";
		this.iconId = R.drawable.sentence;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// query
		final Bundle args = getArguments();
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);

			// root node
			final TreeNode root = this.treeView.getRoot();
			final TreeNode queryNode = root.getChildren().iterator().next();

			// module
			final Module module = new SentenceModule(this);
			module.init(type, pointer);
			module.process(queryNode);
		}
	}
}
