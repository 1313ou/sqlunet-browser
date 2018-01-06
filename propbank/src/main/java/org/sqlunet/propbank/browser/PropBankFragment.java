package org.sqlunet.propbank.browser;

import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.HasXId;
import org.sqlunet.browser.Module;
import org.sqlunet.browser.TreeFragment;
import org.sqlunet.propbank.R;
import org.sqlunet.propbank.loaders.RoleSetFromWordModule;
import org.sqlunet.propbank.loaders.RoleSetModule;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.treeview.model.TreeNode;

/**
 * A fragment representing a PropBank search
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropBankFragment extends TreeFragment
{
	// static private final String TAG = "PropBankF";

	/**
	 * Constructor
	 */
	public PropBankFragment()
	{
		this.layoutId = R.layout.fragment_propbank;
		this.treeContainerId = R.id.data_contents;
		this.header = "PropBank";
		this.iconId = R.drawable.propbank;
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
			final Module module = (pointer instanceof HasXId) ? new RoleSetModule(this) : new RoleSetFromWordModule(this);
			module.init(type, pointer);
			module.process(queryNode);
		}
	}
}
