package org.sqlunet.bnc.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.bnc.R;
import org.sqlunet.bnc.loaders.BaseModule;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;

/**
 * A fragment representing a lexunit.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BNCFragment extends TreeFragment
{
	// static private final String TAG = "BncF";

	/**
	 * Constructor
	 */
	public BNCFragment()
	{
		this.layoutId = R.layout.fragment_bnc;
		this.treeContainerId = R.id.data_contents;
		this.header = "BNC";
		this.iconId = R.drawable.bnc;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// query
		final Bundle args = getArguments();
		assert args != null;
		final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		if (args.containsKey(ProviderArgs.ARG_QUERYPOINTER))
		{
			// pointer
			final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);

			// root node
			final TreeNode root = this.treeView.getRoot();
			final TreeNode queryNode = root.getChildren().iterator().next();

			// module
			Module module = new BaseModule(this);
			module.init(type, pointer);
			module.process(queryNode);
		}
	}
}
