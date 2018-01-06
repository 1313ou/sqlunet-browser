package org.sqlunet.framenet.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.framenet.R;
import org.sqlunet.framenet.loaders.FrameModule;
import org.sqlunet.framenet.loaders.LexUnitFromWordModule;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;

/**
 * A fragment representing a framenet search from a (word, pos)
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetFragment extends TreeFragment
{
	// static private final String TAG = "FrameNetF";

	/**
	 * Constructor
	 */
	public FrameNetFragment()
	{
		this.layoutId = R.layout.fragment_framenet;
		this.treeContainerId = R.id.data_contents;
		this.header = "FrameNet";
		this.iconId = R.drawable.framenet;
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
			assert this.treeView != null;
			final TreeNode root = this.treeView.getRoot();
			final TreeNode queryNode = root.getChildren().iterator().next();

			// module
			Module module = pointer instanceof HasXId ? new FrameModule(this) : new LexUnitFromWordModule(this);
			module.init(type, pointer);
			module.process(queryNode);
		}
	}
}
